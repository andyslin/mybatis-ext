package org.autumn.mybatis.codegenerate.service.impl;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.sql.DataSource;

import org.autumn.mybatis.codegenerate.domain.Module;
import org.autumn.mybatis.codegenerate.domain.Project;
import org.autumn.mybatis.codegenerate.service.CodeGenerateService;
import org.autumn.mybatis.common.meta.domain.Query;
import org.autumn.mybatis.common.meta.service.DatabaseMetaService;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 代码生成服务的Freemarker实现<br>
 *
 * <pre>
 *     循环读取templatepath（默认src/main/resources/META-INF/codetemplate/）下的文件，将模板变量逐个应用到这些模板文件，然后生成新文件
 *     在文件中可以使用的模板变量有（实际使用时可以根据需要修改模板文件、增删模板文件、添加子目录等）：
 *
 *     1. table 表示一个查询的相关信息 {@link Query}
 *     2. project 表示一个工程的信息 {@link Project}
 *     3. module 表示工程中一个业务模块的信息 {@link Module}
 *     4. 自定义变量，由接口中的参数传入 {@link CodeGenerateService#generate(Project, Module, Map)}
 *
 *     此外，由于路径和文件名不能使用Freemarker的语法，因此统一使用Demo、demo来表示模块名
 * </pre>
 */
public class CodeGenerateServiceImpl implements CodeGenerateService {

    /**
     * 模板代码路径
     */
    private static final String DEFAULT_TEMPLATE_PATH = "src/main/resources/META-INF/codetemplate/";

    private final DatabaseMetaService databaseMetaService;

    private final DataSource dataSource;

    public CodeGenerateServiceImpl(DataSource dataSource, DatabaseMetaService databaseMetaService) {
        this.dataSource = dataSource;
        this.databaseMetaService = databaseMetaService;
    }

    @Override
    public void generate(Project project, Module module) throws Exception {
        generate(project, module, DEFAULT_TEMPLATE_PATH, null);
    }

    @Override
    public void generate(Project project, Module module, Map<String, Object> vars) throws Exception {
        generate(project, module, DEFAULT_TEMPLATE_PATH, vars);
    }

    @Override
    public void generate(Project project, Module module, String templatePath) throws Exception {
        generate(project, module, templatePath, null);
    }

    @Override
    public void generate(Project project, Module module, String templatePath, Map<String, Object> vars) throws Exception {
        if (null == templatePath || templatePath.trim().length() == 0) {
            templatePath = DEFAULT_TEMPLATE_PATH;
        }
        Query query = databaseMetaService.parseTableNameOrSql(dataSource, module.getTableNameOrSql());
        //todo 该配置对象是一个重对象，后续可优化成单例
        Configuration config = getConfig(templatePath);
        // 模板文件相对路径和文件的映射
        Map<String, File> templateFiles = getTemplateFiles(templatePath);
        for (Map.Entry<String, File> entry : templateFiles.entrySet()) {
            String relative = entry.getKey();
            Template template = config.getTemplate(relative);
            String outputFilename = getOutputFilename(project, module, relative);
            Object templateModel = this.getTemplateModel(project, query, module, vars);
            this.fill(template, templateModel, outputFilename);
        }
    }

    private Configuration getConfig(String templatePath) throws IOException {
        Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        File file = new File(templatePath);
        if (file.isFile()) {
            file = file.getParentFile();
        }
        config.setDirectoryForTemplateLoading(file);
        config.setLocalizedLookup(false);// 关闭国际化模板查找
        return config;
    }

    private void fill(Template template, Object model, String out) {
        Writer writer = null;
        try {
            File file = new File(out);
            file.getParentFile().mkdirs();
            writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), "UTF-8");
            template.process(model, writer);
            writer.flush();
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(writer);
        }
    }

    private String getOutputFilename(Project project, Module module, String relative) {
        StringBuilder sb = new StringBuilder();
        sb.append(project.getProjectPath()).append("/");
        sb.append(module.getPackageName().replaceAll("[.]", "/")).append("/");
        sb.append(module.getName3()).append("/");
        sb.append(relative.replaceAll("Demo", module.getName1())//
                .replaceAll("demo", module.getName3()));
        return sb.toString().replaceAll("//", "/");
    }

    /**
     * 获取填充模板的模型对象
     *
     * @param project
     * @param query
     * @param module
     *
     * @return
     */
    private Object getTemplateModel(Project project, Query query, Module module, Map<String, Object> vars) {
        if (null == vars) {
            vars = new HashMap<>();
        }
        vars.put("table", query);
        vars.put("project", project);
        vars.put("module", module);
        return vars;
    }

    /**
     * 获取模板文件
     *
     * @return
     */
    private Map<String, File> getTemplateFiles(String templatePath) {
        File file = new File(templatePath);
        if (!file.exists()) {
            throw new RuntimeException("找不到模板文件");
        }
        Map<String, File> files = new LinkedHashMap();
        // 这里可以传入过滤器筛选需要的模板文件
        //pathname.isDirectory() || pathname.getName().toLowerCase().endsWith(".ftl");
        this.collectTemplateFilepath(file, files, pathname -> true, null);
        return files;
    }

    /**
     * 收集模板文件
     *
     * @param file
     * @param files
     * @param filter
     * @param relative
     */
    private void collectTemplateFilepath(File file, Map<String, File> files, FileFilter filter, String relative) {
        if (file.isDirectory()) {
            relative = relative == null ? "" : (relative + "/" + file.getName());
            for (File f : file.listFiles(filter)) {
                this.collectTemplateFilepath(f, files, filter, relative);
            }
        } else if (filter.accept(file)) {
            relative = relative == null ? ("/" + file.getName()) : (relative + "/" + file.getName());
            files.put(relative, file);
        }
    }

    /**
     * 安静关闭
     *
     * @param closeable
     */
    private void closeQuietly(Closeable... closeable) {
        Stream.of(closeable).forEach(c -> {
            try {
                c.close();
            } catch (Exception e) {
                // ignore
            }
        });
    }
}

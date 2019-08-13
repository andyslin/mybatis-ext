package org.autumn.mybatis.codegenerate;

import javax.sql.DataSource;

import org.autumn.mybatis.MybatisBootApplicationTests;
import org.autumn.mybatis.codegenerate.domain.Module;
import org.autumn.mybatis.codegenerate.domain.Project;
import org.autumn.mybatis.codegenerate.service.CodeGenerateService;
import org.autumn.mybatis.codegenerate.service.impl.CodeGenerateServiceImpl;
import org.autumn.mybatis.common.meta.service.DatabaseMetaService;
import org.autumn.mybatis.common.meta.service.impl.DefaultDatabaseMetaService;
import org.junit.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class CodeGenerateServiceTest extends MybatisBootApplicationTests implements InitializingBean {

    private static final String PACKAGE_NAME = "org.autumn.mybatis.codegenerate.generates";

    @Autowired
    private DataSource dataSource;

    private CodeGenerateService service;

    @Override
    public void afterPropertiesSet() throws Exception {
        DatabaseMetaService databaseMetaService = new DefaultDatabaseMetaService();
        this.service = new CodeGenerateServiceImpl(dataSource, databaseMetaService);
    }

    /**
     * 根据SQL语句生成
     *
     * @throws Exception
     */
    @Test
    public void testSql() throws Exception {
        Project project = mockProject();
        Module module = new Module(" select * from PF_USER", "userInfo", PACKAGE_NAME, "用户信息");
        service.generate(project, module);
    }

    /**
     * 根据表名生成
     *
     * @throws Exception
     */
    @Test
    public void testTable() throws Exception {
        Project project = mockProject();
        Module module = new Module("PF_USER", "userInfo", PACKAGE_NAME, "用户信息");
        service.generate(project, module);
    }

    /**
     * 根据表名和模板文件路径生成
     *
     * @throws Exception
     */
    @Test
    public void testTableAndPath() throws Exception {
        Project project = mockProject();
        Module module = new Module("PF_USER", "userInfo", PACKAGE_NAME, "用户信息");
        String templatePath = "src\\main\\resources\\META-INF\\codetemplate\\";
        service.generate(project, module, templatePath);
    }

    /**
     * 根据表名和模板文件生成
     *
     * @throws Exception
     */
    @Test
    public void testTableAndFile() throws Exception {
        Project project = mockProject();
        Module module = new Module("PF_USER", "userInfo", PACKAGE_NAME, "用户信息");
        String templatePath = "src\\main\\resources\\META-INF\\codetemplate\\demo.sqlmapper.xml";
        service.generate(project, module, templatePath);
    }

    private Project mockProject() {
        Project project = Project.builder()
                .author("andyslin")
                .copyright("Autumn Ltd.")
                .projectName("代码生成")
                .projectPath("E:\\workspace-idea-smartapi\\mybatis-ext\\mybatis-generator\\src\\test\\java")
                .version("0.0.1")
                .build();
        return project;
    }
}

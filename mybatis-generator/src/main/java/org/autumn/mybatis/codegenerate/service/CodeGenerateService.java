package org.autumn.mybatis.codegenerate.service;

import java.util.Map;

import org.autumn.mybatis.codegenerate.domain.Module;
import org.autumn.mybatis.codegenerate.domain.Project;

public interface CodeGenerateService {

    /**
     * 生成模板代码
     *
     * @param project 项目
     * @param module  项目模块
     *
     * @throws Exception
     */
    void generate(Project project, Module module) throws Exception;

    /**
     * 生成模板代码
     *
     * @param project 项目
     * @param module  项目模块
     * @param vars    模板变量
     *
     * @throws Exception
     */
    void generate(Project project, Module module, Map<String, Object> vars) throws Exception;

    /**
     * 生成模板代码
     *
     * @param project      项目
     * @param module       项目模块
     * @param templatePath 模板文件目录或模板文件，如果是目录，递归查找所有文件，默认目录src/main/resources/META-INF/codetemplate/
     *
     * @throws Exception
     */
    void generate(Project project, Module module, String templatePath) throws Exception;

    /**
     * 生成模板代码
     *
     * @param project      项目
     * @param module       项目模块
     * @param templatePath 模板文件目录或模板文件，如果是目录，递归查找所有文件，默认目录src/main/resources/META-INF/codetemplate/
     * @param vars         模板变量
     *
     * @throws Exception
     */
    void generate(Project project, Module module, String templatePath, Map<String, Object> vars) throws Exception;
}

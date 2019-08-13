package ${module.packageName}.${module.name3};

import lombok.Getter;
import lombok.Setter;

/**
 * Copy Right Information : ${project.copyright} <br>
 * Project : ${project.projectName}<br>
 * Description : ${module.des} 表单参数<br>
 * Author : ${project.author} <br>
 * Version : ${project.version} <br>
 * Since : ${project.version} <br>
 * Date : ${project.date}<br>
 */
@Getter
@Setter
public class ${module.name1}Form{

    <#list table.columns as column>
    /**
     * ${column.comment}
     */
    private ${column.javaType} ${column.fieldName};
    </#list>
}

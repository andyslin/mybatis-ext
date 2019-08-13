package ${module.packageName}.${module.name3};

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Copy Right Information : ${project.copyright} <br>
 * Project : ${project.projectName}<br>
 * Description : ${module.des} Bean层<br>
 * Author : ${project.author} <br>
 * Version : ${project.version} <br>
 * Since : ${project.version} <br>
 * Date : ${project.date}<br>
 */
@Getter
@Setter
public class ${module.name1}Bean implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -2201443637272719956L;

	<#list table.columns as column>
	/**
	 * ${column.comment}
	 */
	private ${column.javaType} ${column.fieldName};
	</#list>
}

package ${module.packageName}.${module.name3};

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * Copy Right Information : ${project.copyright} <br>
 * Project : ${project.projectName}<br>
 * Description : ${module.des} 数据访问层<br>
 * Author : ${project.author} <br>
 * Version : ${project.version} <br>
 * Since : ${project.version} <br>
 * Date : ${project.date}<br>
 */
public interface ${module.name1}Repository{

    /**
     * 查询列表
     *
     * @param form
     * @return
     */
    List<${module.name1}Bean> findAll(${module.name1}Form form);

    /**
     * 根据主键查找
     *
     * @param id
     * @return
     */
    ${module.name1}Bean find(@Param("id")String id);

    /**
     * 新增
     *
     * @param form
     * @return
     */
    int insert(${module.name1}Form form);

    /**
     * 修改
     *
     * @param form
     * @return
     */
    int update(${module.name1}Form form);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int delete(@Param("id")String id);
}

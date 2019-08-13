package ${module.packageName}.${module.name3};

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Copy Right Information : ${project.copyright} <br>
 * Project : ${project.projectName}<br>
 * Description : ${module.des} 服务层<br>
 * Author : ${project.author} <br>
 * Version : ${project.version} <br>
 * Since : ${project.version} <br>
 * Date : ${project.date}<br>
 */
@Service
public class ${module.name1}Service{

    @Autowired
    private ${module.name1}Repository repository;

    /**
     * 查询列表
     *
     * @param form
     * @return
     */
    public List<${module.name1}Bean> findAll(${module.name1}Form form){
        return repository.findAll(form);
    }

    /**
     * 根据主键查找
     *
     * @param id
     * @return
     */
    public ${module.name1}Bean find(String id){
        return repository.find(id);
    }

    /**
     * 新增
     *
     * @param form
     */
    public void insert(${module.name1}Form form){
        repository.insert(form);
    }

    /**
     * 修改
     *
     * @param form
     */
    public void update(${module.name1}Form form){
        repository.update(form);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(String id){
        repository.delete(id);
    }
}

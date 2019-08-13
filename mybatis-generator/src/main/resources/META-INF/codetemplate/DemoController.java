package ${module.packageName}.${module.name3};

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copy Right Information : ${project.copyright} <br>
 * Project : ${project.projectName}<br>
 * Description : ${module.des} 控制器<br>
 * Author : ${project.author} <br>
 * Version : ${project.version} <br>
 * Since : ${project.version} <br>
 * Date : ${project.date}<br>
 */
@RestController
@RequestMapping("/${module.name3}s")
public class ${module.name1}Controller{

    @Autowired
    private ${module.name1}Service service;

    /**
     * 列表查询
     */
    @GetMapping("")
    public List<${module.name1}Bean> findAll(${module.name1}Form form){
        return service.findAll(form);
    }

    /**
     * 根据主键查找
     */
    @GetMapping("/{id}")
    public ${module.name1}Bean find(String id){
        return service.find(id);
    }

    /**
     * 新增
     */
    @PostMapping("")
    public void insert(${module.name1}Form form){
        service.insert(form);
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public void update(${module.name1}Form form){
        service.update(form);
    }

    /**
     * 删除
     * @param id
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")String id){
        service.delete(id);
    }
}

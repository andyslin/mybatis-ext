package org.autumn.mybatis.decorate;

import java.util.List;

import org.autumn.mybatis.MybatisBootApplicationTests;
import org.autumn.mybatis.decorate.repository.UserRepository;
import org.autumn.mybatis.domain.user.UserBean;
import org.autumn.mybatis.domain.user.UserForm;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class UserServiceTest extends MybatisBootApplicationTests {

    @Autowired
    private UserRepository repository;

    @Test
    public void testFindAll() {
        UserForm form = UserForm.builder()
                .userId("1")
                .userName("张")
                .build();
        List<UserBean> list = repository.findAll(form);
        list.forEach(System.out::println);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("张三", list.get(0).getUserName());
    }

    @Test
    @Transactional
    public void testInsert() {
        UserForm form = UserForm.builder()
                .userId("3")
                .userName("王五")
                .age(18)
                .salary(5000)
                .build();

        // 新增
        int sqlCount = repository.insert(form);
        Assert.assertEquals(1, sqlCount);

        // 查找，并验证新增的数据
        UserBean bean = repository.find(form.getUserId());
        Assert.assertNotNull(bean);
        Assert.assertEquals(form.getUserName(), bean.getUserName());

        // 删除
        sqlCount = repository.delete(form.getUserId());
        Assert.assertEquals(1, sqlCount);

        // 查找，并验证是否删除成功
        bean = repository.find(form.getUserId());
        Assert.assertNull(bean);
    }

}

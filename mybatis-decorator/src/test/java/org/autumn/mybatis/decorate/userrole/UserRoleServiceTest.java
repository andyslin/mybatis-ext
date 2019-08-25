package org.autumn.mybatis.decorate.userrole;

import org.autumn.mybatis.MybatisBootApplicationTests;
import org.autumn.mybatis.domain.user.UserForm;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class UserRoleServiceTest extends MybatisBootApplicationTests {

    @Autowired
    private UserRoleRepository repository;

    @Test
    public void testFindByKey() {
        String userId = "1";
        Map<String, Object> bean = repository.findByKey(userId);
        System.out.println(bean);
    }

    @Test
    public void testSelectJoin() {
        UserRoleForm form = UserRoleForm.builder()
                .userId("1")
                .userName("张")
                .roleId("admin")
                .roleName("管理员")
                .build();
        List<Map<String, Object>> list = repository.selectJoin(form);
        list.forEach(System.out::println);
    }

    @Test
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
    }

    @Test
    public void testUpdate() {
        UserForm form = UserForm.builder()
                .userId("1")
                .userName("王五")
                .age(18)
                .salary(5000)
                .build();

        // 新增
        int sqlCount = repository.update(form);
        Assert.assertEquals(1, sqlCount);
    }
}

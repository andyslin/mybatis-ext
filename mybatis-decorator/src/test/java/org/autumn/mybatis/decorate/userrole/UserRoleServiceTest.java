package org.autumn.mybatis.decorate.userrole;

import java.util.Map;

import org.autumn.mybatis.MybatisBootApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRoleServiceTest extends MybatisBootApplicationTests {

    @Autowired
    private UserRoleRepository repository;

    @Test
    public void testFindByKey() {
        String userId = "1";
        Map<String, Object> bean = repository.findByKey(userId);
        System.out.println(bean);
    }
}

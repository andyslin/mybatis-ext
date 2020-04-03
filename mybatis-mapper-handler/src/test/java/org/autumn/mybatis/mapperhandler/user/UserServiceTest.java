package org.autumn.mybatis.mapperhandler.user;

import org.autumn.mybatis.domain.user.UserForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceTest {

    @Autowired
    private UserRepository repository;

    @Test
    public void testInsertUser(UserForm user) {
        repository.insert(user);
    }
}

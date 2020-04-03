package org.autumn.mybatis.mapperhandler.user;

import org.apache.ibatis.annotations.Mapper;
import org.autumn.mybatis.domain.user.UserForm;
import org.autumn.mybatis.mapperhandler.annotation.SqlRef;

@Mapper
public interface UserRepository {

    @SqlRef({"insert", "insertUserRole"})
    int[] insert(UserForm form);
}

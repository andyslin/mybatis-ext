package org.autumn.mybatis.decorate.userrole;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleRepository {

    /**
     * 查找
     *
     * @param userId
     *
     * @return
     */
    Map<String, Object> findByKey(@Param("userId") String userId);

}

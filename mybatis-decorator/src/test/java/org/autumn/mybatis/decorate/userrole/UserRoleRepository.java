package org.autumn.mybatis.decorate.userrole;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.autumn.mybatis.domain.user.UserForm;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserRoleRepository {

    /**
     * 查找
     *
     * @param userId
     * @return
     */
    Map<String, Object> findByKey(@Param("userId") String userId);

    /**
     * 查询
     *
     * @param form
     * @return
     */
    List<Map<String, Object>> selectJoin(UserRoleForm form);

    /**
     * 新增
     *
     * @param form
     * @return
     */
    int insert0(UserForm form);

    /**
     * 新增
     *
     * @param form
     * @return
     */
    int insert(UserForm form);

    /**
     * 更新
     *
     * @param form
     * @return
     */
    int update0(UserForm form);

    /**
     * 更新
     *
     * @param form
     * @return
     */
    int update(UserForm form);
}

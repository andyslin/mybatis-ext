package org.autumn.mybatis.decorate.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.autumn.mybatis.decorate.domain.UserBean;
import org.autumn.mybatis.decorate.domain.UserForm;

@Mapper
public interface UserRepository {

    /**
     * 查询列表
     *
     * @param form
     *
     * @return
     */
    List<UserBean> findAll(UserForm form);

    /**
     * 查找
     *
     * @param userId
     *
     * @return
     */
    UserBean find(@Param("userId") String userId);

    /**
     * 新增
     *
     * @param form
     *
     * @return
     */
    int insert(UserForm form);

    /**
     * 更新
     *
     * @param form
     *
     * @return
     */
    int update(UserForm form);

    /**
     * 删除
     *
     * @param userId
     *
     * @return
     */
    int delete(@Param("userId") String userId);
}

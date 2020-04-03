package org.autumn.mybatis.domain.user;

import java.util.List;

import org.autumn.mybatis.domain.userrole.UserRoleForm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserForm {

    private String userId;

    private String userName;

    private int age;

    private double salary;

    private List<UserRoleForm> roles;
}

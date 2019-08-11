package org.autumn.mybatis.domain.user;

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
}
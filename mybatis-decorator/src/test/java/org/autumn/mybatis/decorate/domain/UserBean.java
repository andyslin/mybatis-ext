package org.autumn.mybatis.decorate.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1316260575232795510L;

    private String userId;

    private String userName;

    private int age;

    private double salary;
}

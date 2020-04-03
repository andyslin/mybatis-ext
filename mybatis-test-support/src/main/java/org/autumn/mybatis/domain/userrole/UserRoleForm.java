package org.autumn.mybatis.domain.userrole;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRoleForm {

    private String userId;
    private String userName;
    private String roleId;
    private String roleName;
}

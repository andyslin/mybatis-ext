-- ----------------------------
-- Records of pf_user
-- ----------------------------
INSERT INTO `pf_user`(`USER_ID`, `USER_NAME`, `AGE`, `SALARY`)
VALUES ('1', '张三', 35, 50000.00);
INSERT INTO `pf_user`(`USER_ID`, `USER_NAME`, `AGE`, `SALARY`)
VALUES ('2', '李四', 26, 20000.00);

-- ----------------------------
-- Records of pf_role
-- ----------------------------
INSERT INTO `pf_role`(`ROLE_ID`, `ROLE_NAME`, `DES`)
VALUES ('admin', '管理员', '拥有最大权限');
INSERT INTO `pf_role`(`ROLE_ID`, `ROLE_NAME`, `DES`)
VALUES ('visitor', '游客', '最低权限');

-- ----------------------------
-- Records of pf_user_role
-- ----------------------------
INSERT INTO `pf_user_role`(`USER_ID`, `ROLE_ID`)
VALUES ('1', 'admin');
INSERT INTO `pf_user_role`(`USER_ID`, `ROLE_ID`)
VALUES ('2', 'visitor');

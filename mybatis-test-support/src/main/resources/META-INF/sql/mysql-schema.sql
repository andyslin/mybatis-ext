/*==============================================================*/
/* Table: PF_USER                                               */
/*==============================================================*/
DROP TABLE IF EXISTS PF_USER;
CREATE TABLE PF_USER
(
  USER_ID   VARCHAR(64) NOT NULL COMMENT '用户ID',
  USER_NAME VARCHAR(64) COMMENT '用户名',
  AGE       INTEGER COMMENT '年龄',
  SALARY    DECIMAL(11,2) COMMENT '薪水',
  PRIMARY KEY (USER_ID)
);

ALTER TABLE PF_USER COMMENT '用户表';


/*==============================================================*/
/* Table: PF_ROLE                                               */
/*==============================================================*/
DROP TABLE IF EXISTS PF_ROLE;
CREATE TABLE PF_ROLE
(
  ROLE_ID   VARCHAR(64)  NOT NULL COMMENT '角色ID',
  ROLE_NAME VARCHAR(200) NOT NULL COMMENT '角色名称',
  DES       VARCHAR(200) COMMENT '备注',
  PRIMARY KEY (ROLE_ID)
);

ALTER TABLE PF_ROLE COMMENT '角色表';


/*==============================================================*/
/* Table: PF_USER_ROLE                                          */
/*==============================================================*/
DROP TABLE IF EXISTS PF_USER_ROLE;
CREATE TABLE PF_USER_ROLE
(
  USER_ID VARCHAR(64) NOT NULL COMMENT '用户ID',
  ROLE_ID VARCHAR(64) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (USER_ID, ROLE_ID)
);

ALTER TABLE PF_USER_ROLE COMMENT '用户角色表';

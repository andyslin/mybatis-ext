/*==============================================================*/
/* Table: PF_USER                                               */
/*==============================================================*/
DROP TABLE IF EXISTS PF_USER;
CREATE TABLE PF_USER
(
  USER_ID   VARCHAR(64) NOT NULL,
  USER_NAME VARCHAR(64),
  AGE       INTEGER,
  SALARY    NUMBER(11,2),
  CONSTRAINT PK_PF_USER PRIMARY KEY (USER_ID)
);

COMMENT ON TABLE PF_USER IS '用户表';
COMMENT ON COLUMN PF_USER.USER_ID IS '用户ID';
COMMENT ON COLUMN PF_USER.USER_NAME IS '用户名';
COMMENT ON COLUMN PF_USER.AGE IS '年龄';
COMMENT ON COLUMN PF_USER.SALARY IS '薪水';

/*==============================================================*/
/* Table: PF_ROLE                                               */
/*==============================================================*/
DROP TABLE IF EXISTS PF_ROLE;
CREATE TABLE PF_ROLE
(
  ROLE_ID   VARCHAR(64)  NOT NULL,
  ROLE_NAME VARCHAR(200) NOT NULL,
  DES       VARCHAR(200),
  CONSTRAINT PK_PF_ROLE PRIMARY KEY (ROLE_ID)
);

COMMENT ON TABLE PF_ROLE IS '角色表';
COMMENT ON COLUMN PF_ROLE.ROLE_ID IS '角色ID';
COMMENT ON COLUMN PF_ROLE.ROLE_NAME IS '角色名称';
COMMENT ON COLUMN PF_ROLE.DES IS '备注';


/*==============================================================*/
/* Table: PF_USER_ROLE                                          */
/*==============================================================*/
DROP TABLE IF EXISTS PF_USER_ROLE;
CREATE TABLE PF_USER_ROLE
(
  USER_ID VARCHAR(64) NOT NULL,
  ROLE_ID VARCHAR(64) NOT NULL,
  CONSTRAINT PK_PF_USER_ROLE PRIMARY KEY (USER_ID, ROLE_ID)
);

COMMENT ON TABLE PF_USER_ROLE IS '用户角色表';
COMMENT ON COLUMN PF_USER_ROLE.USER_ID IS '用户ID';
COMMENT ON COLUMN PF_USER_ROLE.ROLE_ID IS '角色ID';


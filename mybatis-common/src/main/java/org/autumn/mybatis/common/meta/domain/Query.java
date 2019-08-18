package org.autumn.mybatis.common.meta.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class Query implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6420012564136908545L;

    private String catalog;

    private String schema;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表注释
     */
    private String comment;
    /**
     * 表类型（视图、表、SQL等）
     */
    private String type;
    /**
     * 是否为SQL语句
     */
    private boolean isSql;
    /**
     * 字段集合
     */
    private List<Column> columns;
    /**
     * 主键
     */
    private List<Column> keys;
    /**
     * 普通字段
     */
    private List<Column> normals;
}

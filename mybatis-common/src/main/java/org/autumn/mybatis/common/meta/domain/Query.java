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

    private String tableName;

    private String comment;

    private String type;

    private boolean isSql;

    private List<Column> columns;

    private List<Column> keys;

    private List<Column> normals;
}

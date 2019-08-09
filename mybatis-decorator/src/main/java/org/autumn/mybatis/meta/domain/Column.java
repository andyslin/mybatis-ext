package org.autumn.mybatis.meta.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Column implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1182407447539833355L;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 驼峰式名称
     */
    private String fieldName;

    /**
     * 在Mybatis中Field的配置格式，形如#{userId,jdbcType=VARCHAR}
     */
    private String mybatisField;

    /**
     * 类型，参考{@link java.sql.Types}
     */
    private int sqlType;

    /**
     * 和SqlType对应的jdbcType
     */
    private String jdbcType;

    /**
     * 和SqlType对应的javaType
     */
    private String javaType;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 注释
     */
    private String comment;

    /**
     * 表示给定列的指定列大小。对于数值数据，这是最大精度。对于字符数据，这是字符长度。对于日期时间数据类型，这是 String
     * 表示形式的字符长度（假定允许的最大小数秒组件的精度）。对于二进制数据，这是字节长度。对于 ROWID 数据类型，这是字节长度。对于列大小不适用的数据类型，则返回 Null
     */
    private int columnSize;

    /**
     * 小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null
     */
    private int decimalDigits;

    /**
     * 表中的列的索引（从 1 开始）
     */
    private int ordinalPosition;

    /**
     * 是否可为null
     */
    private boolean nullable;

    /**
     * 是否自增长
     */
    private boolean autoIncrement;

    /**
     * 是否为主键
     */
    private boolean key;
}

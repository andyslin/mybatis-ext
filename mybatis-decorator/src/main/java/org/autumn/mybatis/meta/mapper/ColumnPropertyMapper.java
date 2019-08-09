package org.autumn.mybatis.meta.mapper;

public interface ColumnPropertyMapper {

    /**
     * 属性映射
     *
     * @param column 列名
     *
     * @return Java属性名
     */
    String mapper(String column);
}

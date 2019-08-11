package org.autumn.mybatis.common.meta.mapper;


import org.apache.ibatis.session.Configuration;

public interface ColumnPropertyMapper {

    /**
     * 属性映射
     *
     * @param column 列名
     * @return Java属性名
     */
    default String mapper(String column) {
        return mapper(null, column);
    }

    /**
     * 属性映射
     *
     * @param configuration
     * @param column        列名
     * @return Java属性名
     */
    String mapper(Configuration configuration, String column);
}

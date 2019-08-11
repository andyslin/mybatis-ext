package org.autumn.mybatis.common.meta.mapper.impl;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.meta.mapper.ColumnPropertyMapper;

public class DefaultColumnPropertyMapper implements ColumnPropertyMapper {

    @Override
    public String mapper(Configuration configuration, String column) {
        int index = column.lastIndexOf(".");
        if (-1 != index) {
            column = column.substring(index + 1);
        }
        StringBuilder sb = new StringBuilder();
        boolean upper = false, first = true;
        for (char ch : column.trim().toCharArray()) {
            if (ch == '-' || ch == '_') {
                upper = !first;
            } else {
                sb.append(upper ? Character.toUpperCase(ch) : Character.toLowerCase(ch));
                upper = false;
                first = false;
            }
        }
        return sb.toString();
    }
}

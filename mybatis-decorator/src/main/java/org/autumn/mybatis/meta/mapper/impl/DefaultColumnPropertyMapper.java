package org.autumn.mybatis.meta.mapper.impl;

import org.autumn.mybatis.meta.mapper.ColumnPropertyMapper;

public class DefaultColumnPropertyMapper implements ColumnPropertyMapper {

    @Override
    public String mapper(String column) {
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

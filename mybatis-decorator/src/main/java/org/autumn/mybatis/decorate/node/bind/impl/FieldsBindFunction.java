package org.autumn.mybatis.decorate.node.bind.impl;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.meta.domain.Column;
import org.autumn.mybatis.common.meta.domain.Query;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Set;

/*package*/ class FieldsBindFunction extends AbstractMetadataBindFunction {

    @Override
    public String getName() {
        return "fields";
    }

    @Override
    protected void eval(Configuration configuration, Element bind, String subName, String alias, Query query, Set<String> excludes) {
        List<Column> columns = query.getColumns();
        StringBuilder fields = new StringBuilder();
        String text = "";
        if ("name".equalsIgnoreCase(subName)) {//只列出名称，不包括别名，一般用于INSERT的字段列表
            for (Column column : columns) {
                if (!excludes.contains(column.getColumnName())) {
                    fields.append(",").append(column.getColumnName());
                }
            }
            text = fields.substring(1);
        } else if ("mybatis".equalsIgnoreCase(subName)) {//只列出Mybatis字段，一般用于INSERT的值列表
            for (Column column : columns) {
                if (!excludes.contains(column.getColumnName())) {
                    fields.append(",").append(column.getMybatisField());
                }
            }
            text = fields.substring(1);
        } else if ("and".equalsIgnoreCase(subName)) {//and，用于where条件
            for (Column column : columns) {
                if (!excludes.contains(column.getColumnName())) {
                    fields.append(" AND ").append(column.getColumnName()).append("=").append(column.getMybatisField());
                }
            }
            text = fields.substring(5);
        } else if ("or".equalsIgnoreCase(subName)) {//or，用于where条件
            for (Column column : columns) {
                if (!excludes.contains(column.getColumnName())) {
                    fields.append(" OR ").append(column.getColumnName()).append("=").append(column.getMybatisField());
                }
            }
            text = "(" + fields.substring(4) + ")";
        } else if ("set".equalsIgnoreCase(subName)) {//set，用于update
            for (Column column : columns) {
                if (!excludes.contains(column.getColumnName())) {
                    fields.append(",").append(column.getColumnName()).append("=").append(column.getMybatisField());
                }
            }
            text = fields.substring(1);
        } else {// 一般的查询列表
            for (Column column : columns) {
                if (!excludes.contains(column.getColumnName())) {
                    fields.append(",").append(alias).append(column.getColumnName());
                }
            }
            text = fields.substring(1);
        }

        Document document = bind.getOwnerDocument();
        Text textNode = document.createTextNode(text);
        bind.getParentNode().replaceChild(textNode, bind);
    }
}

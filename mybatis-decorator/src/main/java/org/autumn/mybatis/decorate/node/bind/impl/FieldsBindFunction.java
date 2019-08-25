package org.autumn.mybatis.decorate.node.bind.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.meta.domain.Column;
import org.autumn.mybatis.common.meta.domain.Query;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * fields语法：
 * <p>
 * 子函数：fields[.name|mybatis|and|or|set]
 * 1. 默认：列出含表的别名的字段列表，一般用于SELECT的字段选择部分
 * 2. name 直接列出数据库列名，不包括表的别名，一般用于INSERT的字段列表
 * 3. mybatis 列出Mybatis形式的字段列表，一般用于INSERT的值列表
 * 4. and | or 列出查询条件，并使用 and | or 连接，一般用于where条件查询
 * 5. set 设置字段值，逗号分隔多个字段，一般用于UPDATE的set语句部分
 * <p>
 * 参数：[alias.]TABLE_NAME[,prefix=PREFIX][,column=KEYS|!KEYS][,exclude_column]{0,} 表别名.表名,排除的字段1,排除的字段2,...
 * <p>
 */
/*package*/ class FieldsBindFunction extends AbstractMetadataBindFunction {

    @Override
    public String getName() {
        return "fields";
    }

    @Override
    protected void eval(Configuration configuration, Element bind, String subName, String alias, Query query, Set<String> excludes, Map<String, String> vars) {
        List<Column> columns = query.getColumns();
        String columnFlag = vars.get("column");
        if ("KEYS".equalsIgnoreCase(columnFlag)) {
            columns = query.getKeys();
        } else if ("!KEYS".equalsIgnoreCase(columnFlag)) {
            columns = query.getNormals();
        }

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
            String prefix = vars.get("prefix");
            for (Column column : columns) {
                String columnName = column.getColumnName();
                if (!excludes.contains(columnName)) {
                    if (StringUtils.hasText(prefix)) {
                        fields.append(",").append(alias).append(columnName).append(" AS ").append(prefix).append("_").append(columnName);
                    } else {
                        fields.append(",").append(alias).append(columnName);
                    }
                }
            }
            text = fields.substring(1);
        }

        Document document = bind.getOwnerDocument();
        Text textNode = document.createTextNode(text);
        bind.getParentNode().replaceChild(textNode, bind);
    }
}

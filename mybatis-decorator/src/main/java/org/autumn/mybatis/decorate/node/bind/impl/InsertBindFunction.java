package org.autumn.mybatis.decorate.node.bind.impl;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.meta.domain.Column;
import org.autumn.mybatis.common.meta.domain.Query;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Map;
import java.util.Set;

/*package*/ class InsertBindFunction extends AbstractMetadataBindFunction {

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    protected void eval(Configuration configuration, Element bind, String subName, String alias, Query query, Set<String> excludes, Map<String, String> vars) {
        List<Column> columns = query.getColumns();
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (Column column : columns) {
            if (!excludes.contains(column.getColumnName())) {
                fields.append(",").append(column.getColumnName());
                values.append(",").append(column.getMybatisField());
            }
        }
        String insert = new StringBuilder()
                .append("insert into ").append(query.getTableName())
                .append("(").append(fields.substring(1)).append(")")
                .append("values(").append(values.substring(1)).append(")")
                .toString();

        Node parentNode = bind.getParentNode();
        parentNode.removeChild(bind);
        parentNode.setTextContent(insert);
    }
}

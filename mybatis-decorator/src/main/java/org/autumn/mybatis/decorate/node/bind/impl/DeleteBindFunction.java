package org.autumn.mybatis.decorate.node.bind.impl;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.meta.domain.Column;
import org.autumn.mybatis.common.meta.domain.Query;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Map;
import java.util.Set;

/*package*/ class DeleteBindFunction extends AbstractMetadataBindFunction {

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    protected void eval(Configuration configuration, Element bind, String subName, String alias, Query query, Set<String> excludes, Map<String, String> vars) {
        List<Column> keys = query.getKeys();
        StringBuilder where = new StringBuilder();
        for (Column column : keys) {
            if (!excludes.contains(column.getColumnName())) {
                where.append(" AND ").append(column.getColumnName()).append("=").append(column.getMybatisField());
            }
        }

        String delete = new StringBuilder()
                .append("delete from ").append(query.getTableName())
                .append(" where ").append(where.substring(5))// 去掉第一个" AND "
                .toString();

        Node parentNode = bind.getParentNode();
        parentNode.removeChild(bind);
        parentNode.setTextContent(delete);
    }
}

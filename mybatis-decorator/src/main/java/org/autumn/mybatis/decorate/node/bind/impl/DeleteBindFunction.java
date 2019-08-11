package org.autumn.mybatis.decorate.node.bind.impl;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.meta.MetaHolder;
import org.autumn.mybatis.common.meta.domain.Column;
import org.autumn.mybatis.common.meta.domain.Query;
import org.autumn.mybatis.decorate.node.bind.BindFunction;
import org.w3c.dom.Node;

import java.util.List;

/*package*/ class DeleteBindFunction implements BindFunction {

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public void eval(Configuration configuration, Node node, String subName, String bindValue) {
        Environment environment = configuration.getEnvironment();
        if (null == environment || null == environment.getDataSource()) {
            return;
        }
        Query query = MetaHolder.parseTableNameOrSql(environment.getDataSource(), bindValue);

        List<Column> keys = query.getKeys();
        StringBuilder where = new StringBuilder();
        for (Column column : keys) {
            where.append(" AND ").append(column.getColumnName()).append("=").append(column.getMybatisField());
        }

        String delete = new StringBuilder()
                .append("delete from ").append(query.getTableName())
                .append(" where ").append(where.substring(5))// 去掉第一个" AND "
                .toString();

        Node parentNode = node.getParentNode();
        parentNode.removeChild(node);
        parentNode.setTextContent(delete);
    }
}

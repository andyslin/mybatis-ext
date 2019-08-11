package org.autumn.mybatis.decorate.node.bind.impl;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.meta.MetaHolder;
import org.autumn.mybatis.common.meta.domain.Column;
import org.autumn.mybatis.common.meta.domain.Query;
import org.autumn.mybatis.decorate.node.bind.BindFunction;
import org.w3c.dom.Node;

import java.util.List;

/*package*/ class InsertBindFunction implements BindFunction {

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    public void eval(Configuration configuration, Node node, String subName, String bindValue) {
        Environment environment = configuration.getEnvironment();
        if (null == environment || null == environment.getDataSource()) {
            return;
        }
        Query query = MetaHolder.parseTableNameOrSql(environment.getDataSource(), bindValue);

        List<Column> columns = query.getColumns();
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (Column column : columns) {
            fields.append(",").append(column.getColumnName());
            values.append(",").append(column.getMybatisField());
        }
        String insert = new StringBuilder()
                .append("insert into ").append(query.getTableName())
                .append("(").append(fields.substring(1)).append(")")
                .append("values(").append(values.substring(1)).append(")")
                .toString();

        Node parentNode = node.getParentNode();
        parentNode.removeChild(node);
        parentNode.setTextContent(insert);
    }
}

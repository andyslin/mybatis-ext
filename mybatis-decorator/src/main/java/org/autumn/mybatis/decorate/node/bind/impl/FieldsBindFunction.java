package org.autumn.mybatis.decorate.node.bind.impl;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.meta.MetaHolder;
import org.autumn.mybatis.common.meta.domain.Column;
import org.autumn.mybatis.common.meta.domain.Query;
import org.autumn.mybatis.decorate.node.bind.BindFunction;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.util.List;

/*package*/ class FieldsBindFunction implements BindFunction {

    @Override
    public String getName() {
        return "fields";
    }

    @Override
    public void eval(Configuration configuration, Node node, String subName, String bindValue) {
        Environment environment = configuration.getEnvironment();
        if (null == environment || null == environment.getDataSource()) {
            return;
        }

        String prefix = "";
        String tableName = bindValue;
        int index = tableName.indexOf('.');
        if (-1 != index) {
            prefix = tableName.substring(0, index + 1);
            tableName = tableName.substring(index + 1);
        }

        Query query = MetaHolder.parseTableNameOrSql(environment.getDataSource(), tableName);

        List<Column> columns = query.getColumns();
        StringBuilder fields = new StringBuilder();
        for (Column column : columns) {
            fields.append(",").append(prefix).append(column.getColumnName());
        }

        Document document = node.getOwnerDocument();
        Text textNode = document.createTextNode(fields.substring(1));
        node.getParentNode().replaceChild(textNode, node);
    }
}

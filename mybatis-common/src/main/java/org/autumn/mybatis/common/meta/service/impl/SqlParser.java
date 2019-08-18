package org.autumn.mybatis.common.meta.service.impl;

import org.autumn.mybatis.common.meta.MetaHolder;
import org.autumn.mybatis.common.meta.domain.Column;
import org.autumn.mybatis.common.meta.domain.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*package*/ class SqlParser extends AbstractParser {


    @Override
    public Query parse(Connection conn, String sql) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 包装sql，因为只需数据结构，所以返回空数据集
            sql = "select * from (" + sql + ")a where 1 = 0";
            statement = conn.prepareStatement(sql);
            resultSet = statement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            Query query = new Query();
            query.setSql(true);
            query.setType("SQL");
            setTableProperties(metaData, query);
            setColumns(metaData, query);

            return query;
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
    }

    /**
     * 设置Table属性
     *
     * @param metaData
     * @param query
     * @throws SQLException
     */
    private void setTableProperties(ResultSetMetaData metaData, Query query) throws SQLException {
        query.setTableName(metaData.getTableName(1));
        query.setCatalog(metaData.getCatalogName(1));
        query.setSchema(metaData.getSchemaName(1));
        //query.setType(rs.getString("TABLE_TYPE"));
        query.setComment("");
    }

    private void setColumns(ResultSetMetaData metaData, Query query) throws SQLException {
        int columnCount = metaData.getColumnCount();
        List<Column> columns = new ArrayList<>(columnCount);
        List<Column> keys = new ArrayList<>();
        List<Column> normals = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            Column column = new Column();
            columns.add(column);
            setColumnProperties(column, metaData, i);
            if (!column.isNullable()) {
                column.setKey(false);
                normals.add(column);
            } else {
                column.setKey(true);
                keys.add(column);
            }
        }
        query.setColumns(columns);
        query.setKeys(keys);
        query.setNormals(normals);
    }

    private void setColumnProperties(Column column, ResultSetMetaData metaData, int i) throws SQLException {
        String columnName = metaData.getColumnLabel(i);
        column.setColumnName(columnName);
        column.setFieldName(MetaHolder.column2Property(columnName));
        column.setComment("");
        int sqlType = metaData.getColumnType(i);
        setJdbcType(column, sqlType);

        column.setTypeName(metaData.getColumnTypeName(i));
        column.setNullable(ResultSetMetaData.columnNoNulls != metaData.isNullable(i));
        column.setAutoIncrement(metaData.isAutoIncrement(i));
        column.setOrdinalPosition(i);

        Integer integer = metaData.getPrecision(i);
        if (null != integer) {
            column.setColumnSize(integer);
        }

        Integer decimalDigits = metaData.getScale(i);
        if (null != integer) {
            column.setDecimalDigits(decimalDigits);
        }
    }

}

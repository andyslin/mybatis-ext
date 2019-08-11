package org.autumn.mybatis.common.meta.service.impl;

import org.apache.ibatis.type.JdbcType;
import org.autumn.mybatis.common.meta.MetaHolder;
import org.autumn.mybatis.common.meta.domain.Column;
import org.autumn.mybatis.common.meta.domain.Query;
import org.autumn.mybatis.common.meta.service.DatabaseMetaService;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DefaultDatabaseMetaService implements DatabaseMetaService {

    private static final Pattern pattern = Pattern.compile("select\\s+\\w*", Pattern.CASE_INSENSITIVE);

    @Override
    public Query parseTableNameOrSql(Connection connection, String tableNameOrSql) throws SQLException {
        if (pattern.matcher(tableNameOrSql).find()) {
            return parseSql(connection, tableNameOrSql);
        } else {
            return parseTable(connection, tableNameOrSql);
        }
    }

    private Query parseSql(Connection conn, String sql) throws SQLException {
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
            setTableProperties(metaData, query);
            setColumns(metaData, query);

            return query;
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
    }

    private Query parseTable(Connection conn, String tableName) throws SQLException {
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        try {
            String catalog = null;
            String schema = null;

            DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getTables(catalog, schema, tableName, new String[]{"TABLE", "VIEW"});
            if (!rs.next()) {
                return null;
            }
            Query query = new Query();
            query.setSql(false);
            setTableProperties(rs, query);

            rs1 = meta.getPrimaryKeys(query.getCatalog(), query.getSchema(), query.getTableName());
            List<String> key = getKey(rs1);
            rs1.close();

            rs2 = meta.getColumns(query.getCatalog(), query.getSchema(), query.getTableName(), "%");
            setColumns(query, rs2, key);
            rs2.close();

            return query;
        } finally {
            closeResultSet(rs2);
            closeResultSet(rs1);
            closeResultSet(rs);
        }
    }

    /**
     * 设置Table属性
     *
     * @param rs
     * @param query
     * @throws SQLException
     */
    private void setTableProperties(ResultSet rs, Query query) throws SQLException {
        //        TABLE_CAT:autumn2
        //        TABLE_SCHEM:null
        //        TABLE_NAME:act_ge_bytearray
        //        TABLE_TYPE:TABLE
        //        REMARKS:
        //        TYPE_CAT:null
        //        TYPE_SCHEM:null
        //        TYPE_NAME:null
        //        SELF_REFERENCING_COL_NAME:null
        //        REF_GENERATION:null
        //printResultSetColumns(rs);
        query.setTableName(rs.getString("TABLE_NAME"));
        query.setCatalog(rs.getString("TABLE_CAT"));
        query.setSchema(rs.getString("TABLE_SCHEM"));
        query.setType(rs.getString("TABLE_TYPE"));
        query.setComment(rs.getString("REMARKS"));
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

    /**
     * 获取主键
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private List<String> getKey(ResultSet rs) throws SQLException {
        List<String> key = new ArrayList<>();
        while (rs.next()) {
            key.add(rs.getString("COLUMN_NAME"));
        }
        return key;
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

    /**
     * 获取列
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private void setColumns(Query query, ResultSet rs, List<String> key) throws SQLException {
        List<Column> columns = new ArrayList<>();
        List<Column> keys = new ArrayList<>();
        List<Column> normals = new ArrayList<>();
        while (rs.next()) {
            Column cm = new Column();
            String columnName = rs.getString("COLUMN_NAME");
            columns.add(cm);
            cm.setColumnName(columnName);
            cm.setFieldName(MetaHolder.column2Property(columnName));
            if (null != key && key.contains(columnName)) {
                cm.setKey(true);
                keys.add(cm);
            } else {
                cm.setKey(false);
                normals.add(cm);
            }
            setColumnProperties(rs, cm);
        }

        query.setColumns(columns);
        query.setKeys(keys);
        query.setNormals(normals);
    }

    private void setColumnProperties(ResultSet rs, Column cm) throws SQLException {
        //        TABLE_CAT:autumn2
        //        TABLE_SCHEM:null
        //        TABLE_NAME:act_evt_log
        //        COLUMN_NAME:LOG_NR_
        //        DATA_TYPE:-5
        //        TYPE_NAME:BIGINT
        //        COLUMN_SIZE:19
        //        BUFFER_LENGTH:65535
        //        DECIMAL_DIGITS:0
        //        NUM_PREC_RADIX:10
        //        NULLABLE:0
        //        REMARKS:
        //        COLUMN_DEF:null
        //        SQL_DATA_TYPE:0
        //        SQL_DATETIME_SUB:0
        //        CHAR_OCTET_LENGTH:null
        //        ORDINAL_POSITION:1
        //        IS_NULLABLE:NO
        //        SCOPE_CATALOG:null
        //        SCOPE_SCHEMA:null
        //        SCOPE_TABLE:null
        //        SOURCE_DATA_TYPE:null
        //        IS_AUTOINCREMENT:YES
        //        IS_GENERATEDCOLUMN:NO
        //printResultSetColumns(rs);
        cm.setComment(rs.getString("REMARKS"));
        int sqlType = rs.getInt("DATA_TYPE");
        setJdbcType(cm, sqlType);

        cm.setTypeName(rs.getString("TYPE_NAME"));
        cm.setNullable("YES".equalsIgnoreCase(rs.getString("IS_NULLABLE")));
        cm.setAutoIncrement("YES".equalsIgnoreCase(rs.getString("IS_AUTOINCREMENT")));
        cm.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));

        Integer integer = rs.getObject("COLUMN_SIZE", Integer.class);
        if (null != integer) {
            cm.setColumnSize(integer);
        }

        Integer decimalDigits = rs.getObject("DECIMAL_DIGITS", Integer.class);
        if (null != integer) {
            cm.setDecimalDigits(decimalDigits);
        }
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

    private void setJdbcType(Column column, int sqlType) {
        column.setSqlType(sqlType);

        String jdbcType = this.getJdbcType(sqlType);
        column.setJdbcType(jdbcType);
        String javaType = this.getJavaType(sqlType);
        column.setJavaType(javaType);
        column.setMybatisField("#{" + column.getFieldName() + ",jdbcType=" + jdbcType + "}");
    }

    private String getJdbcType(int sqlType) {
        return JdbcType.forCode(sqlType).name();
    }

    private String getJavaType(int sqlType) {
        switch (JdbcType.forCode(sqlType)) {
            case ARRAY:
                break;
            case BIT:
                return "int";
            case TINYINT:
            case SMALLINT:
            case INTEGER:
                return "int";
            case BIGINT:
                return "long";
            case FLOAT:
                return "float";
            case REAL:
            case DOUBLE:
            case NUMERIC:
            case DECIMAL:
                return "double";
            case CHAR:
            case VARCHAR:
                return "String";
            case LONGVARCHAR:
                return "byte[]";
            case DATE:
            case TIME:
            case TIMESTAMP:
                return "String";
            case BINARY:
            case VARBINARY:
                return "String";
            case LONGVARBINARY:
                return "byte[]";
            case NULL:
            case OTHER:
                return "String";
            case BLOB:
            case CLOB:
                return "byte[]";
            case BOOLEAN:
                return "boolean";
            case CURSOR: // Oracle
            case UNDEFINED:
            case NVARCHAR: // JDK6
            case NCHAR: // JDK6
            case NCLOB: // JDK6
            case STRUCT:
            case JAVA_OBJECT:
            case DISTINCT:
                return "byte[]";
            case REF:
            case DATALINK:
            case ROWID: // JDK6
                return "String";
            case LONGNVARCHAR: // JDK6
                return "byte[]";
            case SQLXML: // JDK6
                return "byte[]";
            case DATETIMEOFFSET: // SQL Server 2008
                return "String";
        }
        return "String";
    }

    private void closeResultSet(ResultSet rs) {
        if (null != rs) {
            try {
                if (!rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    private void closeStatement(Statement statement) {
        if (null != statement) {
            try {
                if (!statement.isClosed()) {
                    statement.close();
                }
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}

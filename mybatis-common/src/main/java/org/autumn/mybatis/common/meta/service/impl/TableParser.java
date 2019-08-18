package org.autumn.mybatis.common.meta.service.impl;

import org.autumn.mybatis.common.meta.MetaHolder;
import org.autumn.mybatis.common.meta.domain.Column;
import org.autumn.mybatis.common.meta.domain.Query;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*package*/ class TableParser extends AbstractParser {

    @Override
    public Query parse(Connection conn, String tableName) throws SQLException {
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
}

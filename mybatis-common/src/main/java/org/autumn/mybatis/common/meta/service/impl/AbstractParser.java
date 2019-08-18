package org.autumn.mybatis.common.meta.service.impl;

import org.apache.ibatis.type.JdbcType;
import org.autumn.mybatis.common.meta.domain.Column;
import org.autumn.mybatis.common.meta.service.impl.DefaultDatabaseMetaService.MetaParser;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*package*/ abstract class AbstractParser implements MetaParser {


    protected void setJdbcType(Column column, int sqlType) {
        column.setSqlType(sqlType);
        column.setNumeric(JdbcUtils.isNumeric(sqlType));
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

    protected void closeResultSet(ResultSet rs) {
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

    protected void closeStatement(Statement statement) {
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

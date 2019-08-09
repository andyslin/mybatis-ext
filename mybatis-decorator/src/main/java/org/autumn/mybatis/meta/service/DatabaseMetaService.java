package org.autumn.mybatis.meta.service;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.autumn.mybatis.meta.domain.Query;

public interface DatabaseMetaService {

    /**
     * 解析一个表名或者可执行的SQL语句
     *
     * @param connection
     * @param tableNameOrSql
     *
     * @return
     *
     * @throws SQLException
     */
    Query parseTableNameOrSql(Connection connection, String tableNameOrSql) throws SQLException;

    /**
     * 解析一个表名或者可执行的SQL语句
     *
     * @param dataSource
     * @param tableNameOrSql
     *
     * @return
     *
     * @throws SQLException
     */
    default Query parseTableNameOrSql(DataSource dataSource, String tableNameOrSql) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return parseTableNameOrSql(conn, tableNameOrSql);
        } finally {
            if (null != conn && !conn.isClosed()) {
                try {
                    conn.close();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

}

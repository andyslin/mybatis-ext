package org.autumn.mybatis.common.meta.service.impl;

import org.autumn.mybatis.common.meta.domain.Query;
import org.autumn.mybatis.common.meta.service.DatabaseMetaService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class DefaultDatabaseMetaService implements DatabaseMetaService {

    private static final Pattern pattern = Pattern.compile("(^|\\s)select\\s+(\\*|\\w)", Pattern.CASE_INSENSITIVE);

    private static final MetaParser TABLE_PARSER = new TableParser();

    private static final MetaParser SQL_PARSER = new SqlParser();

    @Override
    public Query parseTableNameOrSql(Connection connection, String tableNameOrSql) throws SQLException {
        if (pattern.matcher(tableNameOrSql).find()) {
            return SQL_PARSER.parse(connection, tableNameOrSql);
        } else {
            return TABLE_PARSER.parse(connection, tableNameOrSql);
        }
    }

    interface MetaParser {
        Query parse(Connection conn, String meta) throws SQLException;
    }

}

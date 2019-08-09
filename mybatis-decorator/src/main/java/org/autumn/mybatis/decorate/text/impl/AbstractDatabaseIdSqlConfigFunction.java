package org.autumn.mybatis.decorate.text.impl;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

public abstract class AbstractDatabaseIdSqlConfigFunction extends AbstractSqlConfigFunction {

    @Override
    public String eval(Configuration configuration, String[] args) {
        String databaseId = configuration.getDatabaseId();
        Environment environment = configuration.getEnvironment();
        if (null == databaseId && null != environment) {
            databaseId = getDatabaseId(environment.getDataSource());
            configuration.setDatabaseId(databaseId);
        }
        return this.eval(databaseId, args);
    }

    /**
     * 由子类实现
     *
     * @param databaseId
     * @param args
     *
     * @return
     */
    protected abstract String eval(String databaseId, String[] args);

    private String getDatabaseId(DataSource dataSource) {
        try {
            return (String) JdbcUtils.extractDatabaseMetaData(dataSource, dbmd -> getDatabaseId(dbmd.getDatabaseProductName()));
        } catch (MetaDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断数据库类型
     *
     * @param databaseProductName
     *
     * @return
     */
    private String getDatabaseId(String databaseProductName) {
        DatabaseDriver databaseDriver = DatabaseDriver.fromProductName(databaseProductName);
        return databaseDriver.getId();
    }
}

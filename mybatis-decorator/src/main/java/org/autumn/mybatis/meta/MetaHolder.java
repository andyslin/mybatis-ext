package org.autumn.mybatis.meta;

import java.sql.SQLException;
import javax.sql.DataSource;

import org.autumn.mybatis.meta.domain.Query;
import org.autumn.mybatis.meta.mapper.ColumnPropertyMapper;
import org.autumn.mybatis.meta.mapper.impl.DefaultColumnPropertyMapper;
import org.autumn.mybatis.meta.service.DatabaseMetaService;
import org.autumn.mybatis.meta.service.impl.DefaultDatabaseMetaService;

public class MetaHolder {

    private static final ColumnPropertyMapper DEFAULT_COLUMN_PROPERTY_MAPPER = new DefaultColumnPropertyMapper();

    private static final DatabaseMetaService DEFAULT_DATABASE_META_SERVICE = new DefaultDatabaseMetaService();

    private static ColumnPropertyMapper columnPropertyMapper = DEFAULT_COLUMN_PROPERTY_MAPPER;

    private static DatabaseMetaService databaseMetaService = DEFAULT_DATABASE_META_SERVICE;

    //======列名和属性的映射器=========
    //////////////////////////

    /**
     * 注入列名和属性的映射器
     *
     * @param columnPropertyMapper
     */
    public static void setColumnPropertyMapper(ColumnPropertyMapper columnPropertyMapper) {
        if (null != columnPropertyMapper) {
            MetaHolder.columnPropertyMapper = columnPropertyMapper;
        }
    }

    /**
     * 将列名称转换为属性名
     *
     * @param columnName
     *
     * @return
     */
    public static String column2Property(String columnName) {
        return columnPropertyMapper.mapper(columnName);
    }

    //======元信息=========
    //////////////////////////

    /**
     * 注入数据库元信息的服务实现类
     *
     * @param databaseMetaService
     */
    public static void setDatabaseMetaService(DatabaseMetaService databaseMetaService) {
        if (null != databaseMetaService) {
            MetaHolder.databaseMetaService = databaseMetaService;
        }
    }

    /**
     * 解析表或SQL的结构
     *
     * @param dataSource
     * @param tableNameOrSql
     *
     * @return
     */
    public static Query parseTableNameOrSql(DataSource dataSource, String tableNameOrSql) {
        try {
            return databaseMetaService.parseTableNameOrSql(dataSource, tableNameOrSql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

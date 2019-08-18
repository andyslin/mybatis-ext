package org.autumn.mybatis.common.meta;

import org.autumn.mybatis.MybatisBootApplicationTests;
import org.autumn.mybatis.common.meta.domain.Query;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

public class MetaHolderTest extends MybatisBootApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testParseSql() {
        Query query = MetaHolder.parseTableNameOrSql(dataSource, "select * from PF_USER");
        Assert.assertTrue(query.isSql());
        // 通过SQL语句获取不到注释
        Assert.assertFalse(StringUtils.hasLength(query.getComment()));
        System.out.println(query);
    }

    @Test
    public void testParseTable() {
        Query query = MetaHolder.parseTableNameOrSql(dataSource, "PF_USER");
        Assert.assertFalse(query.isSql());
        // 通过表名可以获取到注释（如果数据库中有的话）
        Assert.assertEquals("用户表", query.getComment());
        System.out.println(query);
    }
}

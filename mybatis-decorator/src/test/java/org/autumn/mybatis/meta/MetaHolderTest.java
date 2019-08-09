package org.autumn.mybatis.meta;

import javax.sql.DataSource;

import org.autumn.mybatis.MybatisBootApplicationTests;
import org.autumn.mybatis.meta.domain.Query;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MetaHolderTest extends MybatisBootApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testParseSql() {
        Query query = MetaHolder.parseTableNameOrSql(dataSource, " select * from PF_USER");
        Assert.assertTrue(query.isSql());
        System.out.println(query);
    }

    @Test
    public void testGetTables() {
        Query query = MetaHolder.parseTableNameOrSql(dataSource, "PF_USER");
        Assert.assertFalse(query.isSql());
        System.out.println(query);
    }
}

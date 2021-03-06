package org.autumn.mybatis.decorate.node.bind.impl;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.meta.MetaHolder;
import org.autumn.mybatis.common.meta.domain.Query;
import org.autumn.mybatis.decorate.node.bind.BindFunction;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class AbstractMetadataBindFunction implements BindFunction {

    // 逗号，分隔多个参数
    private static final Pattern COMMA = Pattern.compile("\\s*(,)\\s*");
    private static final Pattern EQUAL = Pattern.compile("\\s*[=]\\s*");

    @Override
    public void eval(Configuration configuration, Element bind, String subName, String bindValue) {
        Environment environment = configuration.getEnvironment();
        if (null == environment || null == environment.getDataSource()) {
            return;
        }

        String[] args = COMMA.split(bindValue);
        String alias = "";
        String tableName = args[0];
        int index = tableName.indexOf('.');
        if (-1 != index) {
            alias = tableName.substring(0, index + 1);
            tableName = tableName.substring(index + 1);
        }

        Query query = MetaHolder.parseTableNameOrSql(environment.getDataSource(), tableName);
        Set<String> excludes = new HashSet<>();
        Map<String, String> vars = new HashMap<>();

        if (args.length >= 1) {
            for (int i = 1, l = args.length; i < l; i++) {
                String[] arg = EQUAL.split(args[i]);
                if (arg.length == 1) {
                    excludes.add(arg[0]);
                } else {
                    vars.put(arg[0], arg[1]);
                }
            }
        }
        this.eval(configuration, bind, subName, alias, query, excludes, vars);
    }

    /**
     * @param configuration Mybatis配置对象
     * @param bind          <bind.../>节点元素
     * @param subName       子函数名称：<bind.../>元素的name属性中点号后面的部分，如fields.set中的set，若没有子函数，则为null
     * @param alias         表的别名：<bind.../>元素的value属性中点号前面（含点号）的部分，如U.PF_USER中的“U.”，若没有点号，则为空格
     * @param query         表对象，从DB中读取
     * @param excludes      需排除的字段，<bind.../>元素的value属性中按逗号分隔的集合（去掉第一部分和包含=的部分，第一部分表示表名，包含=表示键值对，存储在vars中）
     * @param vars          <bind.../>元素的value属性中按逗号分隔后含等号的部分组成的键值对
     */
    protected abstract void eval(Configuration configuration, Element bind, String subName, String alias, Query query, Set<String> excludes, Map<String, String> vars);
}

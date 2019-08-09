package org.autumn.mybatis.decorate.text.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.autumn.mybatis.decorate.text.SqlConfigFunction;
import org.autumn.mybatis.decorate.text.SqlConfigFunctionFactory;

public class BaseSqlConfigFunctionFactory implements SqlConfigFunctionFactory {

    @Override
    public Collection<SqlConfigFunction> getSqlConfigFunctions() {
        List<SqlConfigFunction> functions = new ArrayList<>();
        functions.add(new ConcatSqlConfigFunction());
        functions.add(new DecodeSqlConfigFunction());
        functions.addAll(new LikeSqlConfigFunctionFactory().getSqlConfigFunctions());
        return functions;
    }
}

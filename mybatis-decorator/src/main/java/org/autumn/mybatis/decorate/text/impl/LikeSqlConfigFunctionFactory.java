package org.autumn.mybatis.decorate.text.impl;

import java.util.Arrays;
import java.util.Collection;

import org.autumn.mybatis.decorate.text.SqlConfigFunction;
import org.autumn.mybatis.decorate.text.SqlConfigFunctionFactory;

/* package */ class LikeSqlConfigFunctionFactory implements SqlConfigFunctionFactory {

    @Override
    public Collection<SqlConfigFunction> getSqlConfigFunctions() {
        return Arrays.asList(getLeftLikeSqlConfigFunction(), getRightLikeSqlConfigFunction(), getLikeSqlConfigFunction());
    }

    private SqlConfigFunction getLeftLikeSqlConfigFunction() {
        return new AbstractLikeSqlConfigFunction() {
            @Override
            public String getName() {
                return "llike";
            }

            @Override
            protected String eval(String arg) {
                return "LIKE $concat{'%'," + arg + "}";
            }
        };
    }

    private SqlConfigFunction getRightLikeSqlConfigFunction() {
        return new AbstractLikeSqlConfigFunction() {
            @Override
            public String getName() {
                return "rlike";
            }

            @Override
            protected String eval(String arg) {
                return "LIKE $concat{" + arg + ", '%'}";
            }
        };
    }

    private SqlConfigFunction getLikeSqlConfigFunction() {
        return new AbstractLikeSqlConfigFunction() {
            @Override
            public String getName() {
                return "like";
            }

            @Override
            protected String eval(String arg) {
                return "LIKE $concat{'%'," + arg + ", '%'}";
            }
        };
    }

    private abstract class AbstractLikeSqlConfigFunction extends AbstractDatabaseIdSqlConfigFunction {
        @Override
        protected String eval(String databaseId, String[] args) {
            assertEqualArgsCount(args, 1);
            return eval(args[0]);
        }

        protected abstract String eval(String arg);
    }
}

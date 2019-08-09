package org.autumn.mybatis.decorate.node.bind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.autumn.mybatis.decorate.node.SqlNodeDecorator;
import org.autumn.mybatis.decorate.node.SqlNodeDecoratorFactory;

public class BaseSqlNodeDecoratorFactory implements SqlNodeDecoratorFactory {

    @Override
    public Collection<SqlNodeDecorator> getSqlNodeDecorators() {
        List<SqlNodeDecorator> sqlNodeDecorators = new ArrayList<>();
        sqlNodeDecorators.add(new BindFunctionDecorator());
        return sqlNodeDecorators;
    }
}

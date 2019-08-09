package org.autumn.mybatis.decorate.node;

import java.util.Collection;

public interface SqlNodeDecoratorFactory {

    Collection<SqlNodeDecorator> getSqlNodeDecorators();
}

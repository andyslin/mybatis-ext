package org.autumn.mybatis.decorate.text;

import java.util.Collection;

public interface SqlConfigFunctionFactory {

    /**
     * 产生一组SQL配置函数
     *
     * @return
     */
    Collection<SqlConfigFunction> getSqlConfigFunctions();
}

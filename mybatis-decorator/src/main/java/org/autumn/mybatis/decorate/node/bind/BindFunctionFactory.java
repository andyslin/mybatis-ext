package org.autumn.mybatis.decorate.node.bind;

import java.util.Collection;

public interface BindFunctionFactory {

    /**
     * 产生一组Bind函数
     *
     * @return
     */
    Collection<BindFunction> getBindFunctions();
}

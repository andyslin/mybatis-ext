package org.autumn.mybatis.decorate.text;

import org.apache.ibatis.session.Configuration;

public interface SqlConfigFunction {

    /**
     * 函数名称
     *
     * @return
     */
    String getName();

    /**
     * 执行函数
     *
     * @param configuration
     * @param args
     *
     * @return
     */
    String eval(Configuration configuration, String[] args);
}

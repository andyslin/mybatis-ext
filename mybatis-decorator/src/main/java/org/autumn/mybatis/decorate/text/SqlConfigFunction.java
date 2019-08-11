package org.autumn.mybatis.decorate.text;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.registry.Named;

public interface SqlConfigFunction extends Named {

    /**
     * 执行函数
     *
     * @param configuration
     * @param args
     * @return
     */
    String eval(Configuration configuration, String[] args);
}

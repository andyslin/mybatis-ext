package org.autumn.mybatis.decorate.node;

import org.apache.ibatis.session.Configuration;
import org.w3c.dom.Node;

public interface SqlNodeDecorator {

    /**
     * 是否适配
     *
     * @param configuration
     * @param node
     *
     * @return
     */
    boolean supports(Configuration configuration, Node node);

    /**
     * 装饰
     *
     * @param configuration
     * @param node
     */
    void decorate(Configuration configuration, Node node);
}

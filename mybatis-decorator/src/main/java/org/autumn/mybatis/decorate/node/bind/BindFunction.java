package org.autumn.mybatis.decorate.node.bind;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.registry.Named;
import org.w3c.dom.Node;

public interface BindFunction extends Named {

    void eval(Configuration configuration, Node node, String subName, String bindValue);
}

package org.autumn.mybatis.decorate.node.bind;

import org.apache.ibatis.session.Configuration;
import org.w3c.dom.Node;

public interface BindFunction {

    String getName();

    void eval(Configuration configuration, Node node, String subName, String bindValue);
}

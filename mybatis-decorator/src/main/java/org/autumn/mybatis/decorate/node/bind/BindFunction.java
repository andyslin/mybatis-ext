package org.autumn.mybatis.decorate.node.bind;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.registry.Named;
import org.w3c.dom.Element;

public interface BindFunction extends Named {

    void eval(Configuration configuration, Element bind, String subName, String bindValue);
}

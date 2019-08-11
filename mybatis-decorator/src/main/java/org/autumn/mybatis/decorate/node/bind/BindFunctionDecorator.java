package org.autumn.mybatis.decorate.node.bind;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.decorate.DecorateRegistry;
import org.autumn.mybatis.decorate.node.SqlNodeDecorator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BindFunctionDecorator implements SqlNodeDecorator {

    /**
     * <bind.../>元素，且name以#开头
     *
     * @param configuration
     * @param node
     * @return
     */
    @Override
    public boolean supports(Configuration configuration, Node node) {
        if (node instanceof Element && "bind".equals(node.getNodeName())) {
            Element element = (Element) node;
            return element.getAttribute("name").startsWith("#");
        }
        return false;
    }

    /**
     * 调用具体的bind函数处理Node
     *
     * @param configuration
     * @param node
     */
    @Override
    public void decorate(Configuration configuration, Node node) {
        DecorateRegistry.evalBindFunction(configuration, node);
    }
}

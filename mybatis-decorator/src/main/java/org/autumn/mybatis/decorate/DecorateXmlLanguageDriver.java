package org.autumn.mybatis.decorate;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DecorateXmlLanguageDriver extends XMLLanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
        Node node = script.getNode();
        //循环处理所有装饰元素（包括装饰过程中新产生的装饰元素）
        while (nodeDecorate(configuration, node)) {
        }
        textDecorate(configuration, node);
        // 调用父类的方法
        return super.createSqlSource(configuration, script, parameterType);
    }

    private boolean nodeDecorate(Configuration configuration, Node node) {
        short nodeType = node.getNodeType();
        // 只处理元素和属性两种类型
        if (Node.ELEMENT_NODE == nodeType || Node.ATTRIBUTE_NODE == nodeType) {
            if (DecorateRegistry.decorateSqlNode(configuration, node)) {
                return true;
            }
        }
        NodeList children = node.getChildNodes();
        for (int i = 0, l = children.getLength(); i < l; i++) {
            Node child = children.item(i);
            if (nodeDecorate(configuration, child)) {
                return true;
            }
        }
        return false;
    }

    private void textDecorate(Configuration configuration, Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0, l = children.getLength(); i < l; i++) {
            Node child = children.item(i);
            switch (child.getNodeType()) {
                case Node.CDATA_SECTION_NODE:
                case Node.TEXT_NODE:
                    String content = child.getTextContent();
                    String newContent = DecorateRegistry.evalSqlConfigFunction(configuration, content);
                    if (!content.equals(newContent)) {
                        child.setTextContent(newContent);
                    }
                    break;
                case Node.ELEMENT_NODE:
                    textDecorate(configuration, child);
                    break;
                default:
                    continue;
            }
        }
    }
}

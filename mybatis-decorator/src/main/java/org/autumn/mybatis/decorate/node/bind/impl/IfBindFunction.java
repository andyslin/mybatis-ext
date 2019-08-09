package org.autumn.mybatis.decorate.node.bind.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.decorate.node.bind.BindFunction;
import org.autumn.mybatis.meta.MetaHolder;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * if语法：[and|or,]columnName[,property]
 * 子函数：if.like, if.llike, if.rlike
 */
/*package*/  class IfBindFunction implements BindFunction {

    private static final Pattern ARGS = Pattern.compile("^\\s*((and|or)\\s*,)?\\s*([^,]+)\\s*(,\\s*(.*))?\\s*$", Pattern.CASE_INSENSITIVE);

    @Override
    public String getName() {
        return "if";
    }

    @Override
    public void eval(Configuration configuration, Node node, String subName, String bindValue) {
        Matcher matcher = ARGS.matcher(bindValue);
        if (!matcher.find()) {//不符合if函数的语法
            return;
        }
        String andOr = matcher.group(2);
        if (!StringUtils.hasText(andOr)) {
            andOr = "";
        } else {
            andOr = andOr + " ";
        }
        String column = matcher.group(3);
        String property = matcher.group(5);
        if (!StringUtils.hasText(property)) {
            property = MetaHolder.column2Property(column);
        }

        Document document = node.getOwnerDocument();
        Element element = document.createElement("if");
        element.setAttribute("test", " null != " + property + " and '' != " + property);
        if ("like".equalsIgnoreCase(subName)) {
            element.setTextContent(andOr + column + " $like{#{" + property + ",jdbcType=VARCHAR}}");
        } else if ("llike".equalsIgnoreCase(subName)) {
            element.setTextContent(andOr + column + " $llike{#{" + property + ",jdbcType=VARCHAR}}");
        } else if ("rlike".equalsIgnoreCase(subName)) {
            element.setTextContent(andOr + column + " $rlike{#{" + property + ",jdbcType=VARCHAR}}");
        } else {
            element.setTextContent(andOr + column + " = #{" + property + ",jdbcType=VARCHAR}");
        }
        node.getParentNode().replaceChild(element, node);
    }
}

package org.autumn.mybatis.decorate.node.bind.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.decorate.XmlHolder;
import org.autumn.mybatis.decorate.node.bind.BindFunction;
import org.autumn.mybatis.meta.MetaHolder;
import org.springframework.util.StringUtils;
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
        StringBuilder xml = new StringBuilder();

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

        xml.append("<if test=\"").append("null != ").append(property).append(" and '' != ").append(property).append("\">");
        xml.append(andOr).append(column).append(" ");
        if ("like".equals(subName) || "llike".equals(subName) || "rlike".equals(subName)) {
            // 这里使用了$like{}配置函数
            xml.append("$").append(subName).append("{#{").append(property).append(",jdbcType=VARCHAR}}");
        } else {
            xml.append("= #{").append(property).append(",jdbcType=VARCHAR}");
        }
        xml.append("</if>");
        XmlHolder.replaceNode(node, xml.toString());
    }
}

package org.autumn.mybatis.decorate.node.bind.impl;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.meta.MetaHolder;
import org.autumn.mybatis.decorate.XmlHolder;
import org.autumn.mybatis.decorate.node.bind.BindFunction;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;

import java.util.regex.Pattern;

/**
 * if语法：[where,]([and|or] [like|llike|rlike|>|>=] column[|property] [true|false]){1,}
 * <p>
 */
/*package*/  class IfBindFunction implements BindFunction {

    // 逗号，分隔多个参数
    private static final Pattern COMMA = Pattern.compile("\\s*(,)\\s*");
    // 空格，分隔多个单词
    private static final Pattern BLANK = Pattern.compile("\\s+");
    // 竖线，分隔列名和属性名
    private static final Pattern VERTICAL_LINE = Pattern.compile("[|]");

    @Override
    public String getName() {
        return "if";
    }

    @Override
    public void eval(Configuration configuration, Node node, String subName, String bindValue) {
        // 最终生成的XML字符串
        StringBuilder xml = new StringBuilder();
        // 循环处理空格或逗号分隔的配置参数
        boolean addWhere = false;
        for (String arg : COMMA.split(bindValue)) {
            // 元字符
            if ("WHERE".equalsIgnoreCase(arg)) {
                addWhere = true;
            } else if (StringUtils.hasText(arg)) {
                Tuple tuple = parseTuple(configuration, arg);
                xml.append(tuple.toXml());
            }
        }
        if (addWhere) {
            xml.insert(0, "<where>").append("</where>");
        }
        XmlHolder.replaceNode(node, xml.toString());
    }

    /**
     * 解析为一个元组（连接词、操作符、列名、属性名、属性是否为boolean类型、布尔类型的取值）
     *
     * @param arg
     * @return
     */
    private Tuple parseTuple(Configuration configuration, String arg) {
        Tuple tuple = new Tuple();
        String[] words = BLANK.split(arg);
        if (1 == words.length) {//只有一个单词
            tuple.column = words[0];
        } else if (2 == words.length) {//两个单词
            if ("and".equalsIgnoreCase(words[0]) || "or".equalsIgnoreCase(words[0])) {
                tuple.join = words[0];
            } else {
                tuple.operate = words[0].toLowerCase();
            }
            tuple.column = words[1];
        } else if (3 <= words.length) {//有三个或三个以上单词
            tuple.join = words[0];
            tuple.operate = words[1].toLowerCase();
            tuple.column = words[2];
            if (words.length >= 4) {
                tuple.isBoolean = true;
                tuple.booleanValue = Boolean.parseBoolean(words[3]);
            }
        }

        // 字段|属性
        String[] arr = VERTICAL_LINE.split(tuple.column);
        tuple.property = arr.length >= 2 ? arr[1] : MetaHolder.column2Property(configuration, tuple.column);
        return tuple;
    }

    private static class Tuple {
        private String join = "and";
        private String operate = "=";
        private String column = "";
        private String property = "";
        private boolean isBoolean = false;
        private boolean booleanValue = true;

        private String toXml() {
            StringBuilder xml = new StringBuilder();
            if (isBoolean) {
                xml.append("<if test=\"").append(booleanValue ? "" : "!").append(property).append("\">");
            } else {
                xml.append("<if test=\"").append("null != ").append(property).append(" and '' != ").append(property).append("\">");
            }
            xml.append(" ").append(join).append(" ").append(column).append(" ");
            if ("like".equals(operate) || "llike".equals(operate) || "rlike".equals(operate)) {
                // 这里使用了$like{}配置函数
                xml.append("$").append(operate).append("{#{").append(property).append(",jdbcType=VARCHAR}}");
            } else {
                xml.append(operate).append(" #{").append(property).append(",jdbcType=VARCHAR}");
            }
            xml.append("</if>");
            return xml.toString();
        }
    }
}

package org.autumn.mybatis.decorate;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.registry.GenericRegistry;
import org.autumn.mybatis.common.registry.GenericRegistry.NamedRegistry;
import org.autumn.mybatis.common.registry.GenericRegistry.OrderedRegistry;
import org.autumn.mybatis.decorate.node.SqlNodeDecorator;
import org.autumn.mybatis.decorate.node.SqlNodeDecoratorFactory;
import org.autumn.mybatis.decorate.node.bind.BaseSqlNodeDecoratorFactory;
import org.autumn.mybatis.decorate.node.bind.BindFunction;
import org.autumn.mybatis.decorate.node.bind.BindFunctionFactory;
import org.autumn.mybatis.decorate.node.bind.impl.BaseBindFunctionFactory;
import org.autumn.mybatis.decorate.text.SqlConfigFunction;
import org.autumn.mybatis.decorate.text.SqlConfigFunctionFactory;
import org.autumn.mybatis.decorate.text.impl.BaseSqlConfigFunctionFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public class DecorateRegistry {

    /**
     * SQL配置函数注册器
     */
    private static final NamedRegistry<SqlConfigFunction> sqlConfigFunctionNamedRegistry = GenericRegistry.getNamedRegistry(SqlConfigFunction.class);

    /**
     * SQL配置节点装饰器的注册器
     */
    private static final OrderedRegistry<SqlNodeDecorator> sqlNodeDecoratorOrderedRegistry = GenericRegistry.getOrderedRegistry(SqlNodeDecorator.class);

    /**
     * Bind函数注册器
     */
    private static final NamedRegistry<BindFunction> bindFunctionNamedRegistry = GenericRegistry.getNamedRegistry(BindFunction.class);

    static {
        registerSqlConfigFunctionFactory(new BaseSqlConfigFunctionFactory());
        registerSqlNodeDecoratorFactory(new BaseSqlNodeDecoratorFactory());
        registerBindFunctionFactory(new BaseBindFunctionFactory());
    }

    //======SQL配置函数=========
    //////////////////////////

    /**
     * 执行SQL配置函数
     *
     * @param configuration
     * @param context
     * @return
     */
    public static String evalSqlConfigFunction(Configuration configuration, String context) {
        return SqlConfigFunctionParser.eval(configuration, context);
    }

    /**
     * 注册SQL配置函数
     *
     * @param sqlConfigFunctions
     */
    public static void registerSqlConfigFunction(SqlConfigFunction... sqlConfigFunctions) {
        sqlConfigFunctionNamedRegistry.register(sqlConfigFunctions);
    }

    /**
     * 注册SQL配置函数工厂
     *
     * @param sqlConfigFunctionFactories
     */
    public static void registerSqlConfigFunctionFactory(SqlConfigFunctionFactory... sqlConfigFunctionFactories) {
        if (null != sqlConfigFunctionFactories) {
            Arrays.stream(sqlConfigFunctionFactories)
                    .filter(Objects::nonNull)
                    .map(SqlConfigFunctionFactory::getSqlConfigFunctions)
                    .filter(Objects::nonNull)
                    .forEach(sqlConfigFunctionNamedRegistry::register);
        }
    }

    //======SQL节点装饰器=========
    //////////////////////////

    /**
     * 装饰SQL配置节点
     *
     * @param configuration
     * @param node
     * @return
     */
    public static boolean decorateSqlNode(Configuration configuration, Node node) {
        for (SqlNodeDecorator sqlNodeDecorator : sqlNodeDecoratorOrderedRegistry.get()) {
            if (sqlNodeDecorator.supports(configuration, node)) {
                sqlNodeDecorator.decorate(configuration, node);
                return true;
            }
        }
        return false;
    }

    /**
     * 注册SQL配置节点装饰器
     *
     * @param sqlNodeDecorators
     */
    public static void registerSqlNodeDecorator(SqlNodeDecorator... sqlNodeDecorators) {
        sqlNodeDecoratorOrderedRegistry.register(sqlNodeDecorators);
    }

    /**
     * 注册SQL配置节点装饰器工厂
     *
     * @param sqlNodeDecoratorFactories
     */
    public static void registerSqlNodeDecoratorFactory(SqlNodeDecoratorFactory... sqlNodeDecoratorFactories) {
        if (null != sqlNodeDecoratorFactories) {
            Stream.of(sqlNodeDecoratorFactories)
                    .filter(Objects::nonNull)
                    .map(SqlNodeDecoratorFactory::getSqlNodeDecorators)
                    .filter(Objects::nonNull)
                    .forEach(sqlNodeDecoratorOrderedRegistry::register);
        }
    }

    //======Bind函数=========
    //////////////////////////

    /**
     * 执行Bind函数
     *
     * @param configuration
     * @param node
     * @return
     */
    public static void evalBindFunction(Configuration configuration, Node node) {
        Element element = (Element) node;
        String name = element.getAttribute("name").substring(1);
        int index = name.indexOf(".");
        String subName = null;
        if (-1 != index) {
            subName = name.substring(index + 1);
            name = name.substring(0, index);
        }
        BindFunction bindFunction = bindFunctionNamedRegistry.get(name);
        if (null == bindFunction) {
            throw new RuntimeException("not found bind-function [name=" + name + "]");
        }
        String value = element.getAttribute("value");
        bindFunction.eval(configuration, element, subName, value);
    }

    /**
     * 注册Bind函数
     *
     * @param bindFunctions
     */
    public static void registerBindFunction(BindFunction... bindFunctions) {
        bindFunctionNamedRegistry.register(bindFunctions);
    }

    /**
     * 注册Bind函数工厂
     *
     * @param bindFunctionFactories
     */
    public static void registerBindFunctionFactory(BindFunctionFactory... bindFunctionFactories) {
        if (null != bindFunctionFactories) {
            Arrays.stream(bindFunctionFactories)
                    .filter(Objects::nonNull)
                    .map(BindFunctionFactory::getBindFunctions)
                    .filter(Objects::nonNull)
                    .forEach(bindFunctionNamedRegistry::register);
        }
    }

    /**
     * SQL配置函数解析器
     */
    private static class SqlConfigFunctionParser {

        /**
         * 识别SQL配置函数的正则表达式，匹配形如$fn_name{args}的配置
         */
        private static final String pattern = new StringBuilder()
                .append("(?<=\\s+|^|,|\\{|\\()")//肯定逆序环视(?<=...)，这里表示前面为开头、逗号、左括号或空白字符
                .append("\\$") // 转义为"$"符号本身，和下面一起表示以$开头，并且紧跟（不能出现空白字符）一个字母或下划线开头的字母数字下划线组合

                //捕获型分组1开始，用于捕获形似$name{args}里面的名称name
                .append("(")
                .append("[_a-z](?:[_0-9a-z])*") //一个字母或下划线开头的字母数字下划线组合，其中 (?:...)表示非捕获型分组
                .append(")")
                //捕获型分组1结束

                .append("\\s*\\{\\s*")//表示"{"字符，前后可以有任意空白字符

                //捕获型分组2开始，用于捕获形似$name{args}里面的参数args
                .append("(")
                .append("([^{}]|") //这里的括号也会捕获，但是不会影响捕获组序号，表达式表示非"{"|"}"字符，或者--
                .append("((\\$|\\#)\\{[^{}]+\\})") //或者"${"|"#{"开头，中间不是"{"|"}"的字符，最后跟一个"}"字符
                //也即允许args里面出现${...}和#{...}
                .append(")*")//表示上面两种字符可以出现任意次
                .append(")")
                //捕获型分组2结束

                .append("\\s*\\}\\s*")//表示"}"字符，前面任意空白字符
                .append("(?=\\s+|$|,|\\}|\\))")//肯定顺序环视(?=...)，这里表示后面为结尾、逗号、右括号或空白字符
                .toString();
        private static final Pattern sqlConfigFunctionPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

        private static String eval(Configuration configuration, String context) {
            Matcher matcher = sqlConfigFunctionPattern.matcher(context);
            boolean trace = log.isTraceEnabled();
            while (matcher.find()) {
                String expression = matcher.group(0);
                String name = matcher.group(1);
                String args = matcher.group(2);
                String replacement = resolveSqlConfigFunction(configuration, name, resolveArgs(args));
                String after = matcher.replaceFirst(Matcher.quoteReplacement(replacement));
                if (trace) {
                    StringBuilder sb = new StringBuilder("\nsql-config-function eval ...");
                    sb.append("\n\tmatcher context    : ").append(context)
                            .append("\n\tmatcher expression : ").append(expression)
                            .append("\n\tsql config fn-name : ").append(name)
                            .append("\n\tsql config fn-args : ").append(args)
                            .append("\n\treplacement value  : ").append(replacement)
                            .append("\n\tafter replace value: ").append(after);
                    System.out.println(sb.toString());
                    log.trace(sb.toString());
                }
                context = after;
                matcher = sqlConfigFunctionPattern.matcher(context);
            }
            return context;
        }

        private static String[] resolveArgs(String arg) {
            List<String> args = new ArrayList<>();
            /**
             * -2 : 处于#{的环境 -1 : 处于${的环境 0 : 正常环境 >0 : 处于(嵌套的环境，数值表示嵌套的重数
             */
            int match = 0;
            int match2 = 0;//大括号{的重数
            char[] chs = arg.toCharArray();
            StringBuffer sb = new StringBuffer();
            for (int i = 0, l = chs.length; i < l; i++) {
                char ch = chs[i];
                if (i > 0 && chs[i - 1] == '\\') {//转义字符，删除最后的\，并插入原字符
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append(ch);
                    continue;
                } else if (match == 0 && ch == ',') {//正常环境，并且为,，则结束当前参数的解析
                    String v = sb.toString();
                    if (StringUtils.hasText(v)) {
                        args.add(v.trim());
                    }
                    sb.setLength(0);
                    continue;
                } else {
                    sb.append(ch);
                }

                switch (ch) {
                    case '#':
                        if (0 == match && i < l - 1 && chs[i + 1] == '{') {//正常环境，并且下一字符为{，切换环境
                            match = -2;
                        }
                        break;
                    case '$':
                        if (0 == match && i < l - 1 && chs[i + 1] == '{') {//正常环境，并且下一字符为{，切换环境
                            match = -1;
                        }
                        break;
                    case '{':
                        if (match == -2 || match == -1) {
                            match2++;
                        }
                        break;
                    case '}':
                        if (match == -2 || match == -1) {
                            match2--;
                            if (match2 == 0) {
                                match = 0;
                            }
                        }
                        break;
                    case '(':
                        if (match >= 0) {//正常环境，或已处于小括号环境，嵌套重数加1
                            match++;
                        }
                        break;
                    case ')':
                        if (match > 0) {//处于小括号环境，嵌套重数减1
                            match--;
                        }
                        break;
                    default:
                        break;
                }
            }
            String v = sb.toString();
            if (StringUtils.hasText(v)) {
                args.add(v.trim());
            }

            return args.toArray(new String[args.size()]);
        }

        private static String resolveSqlConfigFunction(Configuration configuration, String name, String[] args) {
            SqlConfigFunction fn = sqlConfigFunctionNamedRegistry.get(name);
            if (null == fn) {
                throw new RuntimeException("not found the sql-config-function [" + name + "]");
            }
            String rs = fn.eval(configuration, args);
            return rs == null ? "" : rs;
        }
    }
}

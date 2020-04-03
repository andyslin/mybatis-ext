package org.autumn.mybatis.mapperhandler.binding.handler.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.scripting.xmltags.OgnlCache;
import org.apache.ibatis.session.SqlSession;
import org.autumn.mybatis.mapperhandler.annotation.BatchParam;
import org.autumn.mybatis.mapperhandler.annotation.Execute;
import org.autumn.mybatis.mapperhandler.annotation.Executes;
import org.autumn.mybatis.mapperhandler.annotation.SqlRef;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Copy Right Information : @Copyright@ <br>
 * Project : @Project@ <br>
 * Description : 批量执行处理器<br>
 * Author : andyslin <br>
 * Version : 0.0.1 <br>
 * Date : 2018-12-24<br>
 */
@Slf4j
public class BatchMapperHandler extends AbstractTransactionMapperHandler {

    public BatchMapperHandler() {
        super();
    }

    public BatchMapperHandler(int order) {
        super(order);
    }

    @Override
    public boolean supports(MapperHandlerContext context) {
        return int[][].class.equals(context.getMethod().getReturnType())
                || int[].class.equals(context.getMethod().getReturnType());
    }

    /**
     * 批量执行
     *
     * <pre>
     * 1. 一个SQL，不同参数执行多次
     * 2. 不同SQL，相同参数执行多次
     * 3. 不同SQL，对应各自不同的参数执行多次
     * 4. 以上情况的混合批量
     * </pre>
     */
    @Override
    public Object execute(SqlSession sqlSession, Object[] args, MapperHandlerContext context) {
        final List<SqlIdAndParameter> sqlIdAndParameters = new ArrayList<>();

        Object param = context.getParamResolver().getNamedParams(args);

        Method method = context.getMethod();
        if (null != method.getAnnotation(Executes.class)) {//混合批量
            resolveExecutes(sqlIdAndParameters, param, method);
        } else {
            resolveMethod(sqlIdAndParameters, param, method, args);
        }
        return this.executeWithTransaction(sqlSession, context, new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                return executeBatch(sqlSession, sqlIdAndParameters, context);
            }
        });
    }

    /**
     * 执行批量
     *
     * @param sqlSession
     * @param sqlIdAndParameters
     * @param context
     *
     * @return
     */
    private Object executeBatch(SqlSession sqlSession, List<SqlIdAndParameter> sqlIdAndParameters, MapperHandlerContext context) {
        for (SqlIdAndParameter sap : sqlIdAndParameters) {
            sqlSession.update(sap.sqlId, sap.parameter);
        }
        List<BatchResult> flushStatements = sqlSession.flushStatements();
        return resolveBatchResult(context, flushStatements);
    }

    /**
     * 解析批量结果
     *
     * @param context
     * @param flushStatements
     *
     * @return
     */
    private Object resolveBatchResult(MapperHandlerContext context, List<BatchResult> flushStatements) {
        if (int[].class.equals(context.getMethod().getReturnType())) {
            int[] rs = new int[flushStatements.size()];
            for (int i = 0, size = flushStatements.size(); i < size; i++) {
                int count = 0;
                for (int uc : flushStatements.get(i).getUpdateCounts()) {
                    count += uc;
                }
                rs[i] = count;
            }
            return rs;
        } else {
            int[][] rs = new int[flushStatements.size()][];
            for (int i = 0, size = flushStatements.size(); i < size; i++) {
                rs[i] = flushStatements.get(i).getUpdateCounts();
            }
            return rs;
        }
    }

    /**
     * 解析混合批量参数
     *
     * @param sqlIdAndParameters
     * @param param
     * @param method
     */
    private void resolveExecutes(List<SqlIdAndParameter> sqlIdAndParameters, Object param, Method method) {
        Execute[] executes = method.getAnnotation(Executes.class).value();
        for (Execute execute : executes) {
            String sqlId = resolveSqlId(method, execute.sqlRef());
            if (this.evaluateCondition(param, execute.condition())) {
                Object p = evaluateParam(param, execute.property());
                if (execute.batch()) {//本身又是一个批量，这时只能是一个SQL不同参数多次执行的批量
                    if (null == p) {
                        continue;
                    }
                    if (!this.isBatchParamType(p.getClass())) {
                        throw new IllegalStateException("interface method: [" + method + "], the param can't convert to batch param, sqlId:[" + sqlId + "]");
                    }
                    List<Object> list = convertToList(p, Object.class);
                    for (int i = 0, size = list.size(); i < size; i++) {
                        Object pp = getOneBatchParam(param, execute.name(), execute.index(), i, list.get(i));
                        sqlIdAndParameters.add(new SqlIdAndParameter(sqlId, pp));
                    }
                } else {//如果不是内嵌批量
                    sqlIdAndParameters.add(new SqlIdAndParameter(sqlId, p));
                }
            } else {
                log.debug("interface method: [" + method + "], not match condition:[" + execute.condition() + "], don't execute sqlId:[" + sqlId + "]");
            }
        }
    }

    /**
     * 解析批量参数
     *
     * @param sqlIdAndParameters
     * @param param
     * @param method
     * @param args
     */
    private void resolveMethod(List<SqlIdAndParameter> sqlIdAndParameters, Object param, Method method, Object[] args) {
        BatchParam batchParamAnno = null;
        Object batchParam = param;

        //查找含有BatchParam参数的注解
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        outer:
        for (int i = 0, length = paramAnnotations.length; i < length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof BatchParam) {
                    batchParamAnno = (BatchParam) annotation;
                    batchParam = evaluateParam(args[i], batchParamAnno.property());
                    break outer;
                }
            }
        }

        String itemName = "item";
        String indexName = "index";
        boolean oneByOne = false;
        if (null != batchParamAnno) {
            itemName = batchParamAnno.name();
            indexName = batchParamAnno.index();
            oneByOne = batchParamAnno.oneByOne();
        }

        SqlRef sqlRef = method.getAnnotation(SqlRef.class);
        if (null == sqlRef || sqlRef.value().length <= 1) {//单SQL
            String sr = (null != sqlRef && sqlRef.value().length == 1) ? sqlRef.value()[0] : null;
            String sqlId = resolveSqlId(method, sr);// 一个SQL
            if (null == batchParam || !this.isBatchParamType(batchParam.getClass())) {
                throw new IllegalStateException("interface method: [" + method + "], the param can't convert to batch param, sqlId:[" + sqlId + "]");
            }
            List<Object> list = convertToList(batchParam, Object.class);
            for (int i = 0, size = list.size(); i < size; i++) {
                Object pp = getOneBatchParam(param, itemName, indexName, i, list.get(i));
                sqlIdAndParameters.add(new SqlIdAndParameter(sqlId, pp));
            }
        } else {//多SQL
            String[] sqlRefs = sqlRef.value();
            int length = sqlRefs.length;
            if (oneByOne) {//sqlId和参数一一对应
                if (null == batchParam || !this.isBatchParamType(batchParam.getClass())) {
                    throw new IllegalStateException("interface method: [" + method + "], the param can't convert to batch param");
                }
                List<Object> list = convertToList(batchParam, Object.class);
                if (length != list.size()) {
                    throw new IllegalStateException("interface method: [" + method + "], the batch sqlId's number is " + length + ", but the batch param's number is " + list.size() + "]");
                }
                for (int i = 0; i < length; i++) {
                    String sqlId = this.resolveSqlId(method, sqlRefs[i]);
                    Object pp = getOneBatchParam(param, itemName, indexName, i, list.get(i));
                    sqlIdAndParameters.add(new SqlIdAndParameter(sqlId, pp));
                }
            } else if (null != batchParam && this.isBatchParamType(batchParam.getClass())) {//参数是迭代类型
                List<Object> list = convertToList(batchParam, Object.class);
                for (int i = 0; i < length; i++) {
                    String sqlId = this.resolveSqlId(method, sqlRefs[i]);
                    for (int j = 0, size = list.size(); j < size; j++) {
                        Object pp = getOneBatchParam(param, itemName, indexName, j, list.get(j));
                        sqlIdAndParameters.add(new SqlIdAndParameter(sqlId, pp));
                    }
                }
            } else {//参数不是迭代类型，每次执行参数都相同
                for (int i = 0; i < length; i++) {
                    String sqlId = this.resolveSqlId(method, sqlRefs[i]);
                    sqlIdAndParameters.add(new SqlIdAndParameter(sqlId, batchParam));
                }
            }
        }
    }

    /**
     * 解析SqlId
     *
     * @param method
     * @param sqlRef
     *
     * @return
     */
    private String resolveSqlId(Method method, String sqlRef) {
        if (!StringUtils.hasText(sqlRef)) {
            return method.getDeclaringClass().getName() + "." + method.getName();
        } else {
            return method.getDeclaringClass().getName() + "." + sqlRef;
        }
    }

    /**
     * 获取批量中的一次执行的参数
     *
     * @param commParam
     * @param batchParamName
     * @param batchIndexName
     * @param index
     * @param arg
     *
     * @return
     */
    private Object getOneBatchParam(Object commParam, String batchParamName, String batchIndexName, int index, Object arg) {
        final Map<String, Object> param = new HashMap<>();
        if (commParam instanceof Map) {
            param.putAll((Map<String, Object>) commParam);
        } else {
            param.put("param", commParam);
        }
        if (StringUtils.hasText(batchIndexName)) {
            param.put(batchIndexName, index);
        }
        if (StringUtils.hasText(batchParamName)) {
            param.put(batchParamName, arg);
        }
        return param;
    }

    /**
     * 求表达式的值
     *
     * @param root
     * @param property
     *
     * @return
     */
    private Object evaluateParam(Object root, String property) {
        if (!"this".equals(property) && StringUtils.hasText(property)) {
            Object rs = OgnlCache.getValue(property, root);
            //Object rs = SpEL.getValue(root, property);
            return rs;
        }
        return root;
    }

    private boolean evaluateCondition(Object root, String condition) {
        if (StringUtils.hasText(condition)) {
            Object c = OgnlCache.getValue(condition, root);
            if (!(c instanceof Boolean && ((Boolean) c).booleanValue())) {
                return false;
            }
        }
        return true;
        //return Utils.isBlank(condition) || SpEL.getValue(root, condition, boolean.class);
    }

    /**
     * 是否可转换为批量类型的参数
     *
     * @param cls
     *
     * @return
     */
    private boolean isBatchParamType(Class<?> cls) {
        return cls.isArray() || Iterator.class.isAssignableFrom(cls) || Enumeration.class.isAssignableFrom(cls)
                || Iterable.class.isAssignableFrom(cls); // 因此包含Collection，从而也包含List、Set、Queue等常见集合类型
    }

    /**
     * 转换为List类型，如果不为空，只校验第一个元素的类型是否匹配
     *
     * @param source      原始对象
     * @param elementType 元素类型
     *
     * @return 转换后的集合
     */
    private <E> List<E> convertToList(Object source, Class<E> elementType) {
        if (null == source) {
            return null;
        } else if (source.getClass().isArray()) {
            List<E> list = new ArrayList<>();
            Class<?> c = source.getClass();
            if (c.equals(int[].class)) {
                int[] arr = (int[]) source;
                if (arr.length == 0) {
                    return Collections.<E>emptyList();
                } else if (elementType.isAssignableFrom(Integer.class)) {
                    for (Integer a : arr) {
                        list.add((E) a);
                    }
                    return list;
                } else {
                    throw new ClassCastException("can not convert the list, the element type is int, but the target type is " + elementType);
                }
            } else if (c.equals(short[].class)) {
                short[] arr = (short[]) source;
                if (arr.length == 0) {
                    return Collections.<E>emptyList();
                } else if (elementType.isAssignableFrom(Short.class)) {
                    for (Short a : arr) {
                        list.add((E) a);
                    }
                    return list;
                } else {
                    throw new ClassCastException("can not convert the list, the element type is short, but the target type is " + elementType);
                }
            } else if (c.equals(long[].class)) {
                long[] arr = (long[]) source;
                if (arr.length == 0) {
                    return Collections.<E>emptyList();
                } else if (elementType.isAssignableFrom(Long.class)) {
                    for (Long a : arr) {
                        list.add((E) a);
                    }
                    return list;
                } else {
                    throw new ClassCastException("can not convert the list, the element type is long, but the target type is " + elementType);
                }
            } else if (c.equals(byte[].class)) {
                byte[] arr = (byte[]) source;
                if (arr.length == 0) {
                    return Collections.<E>emptyList();
                } else if (elementType.isAssignableFrom(Byte.class)) {
                    for (Byte a : arr) {
                        list.add((E) a);
                    }
                    return list;
                } else {
                    throw new ClassCastException("can not convert the list, the element type is byte, but the target type is " + elementType);
                }
            } else if (c.equals(boolean[].class)) {
                boolean[] arr = (boolean[]) source;
                if (arr.length == 0) {
                    return Collections.<E>emptyList();
                } else if (elementType.isAssignableFrom(Boolean.class)) {
                    for (Boolean a : arr) {
                        list.add((E) a);
                    }
                    return list;
                } else {
                    throw new ClassCastException("can not convert the list, the element type is boolean, but the target type is " + elementType);
                }
            } else if (c.equals(char[].class)) {
                char[] arr = (char[]) source;
                if (arr.length == 0) {
                    return Collections.<E>emptyList();
                } else if (elementType.isAssignableFrom(Character.class)) {
                    for (Character a : arr) {
                        list.add((E) a);
                    }
                    return list;
                } else {
                    throw new ClassCastException("can not convert the list, the element type is char, but the target type is " + elementType);
                }
            } else if (c.equals(float[].class)) {
                float[] arr = (float[]) source;
                if (arr.length == 0) {
                    return Collections.<E>emptyList();
                } else if (elementType.isAssignableFrom(Float.class)) {
                    for (Float a : arr) {
                        list.add((E) a);
                    }
                    return list;
                } else {
                    throw new ClassCastException("can not convert the list, the element type is float, but the target type is " + elementType);
                }
            } else if (c.equals(double[].class)) {
                double[] arr = (double[]) source;
                if (arr.length == 0) {
                    return Collections.<E>emptyList();
                } else if (elementType.isAssignableFrom(Double.class)) {
                    for (Double a : arr) {
                        list.add((E) a);
                    }
                    return list;
                } else {
                    throw new ClassCastException("can not convert the list, the element type is double, but the target type is " + elementType);
                }
            } else {
                Object[] arr = (Object[]) source;
                if (arr.length == 0) {
                    return Collections.<E>emptyList();
                } else {
                    Object first = arr[0];
                    if (elementType.isAssignableFrom(first.getClass())) {
                        for (Object a : arr) {
                            list.add((E) a);
                        }
                        return list;
                    } else {
                        throw new ClassCastException("can not convert the list, the element type is " + first.getClass() + ", but the target type is " + elementType);
                    }
                }
            }
        } else if (source instanceof List) {
            List<E> list = (List<E>) source;
            if (list.isEmpty()) {
                return Collections.<E>emptyList();
            } else {
                Object first = list.get(0);
                if (elementType.isAssignableFrom(first.getClass())) {
                    return list;
                } else {
                    throw new ClassCastException("can not convert the list, the element type is " + first.getClass() + ", but the target type is " + elementType);
                }
            }
        } else if (source instanceof Enumeration) {
            Enumeration<E> i = (Enumeration<E>) source;
            if (!i.hasMoreElements()) {
                return Collections.<E>emptyList();
            } else {
                Object first = i.nextElement();
                if (elementType.isAssignableFrom(first.getClass())) {
                    List<E> rs = new ArrayList<>();
                    rs.add((E) first);
                    while (i.hasMoreElements()) {
                        rs.add(i.nextElement());
                    }
                    return rs;
                } else {
                    throw new ClassCastException("can not convert the list, the element type is " + first.getClass() + ", but the target type is " + elementType);
                }
            }
        } else if (source instanceof Iterator) {
            Iterator<E> i = (Iterator<E>) source;
            if (!i.hasNext()) {
                return Collections.<E>emptyList();
            } else {
                Object first = i.next();
                if (elementType.isAssignableFrom(first.getClass())) {
                    List<E> rs = new ArrayList<>();
                    rs.add((E) first);
                    while (i.hasNext()) {
                        rs.add(i.next());
                    }
                    return rs;
                } else {
                    throw new ClassCastException("can not convert the list, the element type is " + first.getClass() + ", but the target type is " + elementType);
                }
            }
        } else if (source instanceof Iterable) {
            return convertToList(((Iterable<?>) source).iterator(), elementType);
        } else if (source instanceof Map) {
            return convertToList(((Map<?, ?>) source).values(), elementType);
        } else {
            return convertToList(new Object[] {source}, elementType);
        }
    }

    /**
     * 辅助类
     */
    private class SqlIdAndParameter {
        String sqlId;
        Object parameter;

        SqlIdAndParameter(String sqlId, Object parameter) {
            this.sqlId = sqlId;
            this.parameter = parameter;
        }
    }
}

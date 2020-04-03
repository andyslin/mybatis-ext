package org.autumn.mybatis.mapperhandler.binding.handler.impl;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;

/**
 * Copy Right Information : @Copyright@ <br>
 * Project : @Project@ <br>
 * Description :查询类执行处理器<br>
 * Author : andyslin <br>
 * Version : 0.0.1 <br>
 * Date : 2018-12-24<br>
 */
public class SelectMapperHandler extends AbstractMapperHandler {

    public SelectMapperHandler() {
        super();
    }

    public SelectMapperHandler(int order) {
        super(order);
    }

    @Override
    public boolean supports(MapperHandlerContext context) {
        MappedStatement statement = context.getMappedStatement();
        if (null != statement) {
            SqlCommandType type = statement.getSqlCommandType();
            return type.equals(SqlCommandType.SELECT);
        }
        return false;
    }

    @Override
    public Object execute(SqlSession sqlSession, Object[] args, MapperHandlerContext context) {
        Object result = null;
        Class<?> returnType = context.getReturnType();
        String sqlId = context.getMappedStatement().getId();
        Object param = context.getParamResolver().getNamedParams(args);
        ResultHandler<?> resultHandler = extractParam(args, ResultHandler.class);
        RowBounds rowBounds = extractParam(args, RowBounds.class);

        if (null != resultHandler) {
            result = this.executeResultHandler(sqlSession, sqlId, param, rowBounds, resultHandler);
        } else if (context.getConfiguration().getObjectFactory().isCollection(returnType) || returnType.isArray()) {
            result = this.executeMany(sqlSession, sqlId, param, rowBounds, returnType);
        } else if (Map.class.isAssignableFrom(context.getMethod().getReturnType()) && context.getMethod().isAnnotationPresent(MapKey.class)) {
            result = this.executeMap(sqlSession, sqlId, param, rowBounds, context);
        } else if (Cursor.class.isAssignableFrom(returnType)) {
            result = this.executeCursor(sqlSession, sqlId, param, rowBounds);
        } else {
            result = sqlSession.selectOne(sqlId, param);
        }
        return result;
    }

    protected <T> T extractParam(Object[] args, Class<T> cls) {
        if (null != args) {
            for (Object arg : args) {
                if (null != arg && cls.isAssignableFrom(arg.getClass())) {
                    return cls.cast(arg);
                }
            }
        }
        return null;
    }

    protected Object executeMany(SqlSession sqlSession, String sqlId, Object param, RowBounds rowBounds, Class<?> returnType) {
        List<?> result;
        if (null != rowBounds) {
            result = sqlSession.selectList(sqlId, param, rowBounds);
        } else {
            result = sqlSession.selectList(sqlId, param);
        }
        // issue #510 Collections & arrays support
        if (!returnType.isAssignableFrom(result.getClass())) {
            if (returnType.isArray()) {
                return convertToArray(result, returnType);
            } else {
                return convertToDeclaredCollection(sqlSession.getConfiguration(), result, returnType);
            }
        }
        return result;
    }

    protected Object executeResultHandler(SqlSession sqlSession, String sqlId, Object param, RowBounds rowBounds, ResultHandler<?> resultHandler) {
        MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(sqlId);
        if (!StatementType.CALLABLE.equals(ms.getStatementType())
                && void.class.equals(ms.getResultMaps().get(0).getType())) {
            throw new BindingException("method " + sqlId
                    + " needs either a @ResultMap annotation, a @ResultType annotation,"
                    + " or a resultType attribute in XML so a ResultHandler can be used as a parameter.");
        }

        if (null != rowBounds) {
            sqlSession.select(sqlId, param, rowBounds, resultHandler);
        } else {
            sqlSession.select(sqlId, param, resultHandler);
        }
        return null;
    }

    protected Object executeMap(SqlSession sqlSession, String sqlId, Object param, RowBounds rowBounds, MapperHandlerContext context) {
        Object result;
        String mapKey = context.getMethod().getAnnotation(MapKey.class).value();
        if (null != rowBounds) {
            result = sqlSession.selectMap(sqlId, param, mapKey, rowBounds);
        } else {
            result = sqlSession.selectMap(sqlId, param, mapKey);
        }
        return result;
    }

    protected Object executeCursor(SqlSession sqlSession, String sqlId, Object param, RowBounds rowBounds) {
        Object result;
        if (null != rowBounds) {
            result = sqlSession.selectCursor(sqlId, param, rowBounds);
        } else {
            result = sqlSession.selectCursor(sqlId, param);
        }
        return result;
    }

    private <E> Object convertToDeclaredCollection(Configuration config, List<E> list, Class<?> returnType) {
        Object collection = config.getObjectFactory().create(returnType);
        MetaObject metaObject = config.newMetaObject(collection);
        metaObject.addAll(list);
        return collection;
    }

    private <E> Object convertToArray(List<E> list, Class<?> returnType) {
        Class<?> arrayComponentType = returnType.getComponentType();
        Object array = Array.newInstance(arrayComponentType, list.size());
        if (arrayComponentType.isPrimitive()) {
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, list.get(i));
            }
            return array;
        } else {
            return list.toArray((E[]) array);
        }
    }
}

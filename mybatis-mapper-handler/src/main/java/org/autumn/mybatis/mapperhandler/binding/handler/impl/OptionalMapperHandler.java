package org.autumn.mybatis.mapperhandler.binding.handler.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.lang.UsesJava8;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;

/**
 * Copy Right Information : @Copyright@ <br>
 * Project : @Project@ <br>
 * Description : 返回Optional类型的处理器<br>
 * Author : andyslin <br>
 * Version : 0.0.1 <br>
 * Date : 2018-12-24<br>
 */
@UsesJava8
public class OptionalMapperHandler extends SelectMapperHandler {

    public OptionalMapperHandler() {
        super();
    }

    public OptionalMapperHandler(int order) {
        super(order);
    }

    @Override
    public boolean supports(MapperHandlerContext context) {
        return super.supports(context)
                && Optional.class.isAssignableFrom(context.getMethod().getReturnType());
    }

    @Override
    public Object execute(SqlSession sqlSession, Object[] args, MapperHandlerContext context) {
        Object result = null;
        Class<?> returnType = getOptionalType(context);
        if (null == returnType) {
            throw new RuntimeException("not supported return type: [" + context.getMethod().getGenericReturnType() + "]");
        }

        String sqlId = context.getMappedStatement().getId();
        Object param = context.getParamResolver().getNamedParams(args);
        RowBounds rowBounds = extractParam(args, RowBounds.class);
        if (context.getConfiguration().getObjectFactory().isCollection(returnType) || returnType.isArray()) {
            result = super.executeMany(sqlSession, sqlId, param, rowBounds, returnType);
        } else if (Map.class.isAssignableFrom(returnType) && context.getMethod().isAnnotationPresent(MapKey.class)) {
            result = super.executeMany(sqlSession, sqlId, param, rowBounds, returnType);
        } else if (Cursor.class.isAssignableFrom(returnType)) {
            result = super.executeCursor(sqlSession, sqlId, param, rowBounds);
        } else {
            result = sqlSession.selectOne(sqlId, param);
        }
        return Optional.ofNullable(result);
    }

    private Class<?> getOptionalType(MapperHandlerContext context) {
        ParameterizedType genericReturnType = (ParameterizedType) context.getMethod().getGenericReturnType();
        Type type = genericReturnType.getActualTypeArguments()[0];
        Class<?> cls = null;
        if (type instanceof ParameterizedType) {
            cls = (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class) {
            cls = (Class<?>) type;
        }
        return cls;
    }

}

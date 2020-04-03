package org.autumn.mybatis.mapperhandler.binding.handler.impl;

import java.lang.reflect.Method;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.SqlSession;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;

/**
 * Copy Right Information : @Copyright@ <br>
 * Project : @Project@ <br>
 * Description : 更新类的处理器<br>
 * Author : andyslin <br>
 * Version : 0.0.1 <br>
 * Date : 2018-12-24<br>
 */
public class UpdateMapperHandler extends AbstractMapperHandler {

    public UpdateMapperHandler() {
        super();
    }

    public UpdateMapperHandler(int order) {
        super(order);
    }

    @Override
    public boolean supports(MapperHandlerContext context) {
        MappedStatement statement = context.getMappedStatement();
        if (null != statement) {
            SqlCommandType type = statement.getSqlCommandType();
            return type.equals(SqlCommandType.INSERT) || type.equals(SqlCommandType.UPDATE) || type.equals(SqlCommandType.DELETE);
        }
        return false;
    }

    @Override
    public Object execute(SqlSession sqlSession, Object[] args, MapperHandlerContext context) {
        MappedStatement statement = context.getMappedStatement();
        Object param = context.getParamResolver().getNamedParams(args);
        int count = sqlSession.update(statement.getId(), param);
        return rowCountResult(context.getMethod(), context.getReturnType(), count);
    }

    private Object rowCountResult(Method method, Class<?> returnType, int rowCount) {
        final Object result;
        if (void.class.equals(returnType)) {
            result = null;
        } else if (Integer.class.equals(returnType) || Integer.TYPE.equals(returnType)) {
            result = rowCount;
        } else if (Long.class.equals(returnType) || Long.TYPE.equals(returnType)) {
            result = (long) rowCount;
        } else if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
            result = rowCount > 0;
        } else {
            throw new BindingException("Mapper method '" + method + "' has an unsupported return type: " + returnType);
        }
        return result;
    }

}

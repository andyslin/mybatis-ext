package org.autumn.mybatis.mapperhandler.binding.handler.impl;

import java.lang.reflect.Method;

import org.apache.ibatis.session.SqlSession;
import org.autumn.mybatis.mapperhandler.annotation.Merge;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;
import org.springframework.util.StringUtils;

/**
 * Copy Right Information : @Copyright@ <br>
 * Project : @Project@ <br>
 * Description : 存在更新、不存在插入的处理器<br>
 * Author : andyslin <br>
 * Version : 0.0.1 <br>
 * Date : 2018-12-24<br>
 */
public class MergeMapperHandler extends AbstractTransactionMapperHandler {

    public MergeMapperHandler() {
        super();
    }

    public MergeMapperHandler(int order) {
        super(order);
    }

    @Override
    public boolean supports(MapperHandlerContext context) {
        return context.getMethod().isAnnotationPresent(Merge.class);
    }

    @Override
    public Object execute(SqlSession sqlSession, Object[] args, MapperHandlerContext context) {
        final Object param = context.getParamResolver().getNamedParams(args);
        Method method = context.getMethod();
        Merge merge = method.getAnnotation(Merge.class);
        final String updateSqlRef = this.resolveSqlId(method, merge.updateSqlRef());
        final String insertSqlRef = this.resolveSqlId(method, merge.insertSqlRef());
        return this.executeWithTransaction(sqlSession, context, status -> {
            int sqlCount = sqlSession.update(updateSqlRef, param);
            if (0 == sqlCount) {
                sqlCount = sqlSession.insert(insertSqlRef, param);
            }
            return sqlCount;
        });
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
}

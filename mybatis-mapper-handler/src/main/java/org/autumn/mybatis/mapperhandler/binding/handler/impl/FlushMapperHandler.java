package org.autumn.mybatis.mapperhandler.binding.handler.impl;

import org.apache.ibatis.annotations.Flush;
import org.apache.ibatis.session.SqlSession;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;

/**
 * Copy Right Information : @Copyright@ <br>
 * Project : @Project@ <br>
 * Description : 批量刷新处理器<br>
 * Author : andyslin <br>
 * Version : 0.0.1 <br>
 * Date : 2018-12-24<br>
 */
public class FlushMapperHandler extends AbstractMapperHandler {

    public FlushMapperHandler() {
        super();
    }

    public FlushMapperHandler(int order) {
        super(order);
    }

    @Override
    public boolean supports(MapperHandlerContext context) {
        return context.getMethod().getAnnotation(Flush.class) != null;
    }

    @Override
    public Object execute(SqlSession sqlSession, Object[] args, MapperHandlerContext context) {
        return sqlSession.flushStatements();
    }

}

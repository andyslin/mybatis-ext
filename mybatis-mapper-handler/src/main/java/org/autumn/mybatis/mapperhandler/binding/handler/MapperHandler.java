package org.autumn.mybatis.mapperhandler.binding.handler;

import org.apache.ibatis.session.SqlSession;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;
import org.springframework.core.Ordered;

public interface MapperHandler extends Ordered {

    boolean supports(MapperHandlerContext context);

    Object execute(SqlSession sqlSession, Object[] args, MapperHandlerContext context);
}

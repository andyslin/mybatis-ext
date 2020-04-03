package org.autumn.mybatis.mapperhandler.binding.context;

import java.lang.reflect.Method;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.mapperhandler.binding.paramresolver.ParamResolver;

/**
 * Copy Right Information : @Copyright@ <br>
 * Project : @Project@ <br>
 * Description : SQL执行处理器上下文<br>
 * Author : andyslin <br>
 * Version : 0.0.1 <br>
 * Date : 2018-12-24<br>
 */
public interface MapperHandlerContext {

    Class<?> getMapperInterface();

    Method getMethod();

    Configuration getConfiguration();

    Class<?> getReturnType();

    /**
     * lazy init
     */
    MappedStatement getMappedStatement();

    /**
     * lazy init
     */
    ParamResolver getParamResolver();
}

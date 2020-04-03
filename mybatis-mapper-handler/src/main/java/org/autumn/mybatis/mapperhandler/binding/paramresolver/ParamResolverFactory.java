package org.autumn.mybatis.mapperhandler.binding.paramresolver;

import java.lang.reflect.Method;

import org.apache.ibatis.session.Configuration;

public interface ParamResolverFactory {

    ParamResolver newParamResolver(Configuration configuration, Method method);
}

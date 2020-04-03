package org.autumn.mybatis.mapperhandler.binding.paramresolver.impl;

import java.lang.reflect.Method;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.mapperhandler.binding.paramresolver.ParamResolver;
import org.autumn.mybatis.mapperhandler.binding.paramresolver.ParamResolverFactory;

public class DefaultParamResolverFactory implements ParamResolverFactory {

    @Override
    public ParamResolver newParamResolver(Configuration configuration, Method method) {
        final ParamNameResolver proxy = new ParamNameResolver(configuration, method);
        return new ParamResolver() {
            @Override
            public String[] getNames() {
                return proxy.getNames();
            }

            @Override
            public Object getNamedParams(Object[] args) {
                return proxy.getNamedParams(args);
            }
        };
    }
}

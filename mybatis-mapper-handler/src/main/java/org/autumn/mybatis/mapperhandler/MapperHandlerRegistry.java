package org.autumn.mybatis.mapperhandler;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.common.registry.GenericRegistry;
import org.autumn.mybatis.common.registry.GenericRegistry.InstanceRegistry;
import org.autumn.mybatis.common.registry.GenericRegistry.OrderedRegistry;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;
import org.autumn.mybatis.mapperhandler.binding.handler.MapperHandler;
import org.autumn.mybatis.mapperhandler.binding.paramresolver.ParamResolver;
import org.autumn.mybatis.mapperhandler.binding.paramresolver.ParamResolverFactory;

public class MapperHandlerRegistry {

    private static final OrderedRegistry<MapperHandler> mapperHandlerRegistry = GenericRegistry.getOrderedRegistry(MapperHandler.class);

    private static final InstanceRegistry<ParamResolverFactory> paramResolverFactoryRegistry = GenericRegistry.getInstanceRegistry(ParamResolverFactory.class);

    public static MapperHandler getMapperHandler(MapperHandlerContext mapperHandlerContext, Method method) {
        List<MapperHandler> mapperHandlers = mapperHandlerRegistry.get();
        if (null != mapperHandlers) {
            for (MapperHandler mapperHandler : mapperHandlers) {
                if (mapperHandler.supports(mapperHandlerContext)) {
                    return mapperHandler;
                }
            }
        }
        return null;
    }

    public static ParamResolver newParamResolver(Configuration configuration, Method method) {
        ParamResolverFactory paramResolverFactory = paramResolverFactoryRegistry.get();
        return null == paramResolverFactory ? null : paramResolverFactory.newParamResolver(configuration, method);
    }
}

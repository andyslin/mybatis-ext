package org.autumn.mybatis.mapperhandler.binding.context.impl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.session.Configuration;
import org.autumn.mybatis.mapperhandler.MapperHandlerRegistry;
import org.autumn.mybatis.mapperhandler.annotation.SqlRef;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;
import org.autumn.mybatis.mapperhandler.binding.paramresolver.ParamResolver;

/**
 * Copy Right Information : @Copyright@ <br>
 * Project : @Project@ <br>
 * Description : 默认的SQL执行处理器上下文<br>
 * Author : andyslin <br>
 * Version : 0.0.1 <br>
 * Date : 2018-12-24<br>
 */
public class DefaultMapperHandlerContext implements MapperHandlerContext {

    private final Class<?> mapperInterface;
    private final Method method;
    private final Configuration configuration;
    private final Class<?> returnType;
    private ParamResolver paramResolver;
    private MappedStatement mappedStatement;
    private boolean mappedStatementInitMonitor = false;

    public DefaultMapperHandlerContext(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.mapperInterface = mapperInterface;
        this.method = method;
        this.configuration = configuration;
        this.returnType = resolveReturnType(mapperInterface, method, configuration);
    }

    private static Class<?> resolveReturnType(Class<?> mapperInterface, Method method, Configuration configuration) {
        Class<?> returnType = null;
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
        if (resolvedReturnType instanceof Class<?>) {
            returnType = (Class<?>) resolvedReturnType;
        } else if (resolvedReturnType instanceof ParameterizedType) {
            returnType = (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
        } else {
            returnType = method.getReturnType();
        }
        return returnType;
    }

    @Override
    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public Class<?> getReturnType() {
        return returnType;
    }

    @Override
    public MappedStatement getMappedStatement() {
        if (!mappedStatementInitMonitor) {
            synchronized (method) {
                if (!mappedStatementInitMonitor) {
                    try {
                        SqlRef sqlRef = method.getAnnotation(SqlRef.class);
                        this.mappedStatement = this.resolveMappedStatement(mapperInterface, method.getName(), method.getDeclaringClass(), configuration, sqlRef);
                    } catch (Exception e) {
                        this.mappedStatement = null;
                    } finally {
                        mappedStatementInitMonitor = true;
                    }
                }
            }
        }
        return mappedStatement;
    }

    private MappedStatement resolveMappedStatement(Class<?> mapperInterface, String methodName,
            Class<?> declaringClass, Configuration configuration, SqlRef sqlRef) {
        if (null != sqlRef && null != sqlRef.value() && sqlRef.value().length >= 1) {
            String statementId = mapperInterface.getName() + "." + sqlRef.value()[0];
            if (configuration.hasStatement(statementId)) {
                return configuration.getMappedStatement(statementId);
            }
        }

        String statementId = mapperInterface.getName() + "." + methodName;
        if (configuration.hasStatement(statementId)) {
            return configuration.getMappedStatement(statementId);
        } else if (mapperInterface.equals(declaringClass)) {
            return null;
        }
        for (Class<?> superInterface : mapperInterface.getInterfaces()) {
            if (declaringClass.isAssignableFrom(superInterface)) {
                MappedStatement ms = resolveMappedStatement(superInterface, methodName,
                        declaringClass, configuration, sqlRef);
                if (ms != null) {
                    return ms;
                }
            }
        }
        return null;
    }

    @Override
    public ParamResolver getParamResolver() {
        if (null == paramResolver) {
            synchronized (method) {
                if (null == paramResolver) {
                    this.paramResolver = MapperHandlerRegistry.newParamResolver(configuration, method);
                }
            }
        }
        return paramResolver;
    }
}

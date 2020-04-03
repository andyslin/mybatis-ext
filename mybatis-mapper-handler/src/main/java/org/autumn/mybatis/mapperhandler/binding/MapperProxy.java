package org.autumn.mybatis.mapperhandler.binding;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.lang.UsesJava7;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;
import org.autumn.mybatis.mapperhandler.MapperHandlerRegistry;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;
import org.autumn.mybatis.mapperhandler.binding.context.impl.DefaultMapperHandlerContext;
import org.autumn.mybatis.mapperhandler.binding.handler.MapperHandler;

public class MapperProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -6424540398559729838L;
    private final SqlSession sqlSession;
    private final Class<T> mapperInterface;
    private final Map<Method, MapperMethod> methodCache;
    private final Map<Method, MapperHandlerContext> mapperHandlerContextCache;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
        this(sqlSession, mapperInterface, methodCache, null);
    }

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache, Map<Method, MapperHandlerContext> mapperHandlerContextCache) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.methodCache = methodCache;
        this.mapperHandlerContextCache = mapperHandlerContextCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else if (isDefaultMethod(method)) {
                return invokeDefaultMethod(proxy, method, args);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }

        if (null != mapperHandlerContextCache) {
            //cachedMapperMethod(method);// for pass the test class org.apache.ibatis.binding.BindingTest
            final MapperHandlerContext mapperHandlerContext = cachedMapperHandlerContext(method);
            MapperHandler mapperHandler = MapperHandlerRegistry.getMapperHandler(mapperHandlerContext, method);
            if (null != mapperHandler) {
                return mapperHandler.execute(sqlSession, args, mapperHandlerContext);
            }
        }
        final MapperMethod mapperMethod = cachedMapperMethod(method);
        return mapperMethod.execute(sqlSession, args);
    }

    private MapperHandlerContext cachedMapperHandlerContext(Method method) {
        MapperHandlerContext mhc = mapperHandlerContextCache.get(method);
        if (mhc == null) {
            mhc = new DefaultMapperHandlerContext(mapperInterface, method, sqlSession.getConfiguration());
            mapperHandlerContextCache.put(method, mhc);
        }
        return mhc;
    }

    private MapperMethod cachedMapperMethod(Method method) {
        MapperMethod mapperMethod = methodCache.get(method);
        if (mapperMethod == null) {
            mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
            methodCache.put(method, mapperMethod);
        }
        return mapperMethod;
    }

    @UsesJava7
    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
            throws Throwable {
        final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class, int.class);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        return constructor
                .newInstance(declaringClass,
                        MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                                | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
                .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    /**
     * Backport of java.lang.reflect.Method#isDefault()
     */
    private boolean isDefaultMethod(Method method) {
        return (method.getModifiers()
                & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC
                && method.getDeclaringClass().isInterface();
    }
}

package org.autumn.mybatis.common.registry;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GenericRegistry {

    /**
     * 命名对象注册器接口
     *
     * @param <N>
     */
    public interface NamedRegistry<N extends Named> {

        /**
         * 注册命名对象
         *
         * @param nameds
         */
        void register(N... nameds);

        /**
         * 注册命名对象
         *
         * @param nameds
         */
        default void register(Collection<N> nameds) {
            if (null != nameds && !nameds.isEmpty()) {
                nameds.stream().filter(Objects::nonNull).forEach(this::register);
            }
        }

        /**
         * 获取命名对象
         *
         * @param name
         * @return
         */
        N get(String name);
    }

    /**
     * 排序对象注册器接口
     *
     * @param <O>
     */
    public interface OrderedRegistry<O> {

        /**
         * 注册排序对象
         *
         * @param ordereds
         */
        void register(O... ordereds);

        /**
         * 注册排序对象
         *
         * @param ordereds
         */
        default void register(Collection<O> ordereds) {
            if (null != ordereds && !ordereds.isEmpty()) {
                ordereds.stream().filter(Objects::nonNull).forEach(this::register);
            }
        }

        /**
         * 获取所有已注册的排序对象
         *
         * @return
         */
        List<O> get();

        /**
         * 获取第一个已注册的对象
         *
         * @return
         */
        default O getFirst() {
            List<O> list = get();
            return (null == list || list.isEmpty()) ? null : list.get(0);
        }
    }

    /**
     * 命名对象缓存
     */
    private static final Map<Class<? extends Named>, Map<String, Named>> named = new HashMap<>();

    /**
     * 排序对象缓存
     */
    private static final Map<Class<?>, List<?>> ordered = new HashMap<>();

    /**
     * 注册命名对象
     *
     * @param cls
     * @param nameds
     * @param <N>
     */
    public static <N extends Named> void registerNameds(Class<N> cls, N... nameds) {
        Map<String, Named> cache = getNamedMap(cls);
        synchronized (cache) {
            Arrays.stream(nameds).filter(Objects::nonNull).forEach(n -> cache.put(n.getName(), n));
        }
    }

    /**
     * 获取命名对象
     *
     * @param cls
     * @param name
     * @param <N>
     * @return
     */
    public static <N extends Named> N getNamed(Class<N> cls, String name) {
        Map<String, Named> cache = getNamedMap(cls);
        Named named = cache.get(name);
        return cls.cast(named);
    }

    /**
     * 获取命名对象注册器
     *
     * @param cls
     * @param <N>
     * @return
     */
    public static <N extends Named> NamedRegistry<N> getNamedRegistry(Class<N> cls) {
        return new NamedRegistry<N>() {
            @Override
            public void register(N... nameds) {
                registerNameds(cls, nameds);
            }

            @Override
            public N get(String name) {
                return getNamed(cls, name);
            }
        };
    }

    private static <N extends Named> Map<String, Named> getNamedMap(Class<N> cls) {
        Map<String, Named> cache = named.get(cls);
        if (null == cache) {
            synchronized (named) {
                cache = named.get(cls);
                if (null == cache) {
                    cache = new HashMap<>();
                    named.put(cls, cache);
                }
            }
        }
        return cache;
    }

    /**
     * 注册排序对象
     *
     * @param cls
     * @param ordereds
     * @param <O>
     */
    public static <O> void registerOrdereds(Class<O> cls, O... ordereds) {
        List<O> list = getOrdered(cls);
        synchronized (list) {
            Arrays.stream(ordereds).filter(Objects::nonNull).forEach(list::add);
            AnnotationAwareOrderComparator.sort(list);
        }
    }

    /**
     * 获取排序对象列表
     *
     * @param cls
     * @param <O>
     * @return
     */
    public static <O> List<O> getOrdered(Class<O> cls) {
        List<O> list = (List<O>) ordered.get(cls);
        if (null == list) {
            synchronized (ordered) {
                list = (List<O>) ordered.get(cls);
                if (null == list) {
                    list = new ArrayList<>();
                    ordered.put(cls, list);
                }
            }
        }
        return list;
    }

    /**
     * 获取排序对象注册器
     *
     * @param cls
     * @param <O>
     * @return
     */
    public static <O> OrderedRegistry<O> getOrderedRegistry(Class<O> cls) {
        return new OrderedRegistry<O>() {
            @Override
            public void register(O... ordereds) {
                registerOrdereds(cls, ordereds);
            }

            @Override
            public List<O> get() {
                return getOrdered(cls);
            }
        };
    }
}

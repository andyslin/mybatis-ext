package org.autumn.mybatis.mapperhandler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BatchParam {

    /**
     * 表示批量参数的属性
     *
     * @return 批量参数属性
     */
    String property() default "";

    /**
     * 批量参数中每一项存入map结构时的名称
     *
     * @return 数据项名称
     */
    String name() default "item";

    /**
     * 当前索引存入map结构时的名称
     *
     * @return 索引名称
     */
    String index() default "index";

    /**
     * SQL和参数是否一一对应
     *
     * @return
     */
    boolean oneByOne() default false;
}

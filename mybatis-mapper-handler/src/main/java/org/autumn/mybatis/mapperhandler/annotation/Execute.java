package org.autumn.mybatis.mapperhandler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Execute {

    /**
     * sqlRef引用
     *
     * @return sqlRef引用
     */
    String sqlRef() default "";

    /**
     * 执行的条件
     *
     * @return
     */
    String condition() default "";

    /**
     * 表示批量参数的属性
     *
     * @return 批量参数属性
     */
    String property() default "";

    /**
     * 是否为内嵌批量
     *
     * @return 是否为内嵌批量
     */
    boolean batch() default true;

    /**
     * 批量参数中每一项存入map结构时的名称, batch=true时有效
     *
     * @return 数据项名称
     */
    String name() default "item";

    /**
     * 当前索引存入map结构时的名称, batch=true时有效
     *
     * @return 索引名称
     */
    String index() default "index";
}

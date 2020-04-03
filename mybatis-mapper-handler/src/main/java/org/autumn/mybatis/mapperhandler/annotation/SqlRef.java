package org.autumn.mybatis.mapperhandler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlRef {
    /**
     * 标识需要执行的SqlId或sqlId数组
     *
     * @return SqlId或sqlId数组
     */
    String[] value();
}

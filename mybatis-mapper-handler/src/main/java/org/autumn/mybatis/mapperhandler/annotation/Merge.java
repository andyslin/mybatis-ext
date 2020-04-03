package org.autumn.mybatis.mapperhandler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Merge {

    /**
     * 更新的sqlRef引用
     *
     * @return 更新的sqlRef引用
     */
    String updateSqlRef();

    /**
     * 新增的sqlRef引用
     *
     * @return 新增的sqlRef引用
     */
    String insertSqlRef();
}

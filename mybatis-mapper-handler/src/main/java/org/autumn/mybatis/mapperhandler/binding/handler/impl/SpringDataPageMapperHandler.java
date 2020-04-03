package org.autumn.mybatis.mapperhandler.binding.handler.impl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.LongSupplier;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.session.SqlSession;
import org.autumn.mybatis.mapperhandler.annotation.SqlRef;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;
import org.autumn.mybatis.mapperhandler.page.PageableRowBounds;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.PageableExecutionUtils;

/**
 * Copy Right Information : @Copyright@ <br>
 * Project : @Project@ <br>
 * Description : 分页查询类的执行处理器<br>
 * Author : andyslin <br>
 * Version : 0.0.1 <br>
 * Date : 2018-12-24<br>
 */
public class SpringDataPageMapperHandler extends AbstractMapperHandler {

    public SpringDataPageMapperHandler() {
        super();
    }

    public SpringDataPageMapperHandler(int order) {
        super(order);
    }

    @Override
    public boolean supports(MapperHandlerContext context) {
        return Page.class.isAssignableFrom(context.getMethod().getReturnType());
    }

    @Override
    public Object execute(SqlSession sqlSession, Object[] args, MapperHandlerContext context) {
        Method method = context.getMethod();
        String sqlId = this.resolveSqlId(method);

        if (!sqlSession.getConfiguration().hasStatement(sqlId)) {
            throw new BindingException("Invalid bound statement (not found): " + sqlId);
        }

        Pageable pageable = this.findPageableParam(sqlId, args);
        final PageableRowBounds rowBounds = PageableRowBounds.build(pageable);

        Object parameter = context.getParamResolver().getNamedParams(args);
        List<?> content = sqlSession.selectList(sqlId, parameter, rowBounds);

        Page<?> page = PageableExecutionUtils.getPage(content, pageable, new LongSupplier() {
            @Override
            public long getAsLong() {
                return rowBounds.getTotal();
            }
        });
        return page;
    }

    private String resolveSqlId(Method method) {
        String namespace = method.getDeclaringClass().getName();
        SqlRef sqlRef = method.getAnnotation(SqlRef.class);
        if (null != sqlRef) {
            String[] value = sqlRef.value();
            if (null != value && value.length >= 1) {
                return namespace + "." + value[0];
            }
        }
        return namespace + "." + method.getName();
    }

    private Pageable findPageableParam(String sqlId, Object[] args) {
        Pageable pageable = null;
        Sort sort = null;
        if (null != args) {
            for (Object arg : args) {
                if (arg instanceof Pageable) {
                    pageable = (Pageable) arg;
                } else if (arg instanceof Sort) {
                    sort = (Sort) arg;
                }
            }
        }
        if (null == pageable) {
            throw new BindingException("not found pageable parameter: " + sqlId);
        }
        if (null != sort && !sort.equals(pageable.getSort())) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }
        return pageable;
    }
}

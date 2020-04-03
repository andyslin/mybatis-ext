package org.autumn.mybatis.mapperhandler.binding.handler.impl;

import org.autumn.mybatis.mapperhandler.binding.handler.MapperHandler;
import org.springframework.core.Ordered;

public abstract class AbstractMapperHandler implements Ordered, MapperHandler {

    private int order;

    public AbstractMapperHandler() {
    }

    public AbstractMapperHandler(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

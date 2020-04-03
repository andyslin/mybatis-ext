package org.autumn.mybatis.mapperhandler.binding.paramresolver;

public interface ParamResolver {

    String[] getNames();

    Object getNamedParams(Object[] args);
}

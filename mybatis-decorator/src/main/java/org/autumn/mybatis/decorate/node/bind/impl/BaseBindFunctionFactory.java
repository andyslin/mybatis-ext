package org.autumn.mybatis.decorate.node.bind.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.autumn.mybatis.decorate.node.bind.BindFunction;
import org.autumn.mybatis.decorate.node.bind.BindFunctionFactory;

public class BaseBindFunctionFactory implements BindFunctionFactory {

    @Override
    public Collection<BindFunction> getBindFunctions() {
        List<BindFunction> functions = new ArrayList<>();
        functions.add(new IfBindFunction());
        functions.add(new InsertBindFunction());
        functions.add(new UpdateBindFunction());
        functions.add(new DeleteBindFunction());
        return functions;
    }
}

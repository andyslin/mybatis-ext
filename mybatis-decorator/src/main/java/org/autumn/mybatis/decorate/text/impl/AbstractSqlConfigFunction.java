package org.autumn.mybatis.decorate.text.impl;

import org.autumn.mybatis.decorate.text.SqlConfigFunction;

public abstract class AbstractSqlConfigFunction implements SqlConfigFunction {

    protected String join(String[] arr, String separator) {
        if (null == arr || arr.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : arr) {
            if (null != obj) {
                sb.append(separator).append(obj);
            }
        }
        return sb.substring(separator.length());
    }

    protected void assertEqualArgsCount(String[] args, int count) {
        if (null != args && args.length != count) {
            throw new IllegalArgumentException("the count of sql-config-function [" + getName() + "] args must be " + count + ", but actual is " + args.length);
        }
    }

    protected void assertAtLeastArgsCount(String[] args, int count) {
        if (null != args && args.length < count) {
            throw new IllegalArgumentException("the count of sql-config-function [" + getName() + "] args at least is " + count + ", but actual is " + args.length);
        }
    }
}

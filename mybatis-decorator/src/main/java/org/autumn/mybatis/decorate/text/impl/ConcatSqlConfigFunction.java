package org.autumn.mybatis.decorate.text.impl;

/* package */ class ConcatSqlConfigFunction extends AbstractDatabaseIdSqlConfigFunction {

    @Override
    public String getName() {
        return "concat";
    }

    @Override
    public String eval(String databaseId, String[] args) {
        super.assertAtLeastArgsCount(args, 2);
        if (databaseId.indexOf("mysql") != -1) {
            return "CONCAT(" + join(args, ",") + ")";
        } else {
            return join(args, "||");
        }
    }
}

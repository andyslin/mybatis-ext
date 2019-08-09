package org.autumn.mybatis.decorate.text.impl;

/* package */ class DecodeSqlConfigFunction extends AbstractDatabaseIdSqlConfigFunction {

    @Override
    public String getName() {
        return "decode";
    }

    @Override
    public String eval(String databaseId, String[] args) {
        super.assertAtLeastArgsCount(args, 3);
        if (databaseId.indexOf("oracle") != -1) {
            return "DECODE(" + join(args, ",") + ")";
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("CASE ").append(args[0]);
            int i = 2, l = args.length;
            for (; i < l; i = i + 2) {
                sb.append(" WHEN ").append(args[i - 1]).append(" THEN ").append(args[i]);
            }
            if (i == l) {//结束循环时，两者相等说明最后一个参数未使用
                sb.append(" ELSE ").append(args[l - 1]);
            }
            sb.append(" END");
            return sb.toString();
        }
    }
}

package org.autumn.mybatis.codegenerate.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Module implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3678402681458229502L;

    /**
     * 表或SQL
     */
    private final String tableNameOrSql;
    /**
     * 模块名称
     */
    private final String name;
    /**
     * 包名
     */
    private final String packageName;
    /**
     * 模块描述
     */
    private final String des;
    /**
     * 首字母大写（大驼峰式，用于类名称）
     */
    private final String name1;
    /**
     * 首字母小写（小驼峰式）
     */
    private final String name2;
    /**
     * 全部小写
     */
    private final String name3;

    public Module(String tableNameOrSql, String name, String packageName, String des) {
        this.tableNameOrSql = tableNameOrSql;
        this.name = name.trim();
        this.packageName = packageName;
        this.des = des;
        StringBuilder sb = new StringBuilder();
        boolean in = false;
        for (char ch : this.name.toCharArray()) {
            switch (ch) {
                case '-':
                case '_':
                    in = true;
                    break;
                default:
                    if (in) {
                        sb.append(Character.toUpperCase(ch));
                    } else {
                        sb.append(ch);
                    }
                    in = false;
                    break;
            }
        }
        this.name1 = Character.toUpperCase(this.name.charAt(0)) + sb.substring(1);
        this.name2 = Character.toLowerCase(this.name.charAt(0)) + sb.substring(1);
        this.name3 = this.name1.toLowerCase();
    }
}

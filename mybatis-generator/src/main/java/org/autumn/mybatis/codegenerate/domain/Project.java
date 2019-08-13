package org.autumn.mybatis.codegenerate.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class Project implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1870380957316479360L;

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 当前日期
     */
    private final String date = LocalDateTime.now().format(format);

    /**
     * 作者
     */
    private String author;
    /**
     * 版权
     */
    private String copyright;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目路径
     */
    private String projectPath;
    /**
     * 版本号
     */
    private String version = "0.0.1";
}

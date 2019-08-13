# 代码生成

> 本地运行项目，需要安装`Lombok`插件

## 主要功能

* 根据表名或SQL语句生成代码
  * 主要思想：循环读取templatepath（默认src/main/resources/META-INF/codetemplate/）下的文件，将这些文件当成Freemarker模板文件，然后将模板变量逐个应用到这些文件并生成新文件
  * 模板变量：（实际使用时可以根据需要修改模板文件路径、模板文件内容、增删模板文件、添加子目录等）
     1. table 表示一个查询的相关信息 {@link Query}
     1. project 表示一个工程的信息 {@link Project}
     1. module 表示工程中一个业务模块的信息 {@link ProjectModule}
     1. 自定义变量，由接口中的参数传入 {@link CodeGenerateService#generate(Project, Module, Map)}
   
   > 此外，由于路径和文件名不能使用Freemarker的语法，因此统一使用Demo、demo来表示模块名

> Freemarker模板文件语法可参考<https://freemarker.apache.org/docs/index.html>,在线测试<https://try.freemarker.apache.org/>

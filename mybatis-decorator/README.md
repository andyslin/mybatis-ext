# Mybatis扩展——SqlMapper配置装饰

1. 装饰SqlMapper配置中的元素节点（一级子元素除外）
   * 主要思想：利用不太常用的`<bind name="#fname[.subname]" value="args"/>`元素，提取出名称`fname`与参数`args`（称之为Bind函数`BindFunction`），使用函数的实现对SqlMapper配置的`Document`进行修正，使之符合Mybatis的原生配置
   * 已有实现：`if|if.like|if.llike|if.rlike`、`insert`、`update`、`delete`
1. 装饰SqlMapper配置中的SQL片段
   * 主要思想：通过特殊格式`$fname{args}`来识别这些片段，提取出名称`fname`与参数`args`（称之为SQL配置函数`SqlConfigFunction`），使用函数的实现解析为Mybatis的合法配置，然后再替换之前的SQL片段
   * 已有实现：`$concat`、`$decode`等
   
> 示例：`org.autumn.mybatis.decorate.UserServiceTest`

package xxs.common.module.codegenerate.method;
//一、基本连接查询，单表查询
//版本1 基于sql生成查询方法 放入到一个模板中吧，版本2 直接追加到指定java文件中。
//输出: ①xml sql,可以选定哪些字段作为查询参数过滤条件 ②mapper接口 ③服务类，服务接口类，④控制器类。⑤ paran类，结果集类
//输入: ①一个sql ，②一个参数列表。③seach name

//二、基于mybatis的嵌套查询的方式实现方法级别的生成。
# SOA EShop 
## Dubbo + TCC 分布式事务

## 业务拆分
用户子系统：
- 用户模块：user+mail,涉及user,role,mail,mail_text,balance表
- 产品模块：product,涉及product,category表
- 新闻模块：news,涉及news表

订单子系统：order,涉及order表

邮件子系统

## 约定
公共的domain、exception、enumeration都放在common模块下
一般情况下api模块只放service接口

## 启动顺序
email，user，order，web

## 注意事项
所有实体类都要实现serializable接口

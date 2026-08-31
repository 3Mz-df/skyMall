项目开发一般分三模块三层：
sky-take-out（父工程，只管版本管理）
                 /         |          \
        sky-common      sky-pojo      sky-server
          ↑只依赖三方库     ↑只依赖三方库    ↑依赖 common + pojo + 一堆框架
          （不依赖任何人）   （不依赖任何人）   （依赖上面两个兄弟）
持久层是啥？ 是指java项目开发的三层之一 三层：控制层（接待请求，转交任务，返回结果）-业务层（逻辑处理）-持久层（操作数据库 执行SQL）

项目需求接口文档在Apifox里

通过swagger-knife4j生成的接口文档在server-config，前端/doc.html访问。登录调试可以拿到新token

接口文档里的全局参数设置token有效时间改成了24h。实现：server-resources-application.yml里admin-ttl: 

server-service-EmployeeServicelmpl里添加了 md5加密，新增员工功能。 还有个TODO（已解决）。

server-handler-GlobalExcptionHandler里添加了 处理SQL异常，新增重复员工抛出错误，程序不再崩溃

TODO内容：利用ThreadLocal线程实现动态新增员工 将登录者的id放入线程，谁登录了就取谁的id，使得系统操作可追溯

添加了员工账号分页查询功能  DTO的东西 SQL语句符号的应用 用了个mybatis的插件“Pagehelper”

添加了启用禁用员工账号功能 SQL语句符号的应用

写调用接口的东西有些注解不太懂？

根据接口文档添加新功能基本流程：Controller里写调用接口的东西 --> Service里写接口调用实现类（方法）--> Serviceimpl里写实现类（方法）

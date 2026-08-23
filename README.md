通过swagger-knife4j生成的接口文档在server-config，前端/doc.html访问。登录调试可以拿到新token

接口文档里的全局参数设置token有效时间改成了24h。实现：server-resources-application.yml里admin-ttl: 

server-service-EmployeeServicelmpl里添加了 md5加密，新增员工功能 还有个TODO 持久层是啥？

server-handler-GlobalExcptionHandler里添加了 处理SQL异常，新增重复员工抛出错误，程序不再崩溃

<img width="1627" height="914" alt="d7ded3d26a1e9e17cfc3e1b21272109639682250" src="https://github.com/user-attachments/assets/f60fe5ac-ff7f-4a6c-b3d8-e35b27c7d7d6" />
项目开发一般分三模块三层：
sky-take-out（父工程，只管版本管理）
                 /         |          \
        sky-common      sky-pojo      sky-server
          ↑只依赖三方库     ↑只依赖三方库    ↑依赖 common + pojo + 一堆框架
          （不依赖任何人）   （不依赖任何人）   （依赖上面两个兄弟）
持久层是啥？ 是指java项目开发的三层之一 三层：控制层（接待请求，转交任务，返回结果）-业务层（逻辑处理）-持久层（操作数据库 执行SQL）

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

!!根据接口文档开发大模块的功能基本流程：Controller里写调用接口的东西 --> Service里写接口调用实现类（方法）--> Serviceimpl里写实现类（方法）|| 具体如下：

------------------
server-controller.admin包下新建 “模块名”Controller（J） 再根据接口文档的提示编写模块里，顺手建好swagger的接口测试入口，然后编写调用接口·日志·返回。||
--------------------------
每个功能的请求映射注解{spring MVC} 其中：

分类管理模块：
  这个类是一个 Controller（控制器），是整个项目的"前台接待员"。它的职责是：接收前端（网页/小程序）发来的 HTTP 请求，然后调用 Service 层去干活，最后把结果返回给前端。
@RestController
  作用：告诉 Spring ——"这个类是一个控制器，请帮我管理它"。Spring 会把这个类创建成对象放进自己的容器（可以理解为一个大仓库）里，前端请求进来时，Spring 会找到对应的控制器去处理。
  它还隐含了"返回的是 JSON数据"，后端给前端的响应会被自动转成 JSON 格式。

@RequestMapping("/admin/category")
  作用：给这个类的所有接口统一加一个地址前缀。例如类里以后写一个"新增分类"的方法，方法上标注 @PostMapping 就可以访问前端接受数据了

@Api(tags = "分类相关接口")
  tags 是分组名称。Swagger 生成文档时，会把所有标了这个注解的接口归到一个分组里，文档页面上就会显示一个分组叫"分类相关接口"，方便查阅。

@Slf4j
  前面讲过，Lombok 会帮你在编译时生成一个 log 日志对象。以后在方法里写 log.info("新增分类：{}", category); 就能在控制台打印日志，用来排查问题。

public class CategoryController {
  public：公共的，任何地方都能访问。class：定义一个类。类 = 把"属性"和"方法"打包在一起的模板。

@Autowired
  作用：自动注入依赖。Spring 启动时会检查：有没有人实现了 CategoryService 接口？有的话就把那个实现类对象创建好，自动赋值给下面这个变量。
  这就是著名的 IOC（控制反转）/ DI（依赖注入） 思想：
  传统写法（自己 new）：CategoryService s = new CategoryServiceImpl(); —— 创建权在自己手里，两个类死死绑定。
  注入写法（Spring 给）：创建权交给 Spring 容器，哪天想换一个实现类，只需要改配置，代码不用动。解耦就是"你俩别绑太死"。

这个类现在的任务是"占位"——它已经声明了自己是分类管理的控制器、绑定了地址前缀、拿到了 Service 层工具，就等着往里写具体的接口方法（新增、删除、查询等）。


新增分类：
  用户在前端页面填写了分类信息（名称、类型、排序），点"保存"按钮。前端把这个数据打包成 JSON 发给后端，后端这个方法就负责：接收数据 → 打印日志 → 交给 Service 保存到数据库 → 告诉前端"成功了"。
@PostMapping
  PostMapping = POST 请求映射。告诉 Spring："当前端用 POST 方式访问这个类对应的地址时，调用这个方法。"
  POST 是 HTTP 的四种常用请求方式之一，语义是"提交/新增数据"：
  GET：查询（只读）
  POST：新增（提交数据）
  PUT：修改
  DELETE：删除
  为什么新增用 POST？因为新增需要携带数据发给服务器，POST 允许把数据放在请求体（body）里，安全且不限制长度。
  注意它没有写路径：因为它会继承类上 @RequestMapping("/admin/category") 的地址 : POST http://localhost:8080/admin/category

@ApiOperation("新增分类")
  ApiOperation = 接口操作说明。Swagger 的注解，作用是在接口文档网页上，给这个方法显示一条说明："新增分类"。
  不写它接口也能跑，但文档里就没人知道这个接口是干嘛的了。

public Result<String> save(@RequestBody CategoryDTO categoryDTO)
  这一行信息量最大，拆成四块：
  ① public：公共方法，任何人（其实是任何框架代码）都能调用。
  ② Result<String>：返回值类型。Result 是项目自定义的"统一返回结果"类（在 sky-common 模块里），它的作用：不管哪个接口，返回给前端的格式都一样，包含三个字段：
    private Integer code; // 编码：1 成功，0 失败
    private String msg;   // 提示信息
    private T data;       // 数据
    为什么叫"统一"？如果每个接口都自己定返回格式（一会儿返回字符串、一会儿返回对象），前端就得写几十种解析代码，累死人。
    统一成 {code, msg, data} 后，前端只看 code 是 1 还是 0 就知道成败。
    <String> 是泛型：Result<T> 里的 T 是个"占位符"，表示"data 里装什么类型的数据，用的时候再定"。这里写 Result<String>，意思是"如果成功要带数据，data 里装的是字符串类型"。
  ③ save：方法名，见名知义——保存。小驼峰命名。
  ④ @RequestBody CategoryDTO categoryDTO：这是方法的入参，拆开讲：
    @RequestBody（请求体）：一个注解。它告诉 Spring："前端发来的 JSON 字符串在请求体里，请帮我自动转换成一个 Java 对象。"
    比如前端发来：{"name":"家常菜","type":1,"sort":1}
    Spring 会自动把它解析成 CategoryDTO 对象，并把 name、type、sort 填进对应字段里。这个过程叫 反序列化。没有这个注解，Spring 就不知道该去哪里取数据。
    CategoryDTO：一个"数据传输对象"（DTO = Data Transfer Object，直译"数据传输对象"）。它长这样：
     private Long id;       // 主键
     private Integer type;  // 类型 1 菜品分类 2 套餐分类
     private String name;   // 分类名称
     private Integer sort;  // 排序
    为什么不用实体类 Category 接收，而要专门弄个 DTO？ 这是分层思想：
    Entity（实体）：和数据库表一一对应，表里有什么字段它就有什么字段，用于持久层。
    DTO：只装"这一次请求/响应需要的字段"，用于前端和 Controller 之间传数据。
    两者分开后，数据库表改了不影响前端传参，前端要多传个临时字段也不用动实体类，各层之间不互相干扰。前端发的 JSON 里没有 id（新增时 id 由数据库自增生成），所以 DTO 里 id 可以为空，灵活。
    
log.info("新增分类:{}", categoryDTO);
  log：@Slf4j 注解在编译时自动生成的日志对象（上一轮讲过）。
  info：日志级别，表示"普通信息"。常见级别从低到高：debug < info < warn < error。
  {} 是占位符：日志框架（这里是 SLF4J）会把后面的参数按顺序填进 {} 里，最终输出类似：新增分类:CategoryDTO(id=null, type=1, name=家常菜, sort=1)
  为什么要打日志？ 出问题时，程序员看控制台日志就知道"前端到底传了什么数据过来"，是排查 bug 的第一现场。这是项目规范：每个 Controller 方法入口都打一条日志。

categoryService.save(categoryDTO);
  调用服务层的方法。Controller 是"接待员"，只负责接收和返回，不亲自碰数据库；真正的业务逻辑交给 CategoryService 去干。
  这就是三层架构的分工：
   Controller（控制层）：收请求、返回结果
        ↓ 调用
   Service（业务层）：处理业务逻辑（比如校验名称是否重复）
        ↓ 调用
   Mapper（持久层）：真正执行 SQL 操作数据库

return Result.success();
  Result.success() 是 Result 类里的一个静态方法，看它的源码：
   public static <T> Result<T> success() {
    Result<T> result = new Result<T>();
    result.code = 1;
    return result;
   }
  它 new 一个 Result 对象，把 code 设为 1（成功），msg 和 data 留空，然后返回。
  框架会把这个对象自动转成 JSON 发给前端，前端收到的就是： { "code": 1, "msg": null, "data": null }

用户点"保存"
   ↓ 前端发送 POST /admin/category，body 里带 JSON 数据
   ↓ Spring 根据 @PostMapping + @RequestMapping 找到 save 方法
   ↓ @RequestBody 把 JSON 自动转成 CategoryDTO 对象
   ↓ log.info 打印日志（能看到前端传了什么）
   ↓ categoryService.save() 执行业务，最终 Mapper 把数据写进数据库
   ↓ return Result.success() → 转成 JSON 返回前端
前端收到 {"code":1}，提示"新增成功"


分类`分页查询：
  分类多了以后（比如几百个），不可能一次性全显示在管理页面里，得一页一页地看。这个方法就是干这个的：
  前端说："我要第 2 页，每页 10 条，名称里带'菜'字的分类" → 后端查数据库 → 返回"总共有多少条 + 当前这一页的数据"。
public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO)
拆成四块：
  ① Result<PageResult>：返回值类型。Result 是统一返回结果的壳子（code/msg/data），而泛型 <PageResult> 表示这次 data 里装的是 PageResult 类型的数据。
  ② page：方法名，表示"按页查询"。
  ③ CategoryPageQueryDTO：专门用来装查询条件的 DTO，看它的字段：
     private int page;       // 页码（第几页）
     private int pageSize;   // 每页记录数（一页显示几条）
     private String name;    // 分类名称（按名称模糊搜索）
     private Integer type;   // 分类类型（1 菜品分类 2 套餐分类）
  ④ 注意：这个参数前面没有 @RequestBody！ 这是和上一个方法最大的区别：
     新增用 POST，数据放在**请求体（body）**里，所以要用 @RequestBody 去 body 里取；
     查询用 GET，数据是跟在 URL 后面的，长这样：/admin/category/page?page=2&pageSize=10&name=菜&type=1
      ? 后面的部分叫 Query String（查询字符串），格式是 键=值，多个用 & 连接。
      Spring 会自动把 URL 里的参数按名字对应填进 DTO 的字段里（page=2 → page 字段 = 2，name=菜 → name 字段 = "菜"）。这叫参数绑定，所以 GET 查询不需要任何注解。
      
log.info("分类分页查询:{}, categoryPageQueryDTO");
  意图是打印日志，记录前端传了什么查询条件。     

PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
  调用 Service 层去查询。把"查询条件"传进去，Service 查完数据库后返回一个 PageResult 对象。
  PageResult 是"分页查询结果的封装类"，看它的字段：
   private long total;   // 总记录数（数据库里符合条件的分类一共有多少条）
   private List records; // 当前页数据集合（本页要显示的那些分类对象）
    为什么要把结果封装成 {total, records} 两个字段？因为前端分页组件（页码条）需要两个信息才能渲染：
    total：总条数 → 用来算"一共有几页"（比如 105 条 ÷ 每页 10 条 = 11 页）；
    records：本页实际数据 → 用来渲染表格里的行。

return Result.success(pageResult);
  调 Result.success(带参数版本)，把 pageResult 塞进 data 字段

用户在管理页面点"第 2 页"，输入搜索条件
   ↓ 前端发 GET /admin/category/page?page=2&pageSize=10&name=菜
   ↓ Spring 根据 @GetMapping("/page") 找到 page 方法
   ↓ 自动把 URL 参数绑定到 CategoryPageQueryDTO 对象
   ↓ log.info 打日志（当前这行有 bug，占位符没生效）
   ↓ categoryService.pageQuery() 执行 SQL：LIMIT 限定范围 + COUNT 统计总数
   ↓ 返回 PageResult{total: 总条数, records: 本页数据}
   ↓ Result.success(pageResult) 包装成统一格式
前端收到 {code:1, data:{total, records}}，渲染表格和页码条


修改分类：
  跟新增分类一样 方法名不同而已


启用禁用分类：
  管理页面的分类列表里，每一行有个"启用/禁用"开关。点一下开关，前端就告诉后端："把 id 为 5 的分类的状态改成 1（启用）"。这个方法负责接收这两个信息，交给 Service 去更新数据库。
public Result<String> startOrStop(@PathVariable("status")Integer status, Long id)
  拆成四块：
  ① Result<String>：返回值，统一返回结果的壳子（成功与否 + data），这里不返回数据，所以 success() 空参调用。
  ② startOrStop：方法名，见名知义——"启动或停止"，即启用或禁用。
  ③ @PathVariable("status") Integer status：第一个入参，重点讲：
    @PathVariable = 路径变量注解：告诉 Spring"去 URL 路径里把 {status} 坑里的值取出来，装进这个参数"。
    还记得项目里那条绑定规则吗：@PathVariable 必须和路径里的占位符 {xxx} 配合使用。
    路径 "/status/{status}" 里挖了 {status} 这个坑，注解 @PathVariable("status") 就负责把坑里的值取出来——坑名和注解里的名字必须对得上。
    如果前端访问 /status/1，Spring 就把 1 转成数字装进 status 参数里。
    Integer：整型包装类。status 的值只有 1（启用）和 0（禁用）两种，正好对应项目里 StatusConstant 常量类的定义。
  ④ Long id：第二个入参。注意它前面没有任何注解！因为 id 不来自路径，而是来自 URL 问号后面（和分页查询、删除一样），Spring 按参数名自动绑定。实际请求长这样：
    POST /admin/category/status/1?id=5
                          ↑     ↑
                    路径变量    查询字符串参数
   一个方法两个参数，来自两个完全不同的位置，这是这个方法的精髓。

categoryService.startOrStop(status,id);
  把两个实参（status、id）按位置传给 Service 层的同名方法。

return Result.success();
  无参调用，只标记"成功"，不带数据。前端收到 {code:1} 就知道开关切换成功，刷新列表即可。

管理员在列表里点 id=5 的分类的"启用"开关
   ↓ 前端发 POST /admin/category/status/1?id=5
   ↓ Spring 匹配 @PostMapping("/status/{status}")
   ↓ @PathVariable("status") 从路径取出 1 → 装进 status 参数
   ↓ 自动绑定从 ?id=5 取出 5 → 装进 id 参数
   ↓ categoryService.startOrStop(1, 5) → Service 校验 → Mapper 执行 UPDATE
   ↓ return Result.success() → 前端收到 {code:1}


根据类型查询分类：
  它的用途和分页查询完全不同。想象这个场景：商家在后台新增一道菜品时，页面上要让他选"这道菜属于哪个分类"，此时需要一个下拉框，列出所有"菜品分类"（type=1）。
  这个方法就是干这个的：
  前端说："把所有 type=1（菜品分类） 的分类都给我" → 后端返回一个列表（不带分页，一次性全给，因为下拉框要显示全部选项）。
public Result<List<Category>> list(Integer type)
  这行有两个新知识点，拆开讲：
  ① Result<List<Category>>——泛型的嵌套
    之前见过 Result<String>（data 里装字符串）、Result<PageResult>（data 里装分页对象）；
    这次是 Result<List<Category>>，读作："Result 的 data 里装的是一个 List，这个 List 里面每个元素都是 Category 对象"。
    List 是集合（Collection）：一个"能装很多个元素的袋子"，有序、可以重复。数据库里查出来 10 条分类，就装成 10 个 Category 对象放进一个 List 里。
    Category 是实体类（Entity）：和数据库 category 表一一对应，表里有什么字段它有什么字段（id、type、name、sort、status、时间、创建人……），每一行数据就是一个 Category 对象。
    泛型可以一层套一层：List<Category> 表示"装 Category 的 List"，外层 Result<...> 表示"data 字段装这个 List"。尖括号里套尖括号，就像快递箱子套快递箱子：Result 是外箱，List 是中箱，Category 是里面的物品。
  ② Integer type——入参，没有注解
    和分页查询一样，type 来自 URL 问号后面：/list?type=1，Spring 按参数名自动绑定到 type 变量；
    Integer 是整型包装类，值只可能是 1 或 2（1=菜品分类，2=套餐分类）。
    
List<Category> list = categoryService.list(type);
  拆成三部分：
  List<Category> list：声明一个局部变量，类型是"装 Category 的 List"，名字叫 list；
  =：把右边的查询结果赋值给左边的变量；
  categoryService.list(type)：调用 Service 层，把 type 传进去，Service 查数据库后返回一个装满了 Category 对象的 List。
  执行完这行，list 变量里就有数据了，比如 5 个菜品分类对象。
  
return Result.success(list);
  调用 success(带参数) 版本，把整个 list（装分类对象的集合）塞进 Result 的 data 字段；
  data 是一个 JSON 数组（[ ] 包起来），前端拿到后直接循环渲染成下拉框的选项。

商家点开"新增菜品"页面，前端需要菜品分类下拉框
   ↓ 前端发 GET /admin/category/list?type=1
   ↓ Spring 匹配 @GetMapping("/list")，自动绑定 type=1
   ↓ categoryService.list(1) → Mapper 执行 SELECT ... WHERE type = 1
   ↓ 返回 List<Category>（若干分类对象）
   ↓ Result.success(list) 塞进 data
前端收到 {code:1, data:[分类1,分类2,...]}，渲染下拉框
****************************************************************************************************************************************

------------------
server-service包下新建 “模块名”Service（I） 这里编写的是接口，每个新建接口右上角可以直接去到实现类。接着server-service-impl包下新建每个接口的实现 “模块名”Serviceimpl（J）。||
--------------------------

****************************************************************************************************************************************

------------------
server-mapper包下新建 “模块名”Mapper 这里编写的是操控SQL的接口，实现类在server-resources-mapper包下 “模块名”Mapper.xml ；如果功能的业务逻辑差别大，可以单独把功能分出来比如在 “模块名”Mapper里分出 “功能名1”Mapper “功能名2”Mapper ...这些单独分出的也是操控SQL的接口，但是他不依赖xml去实现，要在本类下去注解实现（一般是复用不高或是简单的SQL语句）||
--------------------------

****************************************************************************************************************************************

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

项目需求接口文档在Apifox里


通过swagger-knife4j生成的接口文档在server-config，前端/doc.html访问。登录调试可以拿到新token


接口文档里的全局参数设置token有效时间改成了24h。实现：server-resources-application.yml里admin-ttl: 


server-service-EmployeeServicelmpl里添加了 md5加密，新增员工功能。 还有个TODO（已解决）。


server-handler-GlobalExcptionHandler里添加了 处理SQL异常，新增重复员工抛出错误，程序不再崩溃


TODO内容：利用ThreadLocal线程实现动态新增员工 将登录者的id放入线程，谁登录了就取谁的id，使得系统操作可追溯


添加了员工账号分页查询功能  DTO的东西 SQL语句符号的应用 用了个mybatis的插件“Pagehelper”


添加了启用禁用员工账号功能 SQL语句符号的应用


写调用接口的东西有些注解不太懂？ 敲分类模块的时候尽量去解决了


添加了根据id查询员工，编辑员工信息功能

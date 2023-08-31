# SpringMVC笔记

​	SpringMVC隶属于Spring框架的一部分，主要是用来进行Web开发，其对Servlet进行了封装。

​	SpringMVC的主要内容:

* ==请求与响应==
* ==REST风格==
* ==SSM整合(注解版)==
* 拦截器

​	`SpringMVC`是处于Web层的框架，所以其主要的作用就是用来接收前端发过来的请求和数据然后经过处理并将处理的结果响应给前端，所以如何处理请求和响应是SpringMVC中非常重要的一块内容。

​	`REST`是一种软件架构风格，可以降低开发的复杂性，提高系统的可伸缩性，后期的应用也是非常广泛。

​	SSM整合是把SpringMVC+Spring+Mybatis整合在一起来完成业务开发，是对这三个框架的一个综合应用。

## 0.0	序言

​	我们先前的web程序主要是这样的

![1630427769938](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630427769938.png)



* SpringMVC==主要==负责的就是
    * controller层如何接收请求和数据
    * 如何将请求和数据转发给业务层service
    * 如何将响应数据转换成json发回到前端
    * SpringMVC的优点

        * 使用简单、开发便捷(相比于Servlet)
        * 灵活性强

## 1.0	SpringMVC，启动!

### 1.1	创建web工程(Maven结构)

​	预设骨架选webapp，并导入相关maven依赖

```xml
<dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>javax.servlet-api</artifactId>
      <version>3.1.0</version>
      <scope>provided</scope>
    </dependency>
<dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-webmvc</artifactId>
      <version>5.2.10.RELEASE</version>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.tomcat.maven</groupId>
        <artifactId>tomcat7-maven-plugin</artifactId>
        <version>2.1</version>
        <configuration>
          <port>80</port>
          <path>/</path>
        </configuration>
      </plugin>
    </plugins>
      
```

​	**==注意==**

* `scope`是maven中jar包依赖作用范围的描述，如果不设置默认是`compile`在在编译、运行、测试时均有效
* 如果运行有效的话就会和tomcat中的servlet-api包发生冲突，导致启动报错。provided代表的是该包只在编译和测试的时候用，运行的时候无效直接使用tomcat中的,==避免冲突==

### 1.2	设置tomcat服务器，加载web工程(tomcat插件)

### 1.3	导入坐标(==SpringMVC==+Servlet)

​	创建配置类，设置扫描区域等。

```java
@Configuration
@ComponentScan("com.itheima.controller")
public class SpringMvcConfig {
}
```



### 1.4	定义处理请求的功能类(==UserController==)

 	1. 创建配置类
 	2. 创建Controller类，用注解@Controller将其设置为bean，用@RequestMapping()对应的方法。

### 1.5	==设置请求映射(配置映射关系)==

​	使用配置类替换web.xml。将web.xml删除，换成`ServletContainersInitConfig`配置类。这是一个servlet容器启动的配置类，在里面加载spring的配置。

```java
public class ServletContainersInitConfig extends AbstractDispatcherServletInitializer {
    //加载springmvc配置类
    protected WebApplicationContext createServletApplicationContext() {
        //初始化WebApplicationContext对象
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        //加载指定配置类
        ctx.register(SpringMvcConfig.class);
        return ctx;
    }

    //设置由springmvc控制器处理的请求映射路径
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    //加载spring配置类
    protected WebApplicationContext createRootApplicationContext() {
        return null;
    }
}
```



### 1.6	==将SpringMVC设定加载到Tomcat容器中==

**注意事项**

* SpringMVC是基于Spring的，在pom.xml只导入了`spring-webmvc`jar包的原因是它会自动依赖spring相关坐标
* AbstractDispatcherServletInitializer类是SpringMVC提供的快速初始化Web3.0容器的抽象类
* AbstractDispatcherServletInitializer提供了三个接口方法供用户实现
    * `createServletApplicationContext`方法，创建Servlet容器时，加载SpringMVC对应的bean并放入WebApplicationContext对象范围中，而WebApplicationContext的作用范围为ServletContext范围，即整个web容器范围
    * `getServletMappings`方法，设定SpringMVC对应的请求映射路径，即SpringMVC拦截哪些请求
    * `createRootApplicationContext`方法，如果创建Servlet容器时需要加载非SpringMVC对应的bean,使用当前方法进行，使用方式和`createServletApplicationContext`相同。
    * `createServletApplicationContext`用来加载SpringMVC环境
    * `createRootApplicationContext`用来加载Spring环境

#### 	知识点1：@Controller

| 名称 | @Controller                   |
| ---- | ----------------------------- |
| 类型 | 类注解                        |
| 位置 | SpringMVC控制器类定义上方     |
| 作用 | 设定SpringMVC的核心控制器bean |

#### 	知识点2：@RequestMapping

| 名称     | @RequestMapping                 |
| -------- | ------------------------------- |
| 类型     | 类注解或方法注解                |
| 位置     | SpringMVC控制器类或方法定义上方 |
| 作用     | 设置当前控制器方法请求访问路径  |
| 相关属性 | value(默认)，请求访问路径       |

#### 	知识点3：@ResponseBody

| 名称 | @ResponseBody                                        |
| ---- | ---------------------------------------------------- |
| 类型 | **类**注解或**方法**注解                             |
| 位置 | SpringMVC控制器类或方法定义上方                      |
| 作用 | ==设置当前控制器方法响应内容为当前返回值==，无需解析 |

### 1.7	单次请求过程

1. 发送请求`http://localhost/save`
2. web容器发现该请求满足SpringMVC拦截规则，将请求交给SpringMVC处理
3. 解析请求路径/save
4. 由/save匹配执行对应的方法save(）
    * 上面的第五步已经将请求路径和方法建立了对应关系，通过/save就能找到对应的save方法
5. 执行save()
6. 检测到有==@ResponseBody==直接将save()方法的返回值作为响应体返回给请求方

## 2.0	bean加载控制

​	因为功能不同，我们需要避免Spring中的bean错误地加载到springMVC中，反之亦然。解决方案是:==**加载spring控制的bean的时候排除掉springMVC控制的bean**==

​	具体该如何排除：

* 方式一:Spring加载的bean设定扫描范围为精准范围，例如service包、dao包等
* 方式二:Spring加载的bean设定扫描范围为com.itheima,排除掉controller包中的bean
* 方式三:不区分Spring与SpringMVC的环境，加载到同一个环境中[了解即可]

### 2.1	方案一:设定扫描范围为精确范围

```java
@Configuration
@ComponentScan({"com.itheima.service","comitheima.dao"})
public class SpringConfig {
}
```

​	**说明:**

​	上述只是通过例子说明可以精确指定让Spring扫描对应的包结构，真正在做开发的时候，因为Dao最终是交给`MapperScannerConfigurer`对象来进行扫描处理的，我们只需要将其扫描到service包即可。

### 2.2	方案二:修改Spring配置类-->excludeFilers属性

```java
@Configuration
@ComponentScan(value="com.itheima",
    excludeFilters=@ComponentScan.Filter(
    	type = FilterType.ANNOTATION,
        classes = Controller.class
    )
)
public class SpringConfig {
}
```

* excludeFilters属性：设置扫描加载bean时，排除的过滤规则

* type属性：设置排除规则，当前使用按照bean定义时的注解类型进行排除

    * ANNOTATION：按照注解排除
    * ASSIGNABLE_TYPE:按照指定的类型过滤
    * ASPECTJ:按照Aspectj表达式排除，基本上不会用
    * REGEX:按照正则表达式排除
    * CUSTOM:按照自定义规则排除

    大家只需要知道第一种ANNOTATION即可

* classes属性：设置排除的具体注解类，当前设置排除@Controller定义的bean

### 2.4	==更简单的环境配置,简化开发==

​	有了Spring的配置类，要想在tomcat服务器启动将其加载，我们需要修改ServletContainersInitConfig

```java
public class ServletContainersInitConfig extends AbstractDispatcherServletInitializer {
    protected WebApplicationContext createServletApplicationContext() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(SpringMvcConfig.class);
        return ctx;
    }
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
    protected WebApplicationContext createRootApplicationContext() {
      AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(SpringConfig.class);
        return ctx;
    }
}
```

​	**但是**Spring提供了一种更简单的环境配置:可以不用再去创建`AnnotationConfigWebApplicationContext`对象，不用手动`register`对应的配置类

```java
public class ServletContainersInitConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{SpringConfig.class};
    }

    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{SpringMvcConfig.class};
    }

    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
```

## 3.0	请求和响应

### 3.1	设置请求映射路径-->@RequestMapping()

* 当类上和方法上都添加了`@RequestMapping`注解，前端发送请求的时候，要和两个注解的value值相加匹配才能访问到。
    * 类上加`@RequestMapping`注解可以降低耦合度，修改时只要修改类上注解就会对所有方法上的对应注解生效，且类上注解一般不同，可以避免不同方法url相同的问题。
* `@RequestMapping`注解value属性前面加不加`/`都可以

### 3.2	请求参数

​	无论是GET请求还是POST请求(数据在请求体里格式为`x-www-form-urlencoded`)，在后端都只需要方法形参里设置与前端请求的==类型和名称相同的形式参数==即可，例如:

```java
@Controller
public class UserController {

    @RequestMapping("/commonParam")
    @ResponseBody
    public String commonParam(String name,int age){
        System.out.println("普通参数传递 name ==> "+name);
        System.out.println("普通参数传递 age ==> "+age);
        return "{'module':'commonParam'}";
    }
}
```

#### 3.2.1	POST请求传递中文数据乱码问题-->过滤器Fliter

​	Spring为我们提供了一个设定好的==字符过滤器CharacterEncodingFilter==，我们只需要new其对象并设置字符集即可。

```java
public class ServletContainersInitConfig extends AbstractAnnotationConfigDispatcherServletInitializer {
    // ......

    //乱码处理
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        return new Filter[]{filter};
    }
}
```

#### 3.2.2	一种请求参数名字和后台方法形参对不上或者压根没有响应请求参数的处理方式-->@RequestParam()

```java
@RequestMapping("/commonParamDifferentName")
    @ResponseBody
    public String commonParamDifferentName(@RequestParam("name") String userName , int age){
		// ...
        return "{'module':'common param different name'}";
    }
```

​	@RequestParam()括号内填**默认值**。

### 3.3	五种类型参数传递

​	常见的参数种类有:

* 普通参数
* POJO类型参数
* 嵌套POJO类型参数
* 数组类型参数
* 集合类型参数

​	一句话总结算了:

- 方法==形参名字==对应request类型参数名字；
- pojo类的==属性名字==对应request类型参数名字
- 嵌套POJO类型中==其嵌套的pojo类的属性名字==对应request中**`嵌套pojo类.参数名字`**
- 数组类型参数名字对应request类型参数名字==(重复的)==

- 集合类型参数名字对应request类型参数名字==(重复的)==，且形参前要加==@RequestParam==注解。

#### 知识点1：@RequestParam

| 名称     | @RequestParam                                          |
| -------- | ------------------------------------------------------ |
| 类型     | 形参注解                                               |
| 位置     | SpringMVC控制器方法形参定义前面                        |
| 作用     | 绑定请求参数与处理器方法形参间的关系                   |
| 相关参数 | required：是否为必传参数 <br/>defaultValue：参数默认值 |

### 3.4	JSON数据传输参数

##### 	第一步:添加jackson的依赖

​	SpringMVC默认使用的是jackson来处理json的转换，所以需要在pom.xml添加jackson依赖

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.0</version>
</dependency>
```

##### 	第二步:开启SpringMVC的注解支持-->@EnableWebMvc

​	在SpringMVC的配置类中开启SpringMVC的注解支持，这里面就包含了将JSON转换成对象的功能。

```java
@Configuration
@ComponentScan("com.itheima.controller")
//开启json数据类型自动转换
@EnableWebMvc
public class SpringMvcConfig {
}
```

##### 	第三步:参数前添加@RequestBody

​	使用@RequestBody注解将外部传递的**json数组数据**==映射到**形参的集合对象**==中作为数据

```java
@RequestMapping("/listParamForJson")
@ResponseBody
public String listParamForJson(@RequestBody List<String> likes){
    System.out.println("list common(json)参数传递 list ==> "+likes);
    return "{'module':'list common for json param'}";
}
```

##### 	第四步:如果集合对象是引用类型?

​	不用做更多处理，只需遵循好3.3和3.4的规则就好。

#### 	知识点1：@EnableWebMvc

| 名称 | @EnableWebMvc             |
| ---- | ------------------------- |
| 类型 | ==配置类注解==            |
| 位置 | SpringMVC配置类定义上方   |
| 作用 | 开启SpringMVC多项辅助功能 |

#### 	知识点2：@RequestBody

| 名称 | @RequestBody                                                 |
| ---- | ------------------------------------------------------------ |
| 类型 | ==形参注解==                                                 |
| 位置 | SpringMVC控制器方法形参定义前面                              |
| 作用 | 将请求中请求体所包含的数据传递给请求参数，此注解一个处理器方法只能使用一次 |

### 3.41	@RequestBody与@RequestParam区别

* 区别
    * ==@RequestParam==用于接收**url地址传参**，**表单传参**`【application/x-www-form-urlencoded】`
    * ==@RequestBody==用于接收**json数据**`【application/json】`

* 应用
    * 后期开发中，发送json格式数据为主，@RequestBody应用较广
    * 如果发送非json格式数据，选用@RequestParam接收请求参数

### 3.5	日期类型参数传递

​	日期类型比较特殊，因为对于日期的格式有N多中输入方式，比如:

* 2088-08-18
* 2088/08/18
* 08/18/2088
* ......

 	其中，SpringMVC==默认支持==的字符串转日期的格式为**`yyyy/MM/dd`**。使用其它格式时会抛出异常。

#### 3.51	解决方案-->@DateTimeFormat

​	使用`@DateTimeFormat`注解于方法的日期形参上，并在注解的()内设置好==时间字符串的格式==

```java
@RequestMapping("/dataParam")
@ResponseBody
public String dataParam(Date date,
                        @DateTimeFormat(pattern="yyyy-MM-dd") Date date1)
    System.out.println("参数传递 date ==> "+date);
	System.out.println("参数传递 date1(yyyy-MM-dd) ==> "+date1);
    return "{'module':'data param'}";
}
```

#### 知识点1：@DateTimeFormat

| 名称     | @DateTimeFormat                 |
| -------- | ------------------------------- |
| 类型     | ==形参注解==                    |
| 位置     | SpringMVC控制器方法形参前面     |
| 作用     | 设定日期时间型数据格式          |
| 相关属性 | pattern：指定日期时间格式字符串 |

### 3.6	此类转换的内部实现原理

​	讲解内部原理之前，我们需要先思考个问题:

* 前端传递字符串，后端使用日期Date接收
* 前端传递JSON数据，后端使用对象接收
* 前端传递字符串，后端使用Integer接收
* 在数据的传递过程中存在很多类型的转换

​	这个类型转换是由SpringMVC来做的，SpringMVC中提供了很多类型转换接口和实现类。

​	在SpringMVC框架中，有一些类型转换接口，其中有:

* `Converter接口`

```java
/**
*	S: the source type
*	T: the target type
*/
public interface Converter<S, T> {
    @Nullable
    //该方法就是将从页面上接收的数据(S)转换成我们想要的数据类型(T)返回
    T convert(S source);
}
```

​	**注意:Converter所属的包为`org.springframework.core.convert.converter`**

​	Converter接口的实现类:

![1630496385398](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630496385398.png)

​	框架中有提供很多对应Converter接口的实现类，用来实现不同数据类型之间的转换,如:

​	请求参数年龄数据==（String→Integer）==

​	日期格式转换==（String → Date）==

* `HttpMessageConverter接口`

​	该接口是实现==对象与JSON之间==的转换工作

​	**==注意:SpringMVC的配置类把@EnableWebMvc当做标配配置上去，不要省略==**

## 4.0	响应

​	对于响应，主要就包含两部分内容：

* 响应页面
* 响应数据
    * 文本数据
    * json数据

​	因为异步调用是目前常用的主流方式，所以我们需要更关注的就是如何返回JSON数据，对于其他只需要认识了解即可。

### 4.1	响应页面

​	**注意:**
​    1.此处**不能**添加@ResponseBody,如果加了该注入，会直接将page.jsp当**字符串**返回前端
​    2.方法需要==返回String==

```java
@Controller
public class UserController {
    
    @RequestMapping("/toJumpPage")
    //注意
    //1.此处不能添加@ResponseBody,如果加了该注入，会直接将page.jsp当字符串返回前端
    //2.方法需要返回String
    public String toJumpPage(){
        System.out.println("跳转页面");
        return "page.jsp";
    }
    
}
```

​	相应页面的话只需要==return页面文件名字==即可。

### 4.2	响应数据

​	其实要响应文本数据和json数据都是==一样的操作==。都是加注解==@ResponseBody==即可。

```java
@Controller
public class UserController {    
   	@RequestMapping("/toText")
	//注意此处该注解就不能省略，如果省略了,会把response text当前页面名称去查找，如果没有回报404错误
    @ResponseBody
    public String toText(){
        System.out.println("返回纯文本数据");
        return "response text";
    }
}
public class UserController2 {
    @RequestMapping("/toJsonList")
    @ResponseBody
    public List<User> toJsonList(){
        System.out.println("返回json集合数据");
        User user1 = new User();
        user1.setName("传智播客");
        user1.setAge(15);
        User user2 = new User();
        user2.setName("黑马程序员");
        user2.setAge(12);
        List<User> userList = new ArrayList<User>();
        userList.add(user1);
        userList.add(user2);
        return userList;
    }   
}
```

​	由此可见无论是文本数据还是pojo对象还是pojo的集合都是一样的操作。

##### 	知识点1：@ResponseBody

| 名称     | @ResponseBody                                                |
| -------- | ------------------------------------------------------------ |
| 类型     | ==方法\类注解==                                              |
| 位置     | SpringMVC控制器方法定义上方和控制类上                        |
| 作用     | 设置当前控制器返回值作为响应体,<br/>写在类上，该类的所有方法都有该注解功能 |
| 相关属性 | pattern：指定日期时间格式字符串                              |

**说明:**

* 该注解可以写在类上或者方法上
* 写在类上就是该类下的==所有方法都有@ReponseBody==功能
* 当方法上有@ReponseBody注解后
    * 方法的返回值为**字符串**，会将其==作为文本内容==直接响应给前端
    * 方法的返回值为**对象**，会将==对象转换成JSON==响应给前端

​	此处又使用到了类型转换，内部还是通过`Converter`接口的实现类完成的，所以`Converter`除了前面所说的功能外，它还可以实现:

* 对象转Json数据==(POJO -> json)==
* 集合转Json数据==(Collection -> json)==

## 5.0	REST风格

​	==REST==（Representational State Transfer），表现形式状态转换,它是一种软件架构==风格==。

​	当我们想表示一个网络资源的时候，可以使用两种方式:

* 传统风格资源描述形式
    * `http://localhost/user/getById?id=1` 查询id为1的用户信息
    * `http://localhost/user/saveUser` 保存用户信息
* REST风格描述形式
    * `http://localhost/user/1` 
    * `http://localhost/user`

​	传统方式一般是一个请求url对应一种操作，这样做不仅麻烦，也不安全，因为会程序的人读取了你的请求url地址，就大概知道该url实现的是一个什么样的操作。

​	而REST风格的优点有:

- 隐藏资源的访问行为，无法通过地址得知对资源是何种操作
- 书写简化

## 5.1	REST风格的实现

​	REST风格实际上是用不同种类的请求来实现url的精简的:

* `http://localhost/users`	查询全部用户信息 GET（查询）
* `http://localhost/users/1`  查询指定用户信息 GET（查询）
* `http://localhost/users`    添加用户信息    POST（新增/保存）
* `http://localhost/users`    修改用户信息    PUT（修改/更新）
* `http://localhost/users/1`  删除用户信息    DELETE（删除）
* 按照不同的请求方式代表不同的操作类型。

    * 发送GET请求是用来做查询
    * 发送POST请求是用来做新增
    * 发送PUT请求是用来做修改
    * 发送DELETE请求是用来做删除

​	==注意==:

* 上述行为是约定方式，约定不是规范，------可以打破，所以称`REST风格`，而不是REST规范
    * REST提供了对应的架构方式，按照这种架构设计项目可以降低开发的复杂性，提高系统的可伸缩性
    * REST中规定GET/POST/PUT/DELETE针对的是查询/新增/修改/删除，但是我们如果非要用GET请求做删除，这点在程序上运行是可以实现的
    * 但是如果绝大多数人都遵循这种风格，你写的代码让别人读起来就有点莫名其妙了。
* 描述模块的名称通常使用复数，也就是==加s的格式==描述，表示此类资源，而非单个资源，例如:users、books、accounts......

### 5.3	RESTful，启动!

​	根据REST风格对资源进行访问称为==RESTful==。以后的项目最后都要遵循REST风格。

#### 5.31	传递路径参数

​	前端发送请求的时候使用:`http://localhost/users/1`,路径中的`1`就是我们想要传递的参数。

​	后端获取参数时，需要做如下修改:

​	修改@RequestMapping的value属性，将其中修改为`/users/{id}`，目的是和路径匹配

​	在方法的形参前添加@PathVariable注解,同时==形参名==与==value属性中的参数id==也要匹配。

​	例子:

* 设定Http请求动作(动词)

    * @RequestMapping(value="",==method== = RequestMethod.==POST|GET|PUT|DELETE==)

* 设定请求参数(路径变量)

    @RequestMapping(value="/users/=={id}==",method = RequestMethod.DELETE)

    @ReponseBody

    public String delete(==@PathVariable== Integer ==id==){

    }

##### 	知识点1：@PathVariable

| 名称 | @PathVariable                                                |
| ---- | ------------------------------------------------------------ |
| 类型 | ==形参注解==                                                 |
| 位置 | SpringMVC控制器方法形参定义前面                              |
| 作用 | 绑定路径参数与处理器方法形参间的关系，要求路径参数名与形参名一一对应 |

#### 5.32	关于接收参数的三个注解`@RequestBody`、`@RequestParam`、`@PathVariable`,这三个注解之间的区别和应用

* **区别**
    * @RequestParam用于接收==url地址传参或表单传参==
    * @RequestBody用于接收==json数据==
    * @PathVariable用于==接收路径参数==，使用{参数名称}描述路径参数
* 应用
    * 后期开发中，发送请求参数超过1个时，以json格式为主，@RequestBody应用较广
    * 如果发送非json格式数据，选用@RequestParam接收请求参数
    * 采用RESTful进行开发，当参数数量较少时，例如1个，可以采用@PathVariable接收请求路径变量，通常用于传递id值

### 5.4	利用Spring进一步简化RESTful的实现

​	5.3的实现有以下三个问题

​	问题1：每个方法的@RequestMapping注解中都定义了访问路径/books，重复性太高。

​	问题2：每个方法的@RequestMapping注解中都要使用method属性定义请求方式，重复性太高。

​	问题3：每个方法响应json都需要加上@ResponseBody注解，重复性太高。

​	接下来是解决问题

#### 5.41	第一个问题

​	解决方案:将@RequestMapping提到==类==上面，用来定义所有方法共同的访问路径。

#### 5.42	第二个问题

​	解决方案:使用@GetMapping  @PostMapping  @PutMapping  @DeleteMapping代替。`value`属性照写。

#### 5.43	第三个问题

解决方案:1.将@ResponseBody提到==类==上面，让所有的方法都有@ResponseBody的功能

​				2.使用`@RestController`注解==替换==`@Controller`与`@ResponseBody`注解，简化书写

##### 	知识点1：@RestController

| 名称 | @RestController                                              |
| ---- | ------------------------------------------------------------ |
| 类型 | ==类注解==                                                   |
| 位置 | 基于SpringMVC的RESTful开发控制器类定义上方                   |
| 作用 | 设置当前控制器类为RESTful风格，<br/>等同于@Controller与@ResponseBody两个注解组合功能 |

##### 	知识点2：@GetMapping @PostMapping @PutMapping @DeleteMapping

| 名称     | @GetMapping @PostMapping @PutMapping @DeleteMapping          |
| -------- | ------------------------------------------------------------ |
| 类型     | ==方法注解==                                                 |
| 位置     | 基于SpringMVC的RESTful开发控制器方法定义上方                 |
| 作用     | 设置当前控制器方法请求访问路径与请求动作，每种对应一个请求动作，<br/>例如@GetMapping对应GET请求 |
| 相关属性 | value（默认）：请求访问路径                                  |

## 6.0	SSM，启动!

​	具体整合过程写在笔记中就过于冗杂了。重在实践。

### 6.1	分包目录的参考

![1630561591931](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630561591931.png)

* config目录存放的是相关的配置类
* controller编写的是Controller类
* dao存放的是Dao接口，因为使用的是Mapper接口代理方式，所以没有实现类包
* service存的是Service接口，impl存放的是Service实现类
* resources:存入的是配置文件，如Jdbc.properties
* webapp:目录可以存放静态资源
* test/java:存放的是测试类

### 6.2	需要注意的点

​	**说明:**

​	bookDao在Service中注入的会提示一个红线提示，为什么呢?

* BookDao是一个接口，没有实现类，接口是不能创建对象的，所以最终注入的应该是代理对象
* 代理对象是由Spring的IOC容器来创建管理的
* IOC容器又是在Web服务器启动的时候才会创建
* IDEA在检测依赖关系的时候，没有找到适合的类注入，所以会提示错误提示
* 但是程序运行的时候，代理对象就会被创建，框架会使用DI进行注入，所以程序运行无影响。

### 6.3	统一结果的封装

​	这个我其实最终考核项目里已经搞过一个类似的了，具体思路就是:

* 为了封装返回的结果数据:==创建结果模型类，封装数据到data属性中==
* 为了封装返回的数据是何种操作及是否操作成功:==封装操作结果到code属性中==
* 操作失败后为了封装返回的错误信息:==封装特殊消息到message(msg)属性中==

​	设计一个统一数据返回结果类

```java
public class Result{
	private Object data;
	private Integer code;
	private String msg;
}
```

​	再设计一个返回码`Code`类(可以搞成**枚举类**)

```java
//状态码
public class Code {
    public static final Integer SAVE_OK = 20011;
    public static final Integer DELETE_OK = 20021;
    public static final Integer UPDATE_OK = 20031;
    public static final Integer GET_OK = 20041;

    public static final Integer SAVE_ERR = 20010;
    public static final Integer DELETE_ERR = 20020;
    public static final Integer UPDATE_ERR = 20030;
    public static final Integer GET_ERR = 20040;
}
// 都只是例子
```

​	最后再修改`Controller`类的返回值即可

### 6.4	统一异常处理

​	由于前端肯定看不懂我们后台服务器的异常，所以我们需要在后台进行统一的异常处理，将异常信息包装好再返回给前端。以后项目的处理方式图为:

![1630658821746](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630658821746.png)

#### 6.4.1	项目中异常可能的种类和分类

- 框架内部抛出的异常：因为后台==开发使用不合规==导致
- 数据层抛出的异常：因==外部服务器故障==导致（例如：服务器访问超时）
- 业务层抛出的异常：因==业务逻辑书写错误==导致（例如：遍历业务书写操作，导致索引异常等）
- 表现层抛出的异常：因==数据收集、校验等规则==导致（例如：不匹配的数据类型间导致异常）
- 工具类抛出的异常：因==工具类书写不严谨不够健壮==导致（例如：必要释放的连接长期未释放等）

​	==所有的异常均抛出到表现层进行处理==，在此之前我们需要将==异常分类==，最后通过AOP分类统一处理。

​	一种可行的异常分类方案为:

- **业务异常（BusinessException）**:规范的用户行为产生的异常;不规范的用户行为操作产生的异常
    - 发送对应消息传递给用户，提醒规范操作
        - 大家常见的就是提示用户名已存在或密码格式不正确等
- **系统异常（SystemException）**:项目运行过程中可预计但无法避免的异常,如数据库或服务器宕机
    - 发送固定消息传递给用户，安抚用户
        - 系统繁忙，请稍后再试
        - 系统正在维护升级，请稍后再试
        - 系统出问题，请联系系统管理员等
    - 发送特定消息给运维人员，提醒维护
        - 可以发送短信、邮箱或者是公司内部通信软件
    - 记录日志
        - 发消息和记录日志对用户来说是不可见的，属于后台程序
- **其他异常（Exception）**:编程人员未预期到的异常，如:用到的文件不存在
    - 发送固定消息传递给用户，安抚用户
    - 发送特定消息给编程人员，提醒维护（纳入预期范围内）
        - 一般是程序没有考虑全，比如未做非空校验等
    - 记录日志

#### 6.4.2	异常处理器

​	SpringMVC已经给我们提供了一个可以集中的、统一的处理项目中出现的异常的异常处理器

![1630657791653](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630657791653.png)

#### 6.4.3	异常处理器的使用-->@RestControllerAdvice和 @ExceptionHandler

##### 	第一步:创建异常处理器类

```java
//@RestControllerAdvice用于标识当前类为REST风格对应的异常处理器
@RestControllerAdvice
public class ProjectExceptionAdvice {
    //除了自定义的异常处理器，保留对Exception类型的异常处理，用于处理非预期的异常
    @ExceptionHandler(Exception.class)
    public void doException(Exception ex){
      	// ...
    }
}

```

注意:==需要确保SpringMvcConfig能够扫描到异常处理器类==

##### 	第二步:完善异常处理体系

- **设置自定义异常去包装可能出现的业务异常、系统异常、和其他异常。**

```java
public class SystemException extends RuntimeException{
    private Integer code;
    private String message;
    // 构造方法和getter,setter......
    }
```

​	**说明:**

1. 让自定义异常类继承`RuntimeException`的好处是，后期在抛出这两个异常的时候，就不用在try...catch...或throws了

2. 自定义异常类中添加`code`属性的原因是为了更好的区分异常是来自哪个业务的，同时也让Controller层有东西可以返回
3. 可以在code类中继续丰富项目中用到的code



- **将其他层可能抛出异常的地方用自定义异常包装**

​	具体的包装方式有：

1. 方式一:`try{}catch(){}`在catch中重新throw我们自定义异常即可。
2. 方式二:直接throw自定义异常即可



- **丰富异常处理器类中处理自定义异常的代码**

```java
//@RestControllerAdvice用于标识当前类为REST风格对应的异常处理器
@RestControllerAdvice
public class ProjectExceptionAdvice {
    //@ExceptionHandler用于设置当前处理器类对应的异常类型
    @ExceptionHandler(SystemException.class)
    public Result doSystemException(SystemException ex){
        //记录日志
        //发送消息给运维
        //发送邮件给开发人员,ex对象发送给开发人员
        return new Result(ex.getCode(),null,ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Result doBusinessException(BusinessException ex){
        return new Result(ex.getCode(),null,ex.getMessage());
    }

    //除了自定义的异常处理器，保留对Exception类型的异常处理，用于处理非预期的异常
    @ExceptionHandler(Exception.class)
    public Result doOtherException(Exception ex){
        //记录日志
        //发送消息给运维
        //发送邮件给开发人员,ex对象发送给开发人员
        return new Result(Code.SYSTEM_UNKNOW_ERR,null,"系统繁忙，请稍后再试！");
    }
}
```

##### 	知识点1：@RestControllerAdvice

| 名称 | @RestControllerAdvice              |
| ---- | ---------------------------------- |
| 类型 | ==类注解==                         |
| 位置 | Rest风格开发的控制器增强类定义上方 |
| 作用 | 为Rest风格开发的控制器类做增强     |

​	**说明:**此注解自带@ResponseBody注解与@Component注解，具备对应的功能

![1630659060451](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630659060451.png)

##### 	知识点2：@ExceptionHandler

| 名称 | @ExceptionHandler                                            |
| ---- | ------------------------------------------------------------ |
| 类型 | ==方法注解==                                                 |
| 位置 | 专用于异常处理的控制器方法上方                               |
| 作用 | 设置指定异常的处理方案，功能等同于控制器方法，<br/>出现异常后终止原始控制器执行,并转入当前方法执行 |

​	

## 7.0	前后端协议联调

### 7.1	让SpringMVC放行静态资源

​	添加了静态资源到webapp下后，SpringMVC会拦截，所有需要在SpringConfig的配置类中将静态资源进行放行。

* 新建`SpringMvcSupport`类

    ```java
    @Configuration
    public class SpringMvcSupport extends WebMvcConfigurationSupport {
        @Override
        protected void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/pages/**").addResourceLocations("/pages/");
            registry.addResourceHandler("/css/**").addResourceLocations("/css/");
            registry.addResourceHandler("/js/**").addResourceLocations("/js/");
            registry.addResourceHandler("/plugins/**").addResourceLocations("/plugins/");
        }
    }
    ```

* 在`SpringMvcConfig`中扫描`SpringMvcSupport`

    ```java
    @Configuration
    @ComponentScan({"com.itheima.controller","com.itheima.config"})
    @EnableWebMvc
    public class SpringMvcConfig {
    }
    ```



## 8.0	拦截器

​	为了辅助对拦截器这个概念的理解，可以看下面这张图

![1630676280170](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630676280170.png)

​	如果我们需要在每个Controller方法执行的前后添加业务，就可以拦截器来实现。

​	拦截器（Interceptor）是一种==动态拦截方法调用的机制==，在且仅在SpringMVC中执行

* 作用:
    * 在指定的方法调用前后执行预先设定的代码
    * 阻止原始方法的执行
    * 总结：拦截器就是用来做方法增强的

#### 8.0.1	拦截器和过滤器的区别

​	不难发现, 拦截器和过滤器在作用和执行顺序上很相似。它们的区别为:

- 归属不同：Filter属于Servlet技术，Interceptor属于SpringMVC技术
- 拦截内容不同：Filter对所有访问进行增强，Interceptor仅针对SpringMVC的访问进行增强

![1630676903190](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630676903190.png)

### 8.1	拦截器的基本使用

*不知道放在哪里就放在这了: @RequestHeader("Authorization") String token放在controller的参数中就可以直接获取token了*

​	拦截器的执行流程:

![1630679464294](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630679464294.png)

​	当有拦截器后，请求会先进入preHandle方法，

​	如果方法返回true，则放行继续执行后面的handle[controller的方法]和后面的方法

​	如果==返回**false**，则直接跳过后面方法的执行==。

##### 	步骤一:创建拦截器类-->`HandlerInterceptor`接口

​	让一个拦截器类实现`HandlerInterceptor`接口，重写接口中的三个方法。(Controller层)

​	*注:拦截器类要被SpringMVC容器扫描到。*

```java
@Component
//定义拦截器类，实现HandlerInterceptor接口
//注意当前类必须受Spring容器控制
public class ProjectInterceptor implements HandlerInterceptor {
    @Override
    //原始方法调用前执行的内容
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("preHandle...");
        return true;
    }

    @Override
    //原始方法调用后执行的内容
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle...");
    }

    @Override
    //原始方法调用完成后执行的内容
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion...");
    }
}
```

##### 	步骤二:在SpringMvcSupport这个配置类中配置拦截器类

​	3.7 基于BIO形式下的文件上传

### 目标

支持任意类型文件形式的上传。

### 客户端开发

```java
package com.itheima.file;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;

/**
    目标：实现客户端上传任意类型的文件数据给服务端保存起来。

 */
public class Client {
    public static void main(String[] args) {
        try(
                InputStream is = new FileInputStream("C:\\Users\\dlei\\Desktop\\BIO,NIO,AIO\\文件\\java.png");
        ){
            //  1、请求与服务端的Socket链接
            Socket socket = new Socket("127.0.0.1" , 8888);
            //  2、把字节输出流包装成一个数据输出流
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            //  3、先发送上传文件的后缀给服务端
            dos.writeUTF(".png");
            //  4、把文件数据发送给服务端进行接收
            byte[] buffer = new byte[1024];
            int len;
            while((len = is.read(buffer)) > 0 ){
                dos.write(buffer , 0 , len);
            }
            dos.flush();
            Thread.sleep(10000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

### 服务端开发

```java
package com.itheima.file;

import java.net.ServerSocket;
import java.net.Socket;

/**
    目标：服务端开发，可以实现接收客户端的任意类型文件，并保存到服务端磁盘。
 */
public class Server {
    public static void main(String[] args) {
        try{
            ServerSocket ss = new ServerSocket(8888);
            while (true){
                Socket socket = ss.accept();
                // 交给一个独立的线程来处理与这个客户端的文件通信需求。
                new ServerReaderThread(socket).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

```java
package com.itheima.file;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

public class ServerReaderThread extends Thread {
    private Socket socket;
    public ServerReaderThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try{
            // 1、得到一个数据输入流读取客户端发送过来的数据
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            // 2、读取客户端发送过来的文件类型
            String suffix = dis.readUTF();
            System.out.println("服务端已经成功接收到了文件类型：" + suffix);
            // 3、定义一个字节输出管道负责把客户端发来的文件数据写出去
            OutputStream os = new FileOutputStream("C:\\Users\\dlei\\Desktop\\BIO,NIO,AIO\\文件\\server\\"+
                    UUID.randomUUID().toString()+suffix);
            // 4、从数据输入流中读取文件数据，写出到字节输出流中去
            byte[] buffer = new byte[1024];
            int len;
            while((len = dis.read(buffer)) > 0){
                os.write(buffer,0, len);
            }
            os.close();
            System.out.println("服务端接收文件保存成功！");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

### 小结

## 3.7 基于BIO形式下的文件上传

### 目标

支持任意类型文件形式的上传。

### 客户端开发

```java
package com.itheima.file;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;

/**
    目标：实现客户端上传任意类型的文件数据给服务端保存起来。

 */
public class Client {
    public static void main(String[] args) {
        try(
                InputStream is = new FileInputStream("C:\\Users\\dlei\\Desktop\\BIO,NIO,AIO\\文件\\java.png");
        ){
            //  1、请求与服务端的Socket链接
            Socket socket = new Socket("127.0.0.1" , 8888);
            //  2、把字节输出流包装成一个数据输出流
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            //  3、先发送上传文件的后缀给服务端
            dos.writeUTF(".png");
            //  4、把文件数据发送给服务端进行接收
            byte[] buffer = new byte[1024];
            int len;
            while((len = is.read(buffer)) > 0 ){
                dos.write(buffer , 0 , len);
            }
            dos.flush();
            Thread.sleep(10000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

### 服务端开发

```java
package com.itheima.file;

import java.net.ServerSocket;
import java.net.Socket;

/**
    目标：服务端开发，可以实现接收客户端的任意类型文件，并保存到服务端磁盘。
 */
public class Server {
    public static void main(String[] args) {
        try{
            ServerSocket ss = new ServerSocket(8888);
            while (true){
                Socket socket = ss.accept();
                // 交给一个独立的线程来处理与这个客户端的文件通信需求。
                new ServerReaderThread(socket).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

```java
package com.itheima.file;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

public class ServerReaderThread extends Thread {
    private Socket socket;
    public ServerReaderThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try{
            // 1、得到一个数据输入流读取客户端发送过来的数据
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            // 2、读取客户端发送过来的文件类型
            String suffix = dis.readUTF();
            System.out.println("服务端已经成功接收到了文件类型：" + suffix);
            // 3、定义一个字节输出管道负责把客户端发来的文件数据写出去
            OutputStream os = new FileOutputStream("C:\\Users\\dlei\\Desktop\\BIO,NIO,AIO\\文件\\server\\"+
                    UUID.randomUUID().toString()+suffix);
            // 4、从数据输入流中读取文件数据，写出到字节输出流中去
            byte[] buffer = new byte[1024];
            int len;
            while((len = dis.read(buffer)) > 0){
                os.write(buffer,0, len);
            }
            os.close();
            System.out.println("服务端接收文件保存成功！");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

### 小结

​	重写`addInterceptors(InterceptorRegistry registry)`方法(格式和==配置放行静态资源==很像)

```java
@Configuration
public class SpringMvcSupport extends WebMvcConfigurationSupport {
    @Autowired
    private ProjectInterceptor projectInterceptor;
		// 配置静态资源
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/pages/**").addResourceLocations("/pages/");
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //配置拦截器
        registry.addInterceptor(projectInterceptor).addPathPatterns("/books","/books/*" );
    }
}
```

##### 	步骤三:让SpringMvcConfig类扫描到SpringMvcSupport

```java
@Configuration
@ComponentScan({"com.itheima.controller","com.itheima.config"})
@EnableWebMvc
public class SpringMvcConfig{
 
}
```

##### 	步骤四(可选):简化SpringMvcSupport的编写-->WebMvcConfigurer接口

​	让SpringMvcConfig这个配置类==实现WebMvcConfigurer接口==，将SpringMvcSupport包中的内容移到SpringMvcConfig这个配置类中即可。
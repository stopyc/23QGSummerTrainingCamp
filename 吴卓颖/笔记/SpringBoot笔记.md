# SpringBoot笔记

​	`SpringBoot` 是由Pivotal团队提供的全新框架，其设计目的是用来==简化==Spring应用的==初始搭建==以及==开发过程==。

​	原始 `Spring` 环境搭建和开发存在以下问题：

* 配置繁琐
* 依赖设置繁琐

`SpringBoot` 程序优点恰巧就是针对 `Spring` 的缺点

* 自动配置。这个是用来解决 `Spring` 程序配置繁琐的问题
* 起步依赖。这个是用来解决 `Spring` 程序依赖设置繁琐的问题
* 辅助功能（内置服务器,...）。我们在启动 `SpringBoot` 程序时既没有使用本地的 `tomcat` 也没有使用 `tomcat` 插件，而是使用 `SpringBoot` 内置的服务器。

## 1.0	SpringBoot, 启动！

我们使用 `Spring Initializr`  方式创建的 `Maven` 工程的的 `pom.xml` 配置文件中自动生成了很多包含 `starter` 的依赖，如下图

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210918220338109.png" alt="image-20210918220338109" style="zoom:70%;" />

-  `starter`就是起步依赖,定义了当前项目使用的所有项目坐标，以达到减少依赖配置的目的。

* ​	**`parent`**是所有 `SpringBoot` 项目要继承的项目，定义了若干个坐标版本号（依赖管理，而非依赖），以达到减少依赖冲突的目的

* `spring-boot-starter-parent`（2.5.0）与 `spring-boot-starter-parent`（2.4.6）共计57处坐标版本不同，所以实际开发时会开会讨论确定一个稳定共用的SpringBoot版本。

​	**实际开发**

* 使用任意坐标时，仅书写GAV中的G和A，V由SpringBoot提供

    > G：groupid
    >
    > A：artifactId
    >
    > V：version

* 如发生坐标错误，再指定version（要小心版本冲突）

### 1.1	程序启动	

​	创建的每一个 `SpringBoot` 程序时都包含一个类似于下面的类，我们将这个类称作引导类

```java
@SpringBootApplication
public class Springboot01QuickstartApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(Springboot01QuickstartApplication.class, args);
    }
}
```

==注意：==

* `SpringBoot` 在创建项目时，采用jar的打包方式

* `SpringBoot` 的引导类是项目的入口，运行 `main` 方法就可以启动项目

    因为我们在 `pom.xml` 中配置了 `spring-boot-starter-web` 依赖，而该依赖通过前面的学习知道它依赖 `tomcat` ，所以运行 `main` 方法就可以使用 `tomcat` 启动咱们的工程。

### 1.2	切换web服务器-->Maven的排除依赖

​	启动工程默认使用的是 `tomcat` 服务器，但我们也能不使用 `tomcat` 而使用 `jetty` 服务器。主需要将默认的 `tomcat` 服务器给排除掉，使用 `exclusion` 标签即可:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <groupId>org.springframework.boot</groupId>
        </exclusion>
    </exclusions>
</dependency>
```

​	再引入 `jetty` 服务器。在 `pom.xml` 中引入 `jetty` 的起步依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

## 2.0	配置文件

### 2.1	配置文件格式

​	我们是希望将端口号改为 `80`，这样在访问的时候就可以不写端口号了，如下

```
http://localhost/books/1
```

​	而 `SpringBoot` 程序如何修改呢？`SpringBoot` 提供了多种属性配置方式

* `application.properties`

    ```
    server.port=80
    ```

* `application.yml`格式，数据前面冒号后面必须有个==**空格**==

    ```yaml
    server:
    	port: 81
    ```

* `application.yaml`格式，数据前面冒号后面必须有个==**空格**==

    ```yaml
    server:
    	port: 82
    ```

> ==注意：`SpringBoot` 程序的配置文件名必须是 `application` ，只是后缀名不同而已。==
>
> 如果配置文件不出提示，可以在==Project Structure==里面自定义添加(标记)SpringBoot配置文件。

* 点击 `File` 选中 `Project Structure`

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210917163557071.png" alt="image-20210917163557071" style="zoom:80%;" />

* 弹出如下窗口，按图中标记红框进行选择

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210917163736458.png" alt="image-20210917163736458" style="zoom:70%;" />

* 通过上述操作，会弹出如下窗口

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210917163818051.png" alt="image-20210917163818051" style="zoom:80%;" />

* 点击上图的 `+` 号，弹出选择该模块的配置文件

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210917163828518.png" alt="image-20210917163828518" style="zoom:80%;" />

* 通过上述几步后，就可以看到如下界面。`properties` 类型的配合文件有一个，`ymal` 类型的配置文件有两个

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210917163846243.png" alt="image-20210917163846243" style="zoom:80%;" />

### 2.2	配置文件的优先级级

==`application.properties`  >  `application.yml`   >  `application.yaml`==

> ==注意：==
>
> * `SpringBoot` 核心配置文件名为 `application`
>
> * `SpringBoot` 内置属性过多，且所有属性集中在一起修改，在使用时，通过提示键+关键字修改属性
>
>     例如要设置日志的级别时，可以在配置文件中书写 `logging`，就会提示出来。配置内容如下
>
>     ```yaml
>     logging:
>       level:
>         root: info
>     ```

### 2.3	yaml格式

​	上面讲了三种不同类型的配置文件，而 `properties` 类型的配合文件之前我们学习过，接下来我们重点学习 `yaml` 类型的配置文件。

​	**YAML（YAML Ain't Markup Language），一种数据序列化格式。**这种格式的配置文件在近些年已经占有主导地位，那么这种配置文件和前期使用的配置文件是有一些优势的，我们先看之前使用的配置文件。

​	最开始我们使用的是 `xml` ，格式如下：

```xml
<enterprise>
    <name>itcast</name>
    <age>16</age>
    <tel>4006184000</tel>
</enterprise>
```

​	而 `properties` 类型的配置文件如下

```properties
enterprise.name=itcast
enterprise.age=16
enterprise.tel=4006184000
```

​	`yaml` 类型的配置文件内容如下

```yaml
enterprise:
	name: itcast
	age: 16
	tel: 4006184000
```

​	**优点：**

* 容易阅读

    `yaml` 类型的配置文件比 `xml` 类型的配置文件更容易阅读，结构更加清晰

* 容易与脚本语言交互

* 以数据为核心，重数据轻格式

    `yaml` 更注重数据，而 `xml` 更注重格式

​	**YAML 文件扩展名：**

* `.yml` (主流)
* `.yaml`

上面两种后缀名都可以，以后使用更多的还是 `yml` 的。

#### 2.31	语法规则

* 大小写敏感

* 属性层级关系使用多行描述，每行结尾使用冒号结束

* 使用缩进表示层级关系，同层级左侧对齐，只允许使用空格（不允许使用Tab键）

    空格的个数并不重要，只要保证同层级的左侧对齐即可。

* 属性值前面添加空格（属性名与属性值之间使用冒号+空格作为分隔）

* \# 表示注释

​	==核心规则：数据前面要加空格与冒号隔开==

​	数组数据在数据书写位置的下方使用减号作为数据开始符号，每行书写一个数据，减号与数据间空格分隔，例如

```yaml
enterprise:
  name: itcast
  age: 16
  tel: 4006184000
  subject:
    - Java
    - 前端
    - 大数据
```

### 

### 2.4	yaml配置文件数据读取

​	在 `com.itheima.controller` 包写创建名为 `BookController` 的控制器，内容如下

```java
@RestController
@RequestMapping("/books")
public class BookController {

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id){
        System.out.println("id ==> "+id);
        return "hello , spring boot!";
    }
}
```

在 `com.itheima.domain` 包下创建一个名为 `Enterprise` 的实体类等会用来封装数据，内容如下

```java
public class Enterprise {
    private String name;
    private int age;
    private String tel;
    private String[] subject;
    
    //setter and getter
    
    //toString
}
```

在 `resources` 下创建一个名为 `application.yml` 的配置文件，里面配置了不同的数据，内容如下

```yaml
lesson: SpringBoot

server:
  port: 80

enterprise:
  name: itcast
  age: 16
  tel: 4006184000
  subject:
    - Java
    - 前端
    - 大数据
```

#### 2.4.1	使用 @Value注解读取配置数据

​	使用 `@Value("表达式")` 注解可以从配合文件中读取数据，注解中用于读取属性名引用方式是：`${一级属性名.二级属性名……}`。我们可以在 `BookController` 中使用 `@Value`  注解读取配合文件数据，如下

```java
	 @Value("${lesson}")
    private String lesson;
    @Value("${server.port}")
    private Integer port;
    @Value("${enterprise.subject[0]}")
    private String subject_00;
```

#### 2.4.2	 **Environment对象**

​	上面方式读取到的数据特别零散，`SpringBoot` 还可以使用 `@Autowired` 注解注入 `Environment` 对象的方式读取数据。这种方式 `SpringBoot` 会将配置文件中所有的数据封装到 `Environment` 对象中，如果需要使用哪个数据只需要通过调用 `Environment` 对象的 `getProperty(String name)` 方法获取。(实际开发中此对象使用较少)具体代码如下：

```java
   @Autowired
   private Environment env;
   String lesson = env.getProperty("lesson")
```

#### 2.4.3	 自定义对象

​	`SpringBoot` 还提供了将配置文件中的数据封装到我们自定义的实体类对象中的方式。具体操作如下：

* 将实体类 `bean` 的创建交给 `Spring` 管理。

    在类上==添加 `@Component` 注解==

* **使用 `@ConfigurationProperties` 注解**表示==加载配置文件==

    在该注解中也可以使用 **`prefix` 属性**指定只==加载指定前缀的数据==

    具体到该例中，就是在pojo类`Enterprise`中添加如上注解

* 在 `BookController` 中进行==**注入**==

==注意：==

​	如果有Processor警告，在 `pom.xml` 中添加如下依赖即可

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```





### 2.5	配置文件分类

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210917194941597.png" alt="image-20210917194941597" style="zoom:70%;" />

有这样的场景，我们开发完毕后需要测试人员进行测试，由于测试环境和开发环境的很多配置都不相同，所以测试人员在运行我们的工程时需要临时修改很多配置，如下

```shell
java –jar springboot.jar –-spring.profiles.active=test --server.port=85 --server.servlet.context-path=/heima --server.tomcat.connection-timeout=-1 …… …… …… …… ……
```

针对这种情况，`SpringBoot` 定义了配置文件不同的放置的位置；而放在不同位置的优先级是不同的。

`SpringBoot` 中4级配置文件放置位置：(classpath即项目内部resources的目录,file即打包后jar包的目录)

* 1级：classpath：application.yml  
* 2级：classpath：config/application.yml
* 3级：file ：application.yml
* 4级：file ：config/application.yml 

> ==说明：==级别越高优先级越高







## 3.0	多环境配置

​	以后在工作中，对于开发环境、测试环境、生产环境的配置肯定都不相同，比如我们开发阶段会在自己的电脑上安装 `mysql` ，连接自己电脑上的 `mysql` 即可，但是项目开发完毕后要上线就需要该配置，将环境的配置改为线上环境的。

​	

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210917185253557.png" alt="image-20210917185253557" style="zoom:60%;" />

来回的修改配置会很麻烦，而 ==`SpringBoot` 给开发者提供了多环境的**快捷配置**==，需要切换环境时只需要改一个配置即可。不同类型的配置文件多环境开发的配置都不相同，接下来对不同类型的配置文件进行说明

​	注:maven的多环境配置==优先级==是比springboot==高==的,具体看[黑马SSM视频P100](https://www.bilibili.com/video/BV1Fi4y1S7ix?p=100&spm_id_from=pageDriver&vd_source=b579f791bf3f4eec7964e266fdf966fc)

### 3.1	 yaml文件

​	在 `application.yml` 中使用 `---` 来分割不同的配置，内容如下

```yaml
#开发
spring:
  profiles: dev #给开发环境起的名字
server:
  port: 80
---
#生产
spring:
  profiles: pro #给生产环境起的名字
server:
  port: 81
---
#测试
spring:
  profiles: test #给测试环境起的名字
server:
  port: 82
---
```

​	上面配置中 `spring.profiles` 是用来给不同的配置起名字的。可以在.yml文件前面==**加上如下配置**==来启用某一段配置

```yaml
#开发
spring:
  config:
    activate:
      on-profile: dev
```

### 3.2	 properties文件

​	`properties` 类型的配置文件配置多环境需要定义不同的配置文件

* `application-dev.properties` 是开发环境的配置文件。我们在该文件中配置端口号为 `80`

    ```properties
    server.port=80
    ```

* `application-test.properties` 是测试环境的配置文件。我们在该文件中配置端口号为 `81`

    ```properties
    server.port=81
    ```

* `application-pro.properties` 是生产环境的配置文件。我们在该文件中配置端口号为 `82`

    ```properties
    server.port=82
    ```

`SpringBoot` 只会默认加载名为 `application.properties` 的配置文件，所以需要在 `application.properties` 配置文件中设置启用哪个配置文件，配置如下:

```properties
spring.profiles.active=pro
```

### 3.3	命令行启动参数设置

​	使用 `SpringBoot` 开发的程序以后都是打成 `jar` 包，通过 `java -jar xxx.jar` 的方式启动服务的。那么就存在一个问题，如何切换环境呢？因为配置文件打到的jar包中了。

​	我们知道 `jar` 包其实就是一个压缩包，可以解压缩，然后修改配置，最后再打成jar包就可以了。这种方式显然有点麻烦，而 `SpringBoot` 提供了在运行 `jar` 时设置开启指定的环境的方式，如下

```shell
java –jar xxx.jar –-spring.profiles.active=test
```

​	那么这种方式能不能临时修改端口号呢？也是可以的，可以通过如下方式

```shell
java –jar xxx.jar –-server.port=88
```

​	当然也可以同时设置多个配置，比如即指定启用哪个环境配置，又临时指定端口，如下

```shell
java –jar springboot.jar –-server.port=88 –-spring.profiles.active=test
```

​	大家进行测试后就会发现==**命令行设置的端口号优先级高**==（也就是使用的是命令行设置的端口号），配置的优先级其实 `SpringBoot` 官网已经进行了说明，参见 :

```
https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config
```

​	进入上面网站后会看到如下页面

![image-20210917193910191](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210917193910191.png)

如果使用了多种方式配合同一个配置项，优先级高的生效。



### 4.0	SpringBoot整合junit

​	回顾 `Spring` 整合 `junit`

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
public class UserServiceTest {
    
    @Autowired
    private BookService bookService;
    
    @Test
    public void testSave(){
        bookService.save();
    }
}
```

​	使用 `@RunWith` 注解指定运行器，使用 `@ContextConfiguration` 注解来指定配置类或者配置文件。而 `SpringBoot` 整合 `junit` 特别简单，分为以下三步完成

* 在测试类上添加 `SpringBootTest` 注解
* 使用 `@Autowired` 注入要测试的资源
* 定义测试方法进行测试

==注意：==这里的引导类所在包必须是测试类所在包及其子包。

例如：

* 引导类所在包是 `com.itheima`
* 测试类所在包是 `com.itheima`

如果不满足这个要求的话，就需要在使用 `@SpringBootTest` 注解时，使用 `classes` 属性指定引导类的字节码对象。如 `@SpringBootTest(classes = Springboot07TestApplication.class)`



## 5.0	SpringBoot整合mybatis

### 5.1  回顾Spring整合Mybatis

`Spring` 整合 `Mybatis` 需要定义很多配置类

* `SpringConfig` 配置类

    * 导入 `JdbcConfig` 配置类

    * 导入 `MybatisConfig` 配置类

        ```java
        @Configuration
        @ComponentScan("com.itheima")
        @PropertySource("classpath:jdbc.properties")
        @Import({JdbcConfig.class,MyBatisConfig.class})
        public class SpringConfig {
        }
        
        ```

* `JdbcConfig` 配置类

    * 定义数据源（加载properties配置项：driver、url、username、password）

        ```java
        public class JdbcConfig {
            @Value("${jdbc.driver}")
            private String driver;
            @Value("${jdbc.url}")
            private String url;
            @Value("${jdbc.username}")
            private String userName;
            @Value("${jdbc.password}")
            private String password;
        
            @Bean
            public DataSource getDataSource(){
                DruidDataSource ds = new DruidDataSource();
                ds.setDriverClassName(driver);
                ds.setUrl(url);
                ds.setUsername(userName);
                ds.setPassword(password);
                return ds;
            }
        }
        ```

* `MybatisConfig` 配置类

    * 定义 `SqlSessionFactoryBean`

    * 定义映射配置

        ```java
        @Bean
        public MapperScannerConfigurer getMapperScannerConfigurer(){
            MapperScannerConfigurer msc = new MapperScannerConfigurer();
            msc.setBasePackage("com.itheima.dao");
            return msc;
        }
        
        @Bean
        public SqlSessionFactoryBean getSqlSessionFactoryBean(DataSource dataSource){
            SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
            ssfb.setTypeAliasesPackage("com.itheima.domain");
            ssfb.setDataSource(dataSource);
            return ssfb;
        }
        
        ```

### 5.2	定义实体类

在 `com.itheima.domain` 包下定义实体类 `Book`，内容如下

```java
public class Book {
    private Integer id;
    private String name;
    private String type;
    private String description;
    
    //setter and  getter
    
    //toString
}
```

### 5.3	定义dao接口

在 `com.itheima.dao` 包下定义 `BookDao` 接口，内容如下

```java
public interface BookDao {
    @Select("select * from tbl_book where id = #{id}")
    public Book getById(Integer id);
}
```

### 5.4	定义测试类

在 `test/java` 下定义包 `com.itheima` ，在该包下测试类，内容如下

```java
@SpringBootTest
class Springboot08MybatisApplicationTests {

	@Autowired
	private BookDao bookDao;

	@Test
	void testGetById() {
		Book book = bookDao.getById(1);
		System.out.println(book);
	}
}
```

### 5.5	编写配置

我们代码中并没有指定连接哪儿个数据库，用户名是什么，密码是什么。所以这部分需要在 `SpringBoot` 的配置文件中进行配合。

在 `application.yml` 配置文件中配置如下内容

```yml
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ssm_db
    username: root
    password: root
```

### 5.6	测试

运行测试方法，我们会看到如下错误信息

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210917221427930.png" alt="image-20210917221427930" style="zoom:70%;" />

错误信息显示在 `Spring` 容器中没有 `BookDao` 类型的 `bean`。为什么会出现这种情况呢？

原因是 `Mybatis` 会扫描接口并创建接口的代码对象交给 `Spring` 管理，但是现在并没有告诉 `Mybatis` 哪个是 `dao` 接口。而我们要解决这个问题需要在`BookDao` 接口上使用 `@Mapper` ，`BookDao` 接口改进为

```java
@Mapper
public interface BookDao {
    @Select("select * from tbl_book where id = #{id}")
    public Book getById(Integer id);
}
```

> ==注意：==
>
> `SpringBoot` 版本低于2.4.3(不含)，Mysql驱动版本大于8.0时，需要在url连接串中配置时区 `jdbc:mysql://localhost:3306/ssm_db?serverTimezone=UTC`，或在MySQL数据库端配置时区解决此问题

### 5.7	使用Druid数据源

现在我们并没有指定数据源，`SpringBoot` 有默认的数据源，我们也可以指定使用 `Druid` 数据源，按照以下步骤实现

* 导入 `Druid` 依赖

    ```xml
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
        <version>1.1.16</version>
    </dependency>
    ```

* 在 `application.yml` 配置文件配置

    可以通过 `spring.datasource.type` 来配置使用什么数据源。配置文件内容可以改进为

    ```yaml
    spring:
      datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/ssm_db?serverTimezone=UTC
        username: root
        password: root
        type: com.alibaba.druid.pool.DruidDataSource
    ```

## 6.0	案例

`SpringBoot` 到这就已经学习完毕，接下来我们将学习 `SSM` 时做的三大框架整合的案例用 `SpringBoot` 来实现一下。我们完成这个案例基本是将之前做的拷贝过来，修改成 `SpringBoot` 的即可，主要从以下几部分完成

1. pom.xml

    配置起步依赖，必要的资源坐标(druid)

2. application.yml

    设置数据源、端口等

3. 配置类

    全部删除

4. dao

    设置@Mapper

5. 测试类

6. 页面

    放置在resources目录下的static目录中

## 7.0	补充要点

### 7.1	SpringBoot的Servelt中拿request和response

​	在形参里写上就好。
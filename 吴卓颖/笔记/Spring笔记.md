# Spring笔记

## 0.0	有关Spring本身及其思想

​	Spring是一个非常流行和强大的Java开发框架，它可以帮助程序员创建高效、可扩展、可维护的企业级应用程序。

​	Spring的核心特性是**依赖注入（Dependency Injection）[^1]**和**面向切面编程（Aspect-Oriented Programming）[^2]**。Spring还提供了对各种JavaEE技术和开源框架的集成支持，例如Servlet, JSP, EJB, JPA, Hibernate, MyBatis, Struts等。Spring还有很多子项目，例如Spring Boot, Spring Cloud, Spring MVC, Spring Security等。

### 0.1	Spring核心架构

* Spring Framework是Spring生态圈中最基础的项目，是其他项目的根基。

* Spring Framework的发展也经历了很多版本的变更，每个版本都有相应的调整

    ![image-20210729172153796](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729172153796.png)

* Spring Framework的5版本目前没有最新的架构图，而最新的是4版本，所以接下来主要研究的是4的架构图

    ![1629720945720](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1629720945720.png)

    (1)核心层

    * Core Container:核心容器，这个模块是Spring最核心的模块，其他的都需要依赖该模块

    (2)AOP层

    * AOP:面向切面编程，它依赖核心层容器，目的是==在不改变原有代码的前提下对其进行功能增强==
    * Aspects:AOP是思想,**Aspects**是对AOP思想的具体实现

    (3)数据层

    * Data Access:数据访问，Spring全家桶中有对数据访问的具体实现技术
    * Data Integration:数据集成，Spring支持整合其他的数据层解决方案，比如Mybatis
    * Transactions:事务，Spring中事务管理是Spring AOP的一个具体实现，也是后期学习的重点内容

    (4)Web层

    * 这一层的内容将在SpringMVC框架具体学习

    (5)Test层

    * Spring主要整合了Junit来完成单元测试和集成测试

### 0.2	IoC(Inversion of Control)控制反转思想

​	思想： Inversion of Control ，翻译为 控制反转 ，强调的是原来在程序中创建 Bean 的权利反转给第三方。例如:在普通MVC三层架构中业务层经常需要调用数据层的方法，就需要在业务层new数据层的对象，如果数据层的实现类发生变化，那么业务层的代码也需要跟着改变，发生变更后，都需要进行编译打包和重部署。这种代码的问题就是:==耦合度偏高==。

​	针对这个问题，Spring就提出了一个解决方案:

* 使用对象时，在程序中不要主动使用new产生对象，转换为由==外部==提供对象。

​	控制反转的==核心==是：**将对象的创建权交出去，将对象和对象之间关系的管理权交出去，由第三方容器来负责创建与维护**。

​	例如：原来在程序中手动的去`new UserServiceImpl ()`而根据 IoC 思想的指导，不手动去`new`，而去寻求一个第三方去创建` UserServiceImpl `对象和` UserDaoImpl` 对象。这样程序与具体对象就失去的直接联系。

​	控制反转常见的实现方式：==依赖注入==（Dependency Injection，简称DI）

通常，依赖注入的实现由包括两种方式：

- set方法注入

- 构造方法注入

​	而Spring框架就是一个实现了IoC思想的框架。

#### 0.2.2	谁去充当这个外部呢？

​	答:IOC容器

​	Spring和IOC之间的关系是什么呢?

* Spring技术对IOC思想进行了实现
* Spring提供了一个容器，称为==IOC容器==，用来充当IOC思想中的"外部"
* IOC思想中的`别人[外部]`指的就是Spring的IOC容器

​	IOC容器的作用以及内部存放的是什么?

* IOC容器==负责对象的创建、初始化等一系列工作==，其中包含了数据层和业务层的类对象
* 被创建或被管理的对象在IOC容器中统称为==Bean==
* IOC容器中放的就是一个个的Bean对象



#### 0.2.3	在容器中建立对象与对象之间的绑定关系就要用到DI

![1629735078619](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1629735078619.png)

​	什么是依赖注入呢?

* 在容器中建立==bean与bean之间的依赖关系==的整个过程，称为==依赖注入==
    * 业务层要用数据层的类对象，以前是自己`new`的
    * 现在自己不new了，靠`别人[外部其实指的就是IOC容器]`来给注入进来
    * 这种思想就是依赖注入

​	IOC容器中哪些bean之间要建立依赖关系呢?

* 这个需要程序员根据业务需求提前建立好关系，如==业务层需要依赖数据层，service就要和dao建立依赖关系==

介绍完Spring的IOC和DI的概念后，我们会发现这两个概念的最终目标就是:==充分解耦==，具体实现靠:

* 使用IOC容器管理bean（IOC)
* 在IOC容器内将有依赖关系的bean进行关系绑定（DI）
* 最终结果为:使用对象时==不仅可以直接从IOC容器中获取，并且获取到的bean已经绑定了所有的依赖关系.==

​	

## 1.0	Spring, 启动!

### 1.1	IOC容器初使用

> 需求分析:将BookServiceImpl和BookDaoImpl交给Spring管理，并从容器中获取对应的bean对象进行方法调用。
>
> 1.创建Maven的java项目
>
> 2.pom.xml添加Spring的依赖jar包
>
> 3.创建需要使用的类
>
> 4.resources下添加spring配置文件，并完成bean的配置
>
> 5.使用Spring提供的接口完成IOC容器的创建
>
> 6.从容器中获取对象进行方法调用



### 1.2	依赖注入初使用

> ​	需求:基于IOC入门案例，在BookServiceImpl类中删除new对象的方式，使用Spring的DI完成Dao层的注入
>
> 1.删除业务层中使用new的方式创建的dao对象
>
> 2.在业务层提供BookDao的setter方法
>
> 3.在配置文件中添加依赖注入的配置
>
> 4.运行程序调用方法

```java
public class BookServiceImpl implements BookService {
    //删除业务层中使用new的方式创建的dao对象
    private BookDao bookDao;

	//...
    
    //提供对应的set方法
    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }
}

```

​	配置文件:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <!--bean标签标示配置bean
    	id属性标示给bean起名字
    	class属性表示给bean定义类型
	-->
    <bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl"/>

    <bean id="bookService" class="com.itheima.service.impl.BookServiceImpl">
        <!--配置server与dao的关系-->
        <!--property标签表示配置当前bean的属性
        		name属性表示配置哪一个具体的属性(变量名)
        		ref属性表示参照哪一个bean(xml中id)
		-->
        <property name="bookDao" ref="bookDao"/>
    </bean>

</beans>
```

​	==注意:配置中的两个bookDao的含义是不一样的==

* name="bookDao"中`bookDao`的作用是让Spring的IOC容器在获取到名称后，将首字母大写，前面加set找对应的`setBookDao()`方法进行对象注入
* ref="bookDao"中`bookDao`的作用是让Spring能在IOC容器中找到id为`bookDao`的Bean对象给`bookService`进行注入
* 综上所述，对应关系如下:

![1629736314989](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1629736314989.png)

## 2.0	IOC与bean

### 2.1	bean基础配置

​	需要指出的是:JavaBean是一种符合规范的Java类用于封装数据，而Spring的Bean是Spring容器中由Spring框架创建和管理的组件对象。尽管它们具有一些相似之处，但在==概念上它们是不同的。==

#### 2.1.1	class与id

​	对于bean的基础配置，在前面的案例中已经使用过:

```
<bean id="" class=""/>
```

其中，bean标签的功能、使用方式以及id和class属性的作用，我们通过一张图来描述下

![image-20210729183500978](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729183500978.png)

​	需要注意的是:class属性不能写接口，因为接口是没办法创建对象的。

#### 2.2.2	name

​	别名name的配置说明:

![image-20210729183558051](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729183558051.png)

​	需要注意的是bean依赖注入的ref属性指定bean，必须在容器中存在。但是可以用`id`指定也可以用`name`指定。

![1629771744003](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1629771744003.png)

#### 2.2.3	scope

![image-20210729183628138](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729183628138.png)

​	`scope`标签可以设置bean为单例或非单例模式，单例模式下通过IoC容器产生的bean对象均为同一个。bean对象默认只有一个就避免了对象的频繁创建与销毁，达到了bean对象的==复用，性能高==。

* bean在容器中是单例的，会不会产生==线程安全问题==?
    * 如果对象是有状态对象，即该对象**有成员变量可以用来存储数据**的，
        * 因为所有请求线程共用一个bean对象，所以会存在线程安全问题。
    * 如果对象是无状态对象，即该对象**没有成员变量**没有进行数据存储的，
        * 因方法中的局部变量在方法调用完成后会被销毁，所以不会存在线程安全问题。
* 哪些bean对象适合交给容器进行管理?
    * **表现层对象**
    * **业务层对象**
    * **数据层对象**
    * **工具对象**
* 哪些bean对象不适合交给容器进行管理?
    * 封装实例的域对象(Javabean)，因为会引发线程安全问题，所以不适合。

### 2.3	Spring异常信息的阅读

​	![springexception](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/springexception.png)

* 错误信息==从下往上依次查看，因为上面的错误大都是对下面错误的一个包装，最核心错误是在最下面==
* Caused by: java.lang.NoSuchMethodException: com.itheima.dao.impl.BookDaoImpl.`<init>`()
    * Caused by 翻译为`引起`，即出现错误的原因
    * java.lang.NoSuchMethodException:抛出的异常为`没有这样的方法异常`
    * com.itheima.dao.impl.BookDaoImpl.`<init>`():哪个类的哪个方法没有被找到导致的异常，`<init>`()指定是类的构造方法，即该类的无参构造方法

​	如果最后一行错误获取不到错误信息或者看不懂，就去查看第二层。  

### 2.4	bean的实例化

​	Ioc容器实例化bean的有三种方式，`构造方法`,`静态工厂`和`实例工厂`。

#### 2.4.1	构造方法实例化

​	Spring底层使用的是类的**无参构造方法**。且无论构造方法是private还是public均能调用（因为底层是用反射实现的）

#### 2.4.2	静态工厂实例化

​	工厂类:

```java
//静态工厂创建对象
public class OrderDaoFactory {
    public static OrderDao getOrderDao(){
        return new OrderDaoImpl();
    }
}
```

​	在spring的配置文件application.properties中添加以下内容:

```xml
<bean id="orderDao" class="com.itheima.factory.OrderDaoFactory" factory-method="getOrderDao"/>
```

- class:工厂类的类全名

- factory-mehod:具体工厂类中创建对象的方法名

​	对应关系如下图:

![image-20210729195248948](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729195248948.png)

#### 2.4.3	实例工厂实例化

```xml
<bean id="userFactory" class="com.itheima.factory.UserDaoFactory"/>
<bean id="userDao" factory-method="getUserDao" factory-bean="userFactory"/>
```

 	实例化工厂运行的顺序是:

* 创建实例化工厂对象,对应的是第一行配置

* 调用对象中的方法来创建bean，对应的是第二行配置

    * factory-bean:工厂的实例对象

    * factory-method:工厂对象中的具体创建对象的方法名

    * 对应关系如下:

        ![image-20210729200203249](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729200203249.png)

#### 2.4.4	FactoryBean的使用

​	具体的使用步骤为:

​		(1)创建一个UserDaoFactoryBean的类，实现FactoryBean接口，重写接口的方法`getObject`和`getObjectType`

```java
public class UserDaoFactoryBean implements FactoryBean<UserDao> {
    //代替原始实例工厂中创建对象的方法
    public UserDao getObject() throws Exception {
        return new UserDaoImpl();
    }
    //返回所创建类的Class对象
    public Class<?> getObjectType() {
        return UserDao.class;
    }
}
```

​		(2)在Spring的配置文件中进行配置

```xml
<bean id="userDao" class="com.itheima.factory.UserDaoFactoryBean"/>
```

​		(3)AppForInstanceUser运行类不用做任何修改，直接运行

##### 2.4.4.1		implements FactoryBean需要重写的三个方法

​	查看源码会发现，FactoryBean接口其实会有三个方法，分别是:

```java
T getObject() throws Exception;

Class<?> getObjectType();

default boolean isSingleton() {
		return true;
}
```

- 方法一:getObject()，被重写后，在方法中进行对象的创建并返回

- 方法二:getObjectType(),被重写后，主要返回的是被创建类的Class对象

- 方法三:没有被重写，因为它已经给了默认值，从方法名中可以看出其作用是设置对象是否为单例，默认true.
    - 如果想改成非单例模式，另外一个可行的方法是:

```java
//FactoryBean创建对象
public class UserDaoFactoryBean implements FactoryBean<UserDao> {
    //代替原始实例工厂中创建对象的方法
    public UserDao getObject() throws Exception {
        return new UserDaoImpl();
    }

    public Class<?> getObjectType() {
        return UserDao.class;
    }
/* ********************************** */
    public boolean isSingleton() {
        return false;
    }
/* ********************************** */
}
```



### 2.5	bean的生命周期

​	bean生命周期就是bean对象从创建到销毁的整体过程。我们可以一定程度上对bean的生命周期进行控制，例如在bean==创建后和销毁前==做一些事情。具体的控制有两个阶段:

* bean创建之后，想要添加内容，比如用来初始化需要用到资源
* bean销毁之前，想要添加内容，比如用来释放用到的资源

#### 2.5.1	第一种方式

##### 	步骤1:添加初始化和销毁方法

针对这两个阶段，我们在BooDaoImpl类中分别添加两个方法，==方法名任意==

```java
public class BookDaoImpl implements BookDao {
    public void save() {
        System.out.println("book dao save ...");
    }
    //表示bean初始化对应的操作
    public void init(){
        System.out.println("init...");
    }
    //表示bean销毁前对应的操作
    public void destory(){
        System.out.println("destory...");
    }
}
```

##### 	步骤2:配置生命周期

在配置文件添加配置，如下:

```xml
<bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl" init-method="init" destroy-method="destory"/>
```

##### 	步骤3:运行程序: ....

#### 2.5.2	第二种方式

​	Spring提供了两个接口来完成生命周期的控制，它们分别是:

- `InitializingBean`
- `DisposableBean`

​	好处是可以不用再进行配置`init-method`和`destroy-method`。

​	具体操作:

​	修改BookServiceImpl类，添加两个接口`InitializingBean`， `DisposableBean`并实现接口中的两个方法`afterPropertiesSet`和`destroy`即可



#### 2.5.3	关闭容器

​	如果我们只做上述设置，从运行结果中可以看出，`init`方法执行了，但是`destroy`方法却未执行，原因在于:

1. Spring的IOC容器是运行在JVM中
2. 运行main方法后,JVM启动,Spring加载配置文件生成IOC容器,从容器获取bean对象，然后调方法执行
3. main方法执行完后，JVM退出，这个时候IOC容器中的bean还没有来得及销毁就已经结束了
4. 所以没有调用对应的destroy方法

​	所以如果需要关闭容器，有如下两种方法:

1. **close()方法关闭容器**

* 但由于ApplicationContext中没有close方法，所以我们需要将ApplicationContext更换成ClassPathXmlApplicationContext(此为ApplicationContext的子类)

    ```java
    ClassPathXmlApplicationContext ctx = new 
        ClassPathXmlApplicationContext("applicationContext.xml");
    ```

* 调用ctx的close()方法

    ```
    ctx.close();
    ```

* 运行程序，就能执行destroy方法的内容

2. **注册钩子关闭容器**

* 在容器未关闭之前，提前设置好回调函数，让JVM在退出之前回调此函数来关闭容器

* 调用ctx的registerShutdownHook()方法

    ```
    ctx.registerShutdownHook();
    ```

    **注意:**registerShutdownHook在ApplicationContext中也没有

* 运行后，查询打印结果



## 3.0	DI相关内容

​	Spring为我们提供了两种依赖注入方式，分别是:

* setter注入
    * 简单类型
    * ==引用类型==
* 构造器注入
    * 简单类型
    * ==引用类型==

### 3.1	setter注入&lt;property>

#### 3.1.1	引用类型

* bean中定义引用类型属性，并提供可访问的==set==方法

```java
public class BookServiceImpl implements BookService {
    private BookDao bookDao;
    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }
}
```

* 配置中使用==property==标签==ref==属性注入引用类型对象

```xml
<bean id="bookService" class="com.itheima.service.impl.BookServiceImpl">
	<property name="bookDao" ref="bookDao"/>
</bean>

<bean id="bookDao" class="com.itheima.dao.imipl.BookDaoImpl"/>
```

#### 3.1.2	基本数据类型

​	基本数据类型需要使用`value`标签==去直接设置变量的值==

##### 	步骤1:声明属性并提供setter方法

​	在BookDaoImpl类中声明对应的简单数据类型的属性,并提供对应的setter方法

```java
public class BookDaoImpl implements BookDao {

    private String databaseName;
    private int connectionNum;

    public void setConnectionNum(int connectionNum) {
        this.connectionNum = connectionNum;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void save() {
        System.out.println("book dao save ..."+databaseName+","+connectionNum);
    }
}
```

##### 	步骤2:配置文件中进行注入配置

​	在applicationContext.xml配置文件中使用property标签注入

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl">
        <property name="databaseName" value="mysql"/>
     	<property name="connectionNum" value="10"/>
    </bean>
    <bean id="userDao" class="com.itheima.dao.impl.UserDaoImpl"/>
    <bean id="bookService" class="com.itheima.service.impl.BookServiceImpl">
        <property name="bookDao" ref="bookDao"/>
        <property name="userDao" ref="userDao"/>
    </bean>
</beans>
```

​	**说明:**

​	value:后面跟的是简单数据类型，对于参数类型，Spring在注入的时候会自动转换，但是不能写成

```xml
<property name="connectionNum" value="abc"/>
```

​	这样的话，spring在将`abc`转换成int类型的时候就会报错。



### 3.2	构造器注入&lt;constructor-arg>

#### 3.2.1	第一种方案(紧耦合)

​	多个参数也是如此，且顺序可以随意。

##### 	步骤1:删除setter方法并提供构造方法

​	在BookServiceImpl类中将bookDao的setter方法删除掉,并添加==带有bookDao参数的构造方法==

```java
public class BookServiceImpl implements BookService{
    private BookDao bookDao;

    public BookServiceImpl(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    public void save() {
        System.out.println("book service save ...");
        bookDao.save();
    }
}
```

##### 	步骤2:配置文件中进行配置构造方式注入

​	在applicationContext.xml中配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl"/>
    <bean id="bookService" class="com.itheima.service.impl.BookServiceImpl">
        <constructor-arg name="bookDao" ref="bookDao"/>
    </bean>
</beans>
```

​	**说明:**

​	在标签&lt;constructor-arg>中

* `name`属性对应的值为==构造函数中方法**形参的参数名**，必须要保持一致==。

* `ref`属性指向的是spring的IOC容器中==其他bean对象==。

#### 3.2.2	第二种方案(解决形参名的问题)

​	方式一:删除name属性，添加type属性，按照类型注入

```xml
<bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl">
    <constructor-arg type="int" value="10"/>
    <constructor-arg type="java.lang.String" value="mysql"/>
</bean>
```

* 这种方式可以解决构造函数形参名发生变化带来的耦合问题
* 但是如果构造方法参数中有类型相同的参数，这种方式就不太好实现了

​	方式二:删除type属性，添加index属性，按照索引下标注入，下标从0开始

```xml
<bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl">
    <constructor-arg index="1" value="100"/>
    <constructor-arg index="0" value="mysql"/>
</bean>
```

* 这种方式可以解决参数类型重复问题
* 但是如果构造方法参数顺序发生变化后，这种方式又带来了耦合问题

### 3.3	两种参数依赖注入方式的选择

1. 强制依赖使用构造器进行，使用setter注入有概率不进行注入导致null对象出现。
    * 强制依赖指==对象在创建的过程中必须要注入指定的参数==
2. 可选依赖使用setter注入进行，灵活性强
    * 可选依赖指对象在创建过程中注入的参数可有可无
3. Spring框架倡导使用构造器，第三方框架内部大多数采用构造器注入的形式进行数据初始化，相对严谨
4. 如果有必要可以两者同时使用，使用构造器注入完成强制依赖的注入，使用setter注入完成可选依赖的注入
5. 实际开发过程中还要根据实际情况分析，如果受控对象没有提供setter方法就必须使用构造器注入
6. **==自己开发的模块推荐使用setter注入==**

### 3.4	自动配置

​	自动装配是指IoC容器根据bean所依赖的资源在容器中==自动查找并注入==到bean中的过程

#### 3.41	如何自动装配

​	自动装配只需要修改applicationContext.xml配置文件即可:

​	(1)将`<property>`标签删除

​	(2)在`<bean>`标签中添加autowire属性

首先来实现按照类型注入的配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <<bean class="com.itheima.dao.impl.BookDaoImpl"/>两个相同的类注入会报错 
    <!--autowire属性：开启自动装配，通常使用按类型装配-->
    <bean id="bookService" class="com.itheima.service.impl.BookServiceImpl" autowire="byType"/>

</beans>
```

==注意事项:==

* 需要注入属性的类中对应属性的setter方法不能省略
* 被注入的对象必须要被Spring的IOC容器管理
* 按照类型在Spring的IOC容器中如果找到多个对象，会报`NoUniqueBeanDefinitionException`

​	一个类型在IOC中有多个对象，还想要注入成功，这个时候就需要==按照名称注入==，`autowire="byType"`变成`autowire="byName"`。配置方式为:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean class="com.itheima.dao.impl.BookDaoImpl"/>
    <!--autowire属性：开启自动装配，通常使用按类型装配-->
    <bean id="bookService" class="com.itheima.service.impl.BookServiceImpl" autowire="byName"/>

</beans>
```

==注意事项:==

* 按照名称注入中的名称指的是什么?

    ![1629806856156](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1629806856156.png)

    * bookDao是private修饰的，外部类无法直接方法
    * 外部类只能通过属性的set方法进行访问
    * 对外部类来说，setBookDao方法名，去掉set后首字母小写是其属性名
        * 为什么是去掉set首字母小写?
        * 这个规则是set方法生成的默认规则，set方法的生成是把属性名首字母大写前面加set形成的方法名
    * 所以按照名称注入，其实是和对应的set方法有关，但是如果按照标准起名称，属性名和set对应的名是一致的

* 如果按照名称去找对应的bean对象，找不到则注入Null

* 当某一个类型在IOC容器中有多个对象，按照名称注入只找其指定名称对应的bean对象，不会报错 

​	两种方式介绍完后，以后用的更多的是==按照类型==注入。

​	最后对于依赖注入，需要注意一些其他的配置特征:

1. **自动装配**用于==引用类型依赖注入==，不能对简单类型进行操作
2. 使用**按类型装配时**（byType）必须保障容器中相同类型的bean唯一，推荐使用
3. 使用**按名称装配时**（byName）必须保障容器中具有指定名称的bean，因变量名与配置耦合，不推荐使用
4. 自动装配优先级低于setter注入与构造器注入，同时出现时自动装配配置失效

### 3.5	集合注入

​	我们已经能完成引入数据类型和简单数据类型的注入，但是还有一种数据类型==集合==，集合中既可以装简单数据类型也可以装引用数据类型，对于集合，在Spring中该如何注入呢?

​	区别主要在于xml文件的更改:

​	下面的所以配置方式，都是在bookDao的bean标签中使用<property>进行注入

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl">
        
    </bean>
</beans>
```

#### 3.5.1	注入数组类型数据

```xml
<property name="array">
    <array>
        <value>100</value>
        <value>200</value>
        <value>300</value>
    </array>
</property>
```

#### 3.5.2	注入List类型数据

```xml
<property name="list">
    <list>
        <value>itcast</value>
        <value>itheima</value>
        <value>boxuegu</value>
        <value>chuanzhihui</value>
    </list>
</property>
```

#### 3.5.3	注入Set类型数据

```xml
<property name="set">
    <set>
        <value>itcast</value>
        <value>itheima</value>
        <value>boxuegu</value>
        <value>boxuegu</value>
    </set>
</property>
```

#### 3.5.4	注入Map类型数据

```xml
<property name="map">
    <map>
        <entry key="country" value="china"/>
        <entry key="province" value="henan"/>
        <entry key="city" value="kaifeng"/>
    </map>
</property>
```

#### 3.5.5	注入Properties类型数据

```xml
<property name="properties">
    <props>
        <prop key="country">china</prop>
        <prop key="province">henan</prop>
        <prop key="city">kaifeng</prop>
    </props>
</property>
```

​	配置完成后，运行下看结果:

![1629808046783](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1629808046783.png)

​	**说明：**

* property标签表示setter方式注入，构造方式注入`constructor-arg`标签内部也可以写`<array>`、`<list>`、`<set>`、`<map>`、`<props>`标签
* List的底层也是通过数组实现的，所以`<list>`和`<array>`标签是可以混用
* 集合中要添加引用类型，只需要把`<value>`标签改成`<ref>`标签，这种方式用的比较少

### 3.6	spring读取properties文件

![springproperties1](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/springproperties1.png)

![Springproperties2](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/Springproperties2.png)



## 4.0	IOC/DI注解开发

##### 	步骤1:删除原XML配置

​	将配置文件中的`<bean>`标签删除掉

```xml
<bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl"/>
```

##### 	步骤2:Dao上添加注解

​	在`BookDaoImpl`类上添加`@Component`注解

```java
@Component("bookDao")
public class BookDaoImpl implements BookDao {
    public void save() {
        System.out.println("book dao save ..." );
    }
}
```

​	==注意:@Component注解不可以添加在接口上，因为接口是无法创建对象的。==

​	XML与注解配置的**对应关系**:

![1629990315619](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1629990315619.png)

##### 	步骤3:配置Spring的注解包扫描

​	为了让Spring框架能够扫描到写在类上的注解，需要在配置文件上进行包扫描

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <context:component-scan base-package="com.itheima"/>
</beans>
```

​	**说明:**

​	**&lt;component-scan>**

* component:组件,Spring将管理的bean视作自己的一个组件
* scan:扫描

​	**base-package**指定Spring框架扫描的包路径，它会扫描指定包及其子包中的所有类上的注解。

* 包路径越多[如:com.itheima.dao.impl]，扫描的范围越小速度越快
* 包路径越少[如:com.itheima],扫描的范围越大速度越慢
* 一般扫描到项目的组织名称即Maven的groupId下[如:com.itheima]即可。

##### 	步骤4:Service上添加注解

​	在BookServiceImpl类上也添加`@Component`交给Spring框架管理

```java
@Component
public class BookServiceImpl implements BookService {
    private BookDao bookDao;

    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    public void save() {
        System.out.println("book service save ...");
        bookDao.save();
    }
}
```

​	**说明:**

* BookServiceImpl类没有起名称，所以在App中是按照类型来获取bean对象

* @Component注解如果不起名称，会有一个默认值就是`当前类名首字母小写`，所以也可以按照名称获取，如

    ```java
    BookService bookService = (BookService)ctx.getBean("bookServiceImpl");
    System.out.println(bookService);
    ```

​	对于@Component注解，还衍生出了其他三个注解`@Controller`、`@Service`、`@Repository`

​	通过查看源码会发现这三个注解和`@Component`注解的作用是一样的

![1630028345074](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630028345074.png)

​	这是为了方便我们后期在编写类的时候能很好的**区分出这个类是属于`表现层`、`业务层`还是`数据层`的类。**

### 4.1	纯注解开发模式

​	实现思路为:  将配置文件applicationContext.xml删除掉，使用==类==来替换。

#### 4.1.1	开发

##### 	步骤1:创建配置类

​	创建一个配置类`SpringConfig`

```java
public class SpringConfig {
}
```

##### 	步骤2:标识该类为配置类

​	在配置类上添加`@Configuration`注解，将其标识为一个配置类,替换`applicationContext.xml`

```java
@Configuration
public class SpringConfig {
}
```

##### 步骤3:用注解替换包扫描配置

在配置类上添加包扫描注解`@ComponentScan`替换`<context:component-scan base-package=""/>`

```java
@Configuration
@ComponentScan("com.itheima")
public class SpringConfig {
}
```

##### 步骤4:创建运行类并执行

创建一个新的运行类`AppForAnnotation`

```java
public class AppForAnnotation {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        BookDao bookDao = (BookDao) ctx.getBean("bookDao");
        System.out.println(bookDao);
        BookService bookService = ctx.getBean(BookService.class);
        System.out.println(bookService);
    }
}
```

​	运行AppForAnnotation,可以看到两个对象依然被获取成功。

### 4.2	总结:

* Java类替换Spring核心配置文件

    ![1630029254372](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630029254372.png)

* `@Configuration`注解用于设定当前类为配置类

* `@ComponentScan`注解用于设定扫描路径，此注解==只能添加一次，多个数据请用数组格式==

    ```
    @ComponentScan({com.itheima.service","com.itheima.dao"})
    ```

* 从读取Spring核心配置文件初始化容器对象，切换为读取Java配置类初始化容器对象

    ```java
    //加载配置文件初始化容器
    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    //加载配置类初始化容器
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
    ```

#### 知识点1：@Configuration()

| 名称 | @Configuration                        |
| ---- | ------------------------------------- |
| 类型 | 类注解                                |
| 位置 | 类定义上方                            |
| 作用 | 设置该类为spring配置类                |
| 属性 | value（默认）：定义bean的id，有默认值 |

#### 知识点2：@ComponentScan()

| 名称 | @ComponentScan                                           |
| ---- | -------------------------------------------------------- |
| 类型 | 类注解                                                   |
| 位置 | 类定义上方                                               |
| 作用 | 设置spring配置类扫描路径，用于加载使用注解格式定义的bean |
| 属性 | value（默认）：扫描路径，此路径可以逐层向下扫描          |

**小结:**

这一节重点掌握的是使用注解完成Spring的bean管理，需要掌握的内容为:

* applicationContext.xml中`<context:component-san/>`的作用是指定扫描包路径，对应注解为==@ComponentScan==
* ==@Configuration==声明该类为配置类，使用类替换applicationContext.xml文件
* ↑以上为针对配置类使用的注解↑
* ↓以下为针对bean类使用的注解↓
* 记住==@Component、@Controller、@Service、@Repository==这四个注解
* ClassPathXmlApplicationContext是==加载XML配置文件的对象==
* AnnotationConfigApplicationContext是==加载配置类的对象==

### 4.3	注解开发bean作用范围与生命周期管理

#### 4.3.2	Bean的作用范围-->`@scope`注解

​	(1)先运行App类,在控制台打印两个一摸一样的地址，说明默认情况下bean是单例

![1630031192753](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630031192753.png)

​	(2)要想将BookDaoImpl变成非单例，只需要在其类上添加`@scope`注解

```java
@Repository
//@Scope设置bean的作用范围
@Scope("prototype")
public class BookDaoImpl implements BookDao {

    public void save() {
        System.out.println("book dao save ...");
    }
}
```

再次执行App类，打印结果:

![1630031808947](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630031808947.png)

##### 知识点1：@Scope

| 名称 | @Scope                                                       |
| ---- | ------------------------------------------------------------ |
| 类型 | 类注解                                                       |
| 位置 | 类定义上方                                                   |
| 作用 | 设置该类创建对象的作用范围<br/>可用于设置创建出的bean是否为单例对象 |
| 属性 | value（默认）：定义bean作用范围，<br/>==默认值singleton（单例），可选值prototype（非单例）== |

#### 4.3.2 Bean的生命周期-->`@PostConstruct`和`@PreDestroy`注解

​	(1)在BookDaoImpl中添加两个方法，`init`和`destroy`,方法名可以任意

```java
@Repository
public class BookDaoImpl implements BookDao {
    public void save() {
        System.out.println("book dao save ...");
    }
    public void init() {
        System.out.println("init ...");
    }
    public void destroy() {
        System.out.println("destroy ...");
    }
}

```

​	(2)如何对方法进行标识，在对应的方法上添加`@PostConstruct`和`@PreDestroy`注解即可。

```java
@Repository
public class BookDaoImpl implements BookDao {
    public void save() {
        System.out.println("book dao save ...");
    }
    @PostConstruct //在构造方法之后执行，替换 init-method
    public void init() {
        System.out.println("init ...");
    }
    @PreDestroy //在销毁方法之前执行,替换 destroy-method
    public void destroy() {
        System.out.println("destroy ...");
    }
}

```

​	(3)要想看到两个方法执行，需要注意的是`destroy`只有在容器关闭的时候，才会执行，所以需要修改App的类

```java
public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        BookDao bookDao1 = ctx.getBean(BookDao.class);
        BookDao bookDao2 = ctx.getBean(BookDao.class);
        System.out.println(bookDao1);
        System.out.println(bookDao2);
        ctx.close(); //关闭容器
    }
}
```

​	(4)运行App,类查看打印结果，证明init和destroy方法都被执行了。

![1630032385498](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630032385498.png)

​	由于JDK9以后jdk中的javax.annotation包被移除了，这两个注解刚好就在这个包中。	所以@PostConstruct和@PreDestroy注解如果找不到，需要导入下面的jar包:

```java
<dependency>
  <groupId>javax.annotation</groupId>
  <artifactId>javax.annotation-api</artifactId>
  <version>1.3.2</version>
</dependency>
```

##### 	知识点1：@PostConstruct

| 名称 | @PostConstruct         |
| ---- | ---------------------- |
| 类型 | **方法**注解           |
| 位置 | **方法上**             |
| 作用 | 设置该方法为初始化方法 |
| 属性 | 无                     |

##### 	知识点2：@PreDestroy

| 名称 | @PreDestroy          |
| ---- | -------------------- |
| 类型 | **方法**注解         |
| 位置 | **方法上**           |
| 作用 | 设置该方法为销毁方法 |
| 属性 | 无                   |

##### 	**小结**

![1630033039358](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630033039358.png)

### 4.4	注解开发依赖注入@Autowired

​	Spring为了使用注解简化开发，并没有提 供`构造函数注入`、`setter注入`对应的注解，只提供了==自动装配==的注解实现。

​	(1) 在BookServiceImpl类的bookDao属性上添加`@Autowired`注解

```java
@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookDao bookDao;
    
//	  public void setBookDao(BookDao bookDao) {
//        this.bookDao = bookDao;
//    }
    public void save() {
        System.out.println("book service save ...");
        bookDao.save();
    }
}
```

​	**注意:**

* `@Autowired`可以写在属性上，也可也写在setter方法上，最简单的处理方式是`写在属性上并将setter方法删除掉`
* 为什么setter方法可以删除呢?
    * **自动装配**基于==反射设计创建对象并通过暴力反射为私有属性进行设值==
    * 普通反射只能获取public修饰的内容
    * 暴力反射除了获取public修饰的内容还可以获取private修改的内容
    * 所以此处无需提供setter方法

​	(2)`@Autowired`是**默认**按照类型注入，那么==对应BookDao接口如果有多个实现类==，比如添加BookDaoImpl2

```java
@Repository
public class BookDaoImpl2 implements BookDao {
    public void save() {
        System.out.println("book dao save ...2");
    }
}
```

​	这个时候再次运行App，就会报错

![1630034272959](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630034272959.png)

​	此时，按照类型注入就无法区分到底注入哪个对象，解决方案:`按照名称注入`

* 先给两个Dao类==分别起个名称==

    ```java
    @Repository("bookDao")
    public class BookDaoImpl implements BookDao {
        public void save() {
            System.out.println("book dao save ..." );
        }
    }
    @Repository("bookDao2")
    public class BookDaoImpl2 implements BookDao {
        public void save() {
            System.out.println("book dao save ...2" );
        }
    }
    ```

    此时就可以注入成功，但是得思考个问题: 

    * @Autowired是按照类型注入的，给BookDao的两个实现起了名称，它还是有两个bean对象，为什么不报错?

    * !!@Autowired默认按照类型自动装配，如果IOC容器中同类的Bean找到多个，就==按照**变量名和Bean的名称匹配**。因为变量名叫`bookDao`而容器中也有一个`booDao`==，所以可以成功注入。

    * 分析下面这种情况是否能完成注入呢?

        ![1630036236150](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630036236150.png)

    * 答: 不行，因为按照类型会找到多个bean对象，此时会按照`bookDao`名称去找，因为IOC容器只有名称叫`bookDao1`和`bookDao2`,所以找不到，会报`NoUniqueBeanDefinitionException`

#### 4.4.3 注解实现按照名称注入-->@Qualifier()

​	当根据类型在容器中找到多个bean,注入参数的属性名又和容器中bean的名称不一致，这个时候该如何解决，就需要使用到`@Qualifier`来指定注入哪个名称的bean对象。

```java
@Service
public class BookServiceImpl implements BookService {
    @Autowired
    @Qualifier("bookDao1")
    private BookDao bookDao;
    
    public void save() {
        System.out.println("book service save ...");
        bookDao.save();
    }
}
```

@Qualifier注解后的值就是需要注入的bean的名称。

==注意:@Qualifier不能独立使用，必须和@Autowired一起使用==

#### 4.4.4	简单数据类型注入-->@Value()注解

​	简单类型注入就比较容易懂了。简单类型注入的是基本数据类型或者字符串类型，下面在`BookDaoImpl`类中添加一个`name`属性，用其进行简单类型注入

```java
@Repository("bookDao")
public class BookDaoImpl implements BookDao {
    private String name;
    public void save() {
        System.out.println("book dao save ..." + name);
    }
}
```

​	数据类型换了，对应的注解也要跟着换，这次使用`@Value`注解，将值写入注解的参数中就行了

```java
@Repository("bookDao")
public class BookDaoImpl implements BookDao {
    @Value("itheima")
    private String name;
    public void save() {
        System.out.println("book dao save ..." + name);
    }
}
```

​	注意数据格式要匹配，如将"abc"注入给int值，这样程序就会报错。

#### 4.4.5	注解读取properties配置文件-->@PropertySource

​	`@Value`一般会被用在从properties配置文件中读取内容进行使用，具体如何实现?

##### 	步骤1：resource下准备properties文件

​	jdbc.properties

```properties
name=itheima888
```

##### 	步骤2: 使用注解加载properties配置文件

​	在配置类上添加`@PropertySource`注解

```java
@Configuration
@ComponentScan("com.itheima")
@PropertySource("jdbc.properties")
public class SpringConfig {
}

```

##### 	步骤3：使用@Value读取配置文件中的内容

```java
@Repository("bookDao")
public class BookDaoImpl implements BookDao {
    @Value("${name}")
    private String name;
    public void save() {
        System.out.println("book dao save ..." + name);
    }
}
```

​	**注意:**

* 如果读取的properties配置文件有多个，可以使用`@PropertySource`的属性来指定多个，要使用==数组的形式==。

    ```java
    @PropertySource({"jdbc.properties","xxx.properties"})
    ```

* `@PropertySource`注解属性中==不支持使用通配符`*`==,运行会报错

    ```java
    @PropertySource({"*.properties"})
    ```

* `@PropertySource`注解属性中可以把`classpath:`加上,代表==从当前项目的根路径找文件==

    ```java
    @PropertySource({"classpath:jdbc.properties"})
    ```

#### 	知识点1：@Autowired


| 名称 | @Autowired                                                   |
| ---- | ------------------------------------------------------------ |
| 类型 | 属性注解  或  方法注解（了解）  或  方法形参注解（了解）     |
| 位置 | **属性定义上方**  或  标准set方法上方  或  类set方法上方  或  **方法形参前面** |
| 作用 | 为引用类型属性设置值                                         |
| 属性 | required：true/false，定义该属性是否允许为null               |

#### 	知识点2：@Qualifier

| 名称 | @Qualifier                                           |
| ---- | ---------------------------------------------------- |
| 类型 | 属性注解  或  方法注解（了解）                       |
| 位置 | 属性定义上方  或  标准set方法上方  或  类set方法上方 |
| 作用 | 为引用类型属性指定注入的beanId                       |
| 属性 | value（默认）：设置注入的beanId                      |

#### 	知识点3：@Value

| 名称 | @Value                                               |
| ---- | ---------------------------------------------------- |
| 类型 | 属性注解  或  方法注解（了解）                       |
| 位置 | 属性定义上方  或  标准set方法上方  或  类set方法上方 |
| 作用 | 为  基本数据类型  或  字符串类型  属性设置值         |
| 属性 | value（默认）：要注入的属性值                        |

#### 	知识点4：@PropertySource

| 名称 | @PropertySource                                              |
| ---- | ------------------------------------------------------------ |
| 类型 | 类注解                                                       |
| 位置 | **类定义上方**                                               |
| 作用 | 加载properties文件中的属性值                                 |
| 属性 | value（默认）：设置加载的properties文件对应的文件名或文件名组成的数组 |



### 4.5	引入外部配置类

​	如果把所有的第三方bean都配置到Spring的配置类`SpringConfig`中，虽然可以，但是不利于代码阅读和分类管理，所有我们就想能不能按照类别将这些bean配置到不同的配置类中?

​	对于数据源的bean,我们新建一个`JdbcConfig`配置类，并把数据源配置到该类下。

```java
public class JdbcConfig {
	@Bean
    public DataSource dataSource(){
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/spring_db");
        ds.setUsername("root");
        ds.setPassword("root");
        return ds;
    }
}
```

​	现在的问题是，这个配置类如何能被Spring配置类加载到，并创建DataSource对象在IOC容器中?

​	针对这个问题，有两个解决方案:

#### 4.5.1	使用包扫描引入

##### 	步骤1:在Spring的配置类上添加包扫描

```java
@Configuration
@ComponentScan("com.itheima.config")
public class SpringConfig {
	
}
```

##### 	步骤2:在JdbcConfig上添加配置注解

​	JdbcConfig类要放入到`com.itheima.config`包下，需要被Spring的配置类扫描到即可

```java
@Configuration
public class JdbcConfig {
	@Bean
    public DataSource dataSource(){
        // ......
    }
}
```

##### 	步骤3:运行程序

​	依然能获取到bean对象并打印控制台。

​	这种方式虽然能够扫描到，但是==不能很快的知晓都引入了哪些配置类==，所有这种方式不推荐使用。

#### 4.5.2	使用`@Import`引入

​	这种方案可以不用加`@Configuration`注解，但是必须在Spring配置类上使用`@Import`注解手动引入需要加载的配置类:

##### 	步骤1:去除JdbcConfig类上的注解

```java
public class JdbcConfig {
	@Bean
    public DataSource dataSource(){
        // ......
    }
}
```

##### 	步骤2:在Spring配置类中引入

```java
@Configuration
//@ComponentScan("com.itheima.config")
@Import({JdbcConfig.class})
public class SpringConfig {
	
}
```

​	**注意:**

* ==扫描注解@ComponentScan("com.itheima.config")可以移除==

* @Import参数需要的是一个数组，可以引入多个配置类。

* @Import注解在配置类中**只能写一次**，下面的方式是==不允许的==

    ```java
    @Configuration
    //@ComponentScan("com.itheima.config")
    @Import(JdbcConfig.class)
    @Import(Xxx.class)
    public class SpringConfig {
    	
    }
    ```

### 	知识点1：@Bean

| 名称 | @Bean                                          |
| ---- | ---------------------------------------------- |
| 类型 | 方法注解                                       |
| 位置 | **方法定义上方**                               |
| 作用 | 设置该方法的==**返回值**==作为spring管理的bean |
| 属性 | value（默认）：定义bean的id                    |

### 	知识点2：@Import

| 名称 | @Import                                                      |
| ---- | ------------------------------------------------------------ |
| 类型 | 类注解                                                       |
| 位置 | 类定义上方                                                   |
| 作用 | 导入配置类                                                   |
| 属性 | value（默认）：定义导入的==配置类类名==，<br/>当配置类有多个时使用数组格式一次性导入多个配置类 |

### 4.6	注解开发实现为第三方bean注入资源

​	在使用@Bean创建bean对象的时候，如果方法在创建的过程中需要其他资源(例如配置文件)该怎么办?

​	这些资源会有两大类，分别是`简单数据类型` 和`引用数据类型`。

#### 4.6.1	简单数据类型

##### 	步骤1:类中提供四个属性

```java
public class JdbcConfig {
    private String driver;
    private String url;
    private String userName;
    private String password;
	@Bean
    public DataSource dataSource(){
       // ...
    }
}
```

##### 	步骤2:使用`@Value`注解引入值

```java
public class JdbcConfig {
    @Value("com.mysql.jdbc.Driver")
    private String driver;
    @Value("jdbc:mysql://localhost:3306/spring_db")
    private String url;
    @Value("root")
    private String userName;
    @Value("password")
    private String password;
	@Bean
    public DataSource dataSource(){
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(userName);
        ds.setPassword(password);
        return ds;
    }
}
```

#### 4.6.2	引用数据类型

​	假设第三方Bean中需要BookDao这一引用类型。

##### 	步骤1:在SpringConfig中扫描BookDao

​	扫描的目的是让Spring能管理到BookDao,也就是说要让IOC容器中有一个bookDao对象

```java
@Configuration
@ComponentScan("com.itheima.dao")
@Import({JdbcConfig.class})
public class SpringConfig {
}
```

##### 	步骤2:在JdbcConfig类的方法上添加形式参数

```java
@Bean
public DataSource dataSource(BookDao bookDao){
    System.out.println(bookDao);
    DruidDataSource ds = new DruidDataSource();
    ds.setDriverClassName(driver);
    ds.setUrl(url);
    ds.setUsername(userName);
    ds.setPassword(password);
    return ds;
}
```

​	小结: ==引用类型注入只需要为bean定义方法设置形参即可，容器会根据类型自动装配对象。==

### 4.7	注解开发总结

![1630134786448](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630134786448.png)







## 5.0	Spring整合Mybatis

1. 在maven中导入两个相关的依赖

2. Spring要管理MyBatis中的SqlSessionFactory，所以要新建一个MybatisConfig配置类，使用一个新的类`SqlSessionFactoryBean`扫描类型别名的包

    ```java
    @Bean
        public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource){
            SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
            ssfb.setTypeAliasesPackage("com.itheima.domain");// 从mysql数据库中读取转化的类型对象
            ssfb.setDataSource(dataSource);
            return ssfb;
        }
    ```

3. Spring要管理Mapper接口的扫描，在MybatisConfig配置类中设置扫描映射的包

    ```java
    @Bean
        public MapperScannerConfigurer mapperScannerConfigurer(){
            MapperScannerConfigurer msc = new MapperScannerConfigurer();
            msc.setBasePackage("com.itheima.dao");
            return msc;
        }
    ```

​	此时Spring与Mybatis的整合就已经完成了，其中主要用到的两个类分别是:

* ==SqlSessionFactoryBean==
* ==MapperScannerConfigurer==

## 6.0	Spring整合Junit

1. 使用maven中导入两个相关的依赖@ContextConfiguration
2. 创建测试类
3. 设置==类运行器==的注解@RunWith和==加载配置类==的注解
4. (可选)设置自动装配注入bean的注解@Autowired
5. 测试方法前加注解@Test

```java
//设置类运行器
@RunWith(SpringJUnit4ClassRunner.class)
//设置Spring环境对应的配置类
@ContextConfiguration(classes = {SpringConfiguration.class}) //加载配置类
//@ContextConfiguration(locations={"classpath:applicationContext.xml"})//加载配置文件
public class AccountServiceTest {
    //支持自动装配注入bean
    @Autowired
    private AccountService accountService;
    @Test
    public void testFindById(){
    // ...
    }
}
```

​	**注意:**

* 单元测试，如果测试的是配置文件，则使用`@ContextConfiguration(locations={配置文件名,...})`
* Junit运行后是基于Spring环境运行的，所以Spring提供了一个专用的类运行器，这个务必要设置，这个类运行器就在Spring的测试专用包中提供的，导入的坐标就是这个东西`SpringJUnit4ClassRunner`
* Junit和Mybatis的整合都是固定格式。可以套用

#### 	知识点1：@RunWith

| 名称 | @RunWith                          |
| ---- | --------------------------------- |
| 类型 | 测试类注解                        |
| 位置 | **测试类定义上方**                |
| 作用 | 设置JUnit运行器                   |
| 属性 | value（默认）：运行所使用的运行期 |

#### 	知识点2：@ContextConfiguration

| 名称 | @ContextConfiguration                                        |
| ---- | ------------------------------------------------------------ |
| 类型 | 测试类注解                                                   |
| 位置 | 测试类定义上方                                               |
| 作用 | 设置JUnit加载的Spring核心配置                                |
| 属性 | classes：核心配置类，可以使用数组的格式设定加载多个配置类<br/>locations:配置文件，可以使用数组的格式设定加载多个配置文件名称 |



## 7.0	Spring的事务管理

- 事务作用：在数据层保障一系列的数据库操作同成功同失败
- Spring事务作用：在数据层或**==业务层==**保障一系列的数据库操作同成功同失败

​	Spring为了管理事务，提供了一个平台事务管理器`PlatformTransactionManager`

![1630243651541](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630243651541.png)

​	PlatformTransactionManager只是一个接口，Spring还为其提供了一个具体的实现:

![1630243993380](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630243993380.png)

​	从名称上可以看出，我们只需要给它一个DataSource对象，它就可以帮你去在业务层管理事务。其内部采用的是JDBC的事务。所以说如果你持久层采用的是JDBC相关的技术，就可以采用这个事务管理器来管理你的事务。而==Mybatis内部采用的就是JDBC的事务==，所以后期我们==Spring整合Mybatis==就采用的这个DataSourceTransactionManager事务管理器。

### 7.1	基本事务开启

##### 	知识点1：@EnableTransactionManagement

| 名称 | @EnableTransactionManagement           |
| ---- | -------------------------------------- |
| 类型 | **配置类**注解                         |
| 位置 | 配置类定义上方                         |
| 作用 | 设置当前Spring环境中开启注解式事务支持 |

##### 	知识点2：@Transactional   

| 名称 | @Transactional                                               |
| ---- | ------------------------------------------------------------ |
| 类型 | 接口注解  类注解  方法注解                                   |
| 位置 | 业务层接口上方  业务层实现类上方  业务方法上方               |
| 作用 | 为当前业务层方法添加事务（如果设置在类或接口上方则类或接口中所有方法均添加事务） |

​	==注意:==

​	@Transactional可以写在接口类上、接口方法上、实现类上和实现类方法上

* 写在接口类上，该接口的所有实现类的**所有方法**都会有事务
* 写在接口方法上，该接口的**所有实现类的该方法**都会有事务
* 写在实现类上，该类中的**所有方法**都会有事务
* 写在实现类方法上，该方法上有事务
* ==建议写在实现类或实现类的方法上==

### 7.2	Spring事务角色

​	Spring中事务角色有两个:分别是`事务管理员`和`事务协调员`。

​	对于每个单独开启事务的相关方法，如果它们处于开启Spring的事务管理的方法中，则这些方法的事务会被统一整个入Spring的一个事务中。

![1630249111055](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630249111055.png)



​	

* transfer上添加了@Transactional注解，在该方法上就会有一个事务T
* AccountDao的outMoney方法的事务T1加入到transfer的事务T中
* AccountDao的inMoney方法的事务T2加入到transfer的事务T中
* 这样就保证他们在同一个事务中，当业务层中出现异常，整个事务就会回滚，保证数据的准确性。



​	于是我们就可以得到如下概念:

- 事务管理员：发起事务方，在Spring中通常指代业务层开启事务的方法
- 事务协调员：加入事务方，在Spring中通常指代数据层方法，也可以是业务层方法

​	==注意:==

​	目前的事务管理是基于`DataSourceTransactionManager`和`SqlSessionFactoryBean`使用的是同一个数据源。如果在设置bean类时它们使用的不是同一个datasource，则无法实现上述的事务处理效果。

### 7.3	Spring事务属性

#### 7.3.1	实物配置

​	事务属性主要有三部分内容`事务配置`、`事务传播行为`。

​	

![1630250069844](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630250069844.png)

​	上面这些属性都可以在`@Transactional`注解的参数上进行设置。

* readOnly：true只读事务，false读写事务，增删改要设为false,查询设为true。

* timeout:设置超时时间单位秒，在多长时间之内事务没有提交成功就自动回滚，-1表示不设置超时时间。

* rollbackFor:当出现指定异常进行事务回滚

* noRollbackFor:当出现指定异常不进行事务回滚

    * noRollbackFor是设定对于指定的异常不回滚

    * rollbackFor是**指定回滚异常**，需要注意的是，Spring的事务==只会对`Error异常`和`RuntimeException异常`及其子类进行事务回滚==，其他的异常类型是不会回滚的，比如IOException不符合上述条件所以不回滚。

​	此时我们就可以使用注解@Transactional()后的rollbackFor属性来设置出现的异常回滚:

```java
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;
	 @Transactional(rollbackFor = {IOException.class})
    public void transfer(String out,String in ,Double money) throws IOException{
        accountDao.outMoney(out,money);
        //int i = 1/0; //这个异常事务会回滚
        if(true){
            throw new IOException(); //这个异常事务就不会回滚
        }
        accountDao.inMoney(in,money);
    }

}
```

* rollbackForClassName等同于rollbackFor,只不过属性为异常的类全名字符串

* noRollbackForClassName等同于noRollbackFor，只不过属性为异常的类全名字符串

* isolation设置事务的隔离级别

    * DEFAULT   :默认隔离级别, 会采用数据库的隔离级别
    * READ_UNCOMMITTED : 读未提交
    * READ_COMMITTED : 读已提交
    * REPEATABLE_READ : 重复读取
    * SERIALIZABLE: 串行化

#### 7.3.2	事务传播行为

​	事务传播行为指的是==**事务协调员**对**事务管理员**所携带事务的处理态度==。

​	例如，如果我们想让某个开启事务的方法(事务协调员)不被Spring(事务管理员)所开启事务所包含，我们就可以设置@Transactional的propagation属性为对应的值。

![1630254257628](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630254257628.png)

​	此处`REQUIRES_NEW`就是我们需要的值:

```java
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogDao logDao;
	//propagation设置事务属性：传播行为设置为当前操作需要新事务
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String out,String in,Double money ) {
        logDao.log("转账操作由"+out+"到"+in+",金额："+money);
    }
}
```





















[^1]: Spring通过依赖注入的方式来管理对象之间的依赖关系。开发者只需要声明对象之间的依赖关系，而不需要显式地创建和管理这些对象的实例。这种方式使得代码更加松耦合、可测试和可维护。
[^2]: Spring支持面向切面编程，可以将与业务逻辑无关的横切关注点（如事务管理、日志记录等）从业务逻辑中分离出来，以增强应用程序的模块性和可重用性。
# AOP面向切面编程笔记

​	Spring有两个核心的概念，一个是`IOC/DI`，一个是`AOP`。AOP是在==不改原有代码的前提下对其进行增强==。

## 0.0	一些基本概念

![1630144353462](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630144353462.png)

* **连接点**(JoinPoint)：程序执行过程中的任意位置，粒度为执行方法、抛出异常、设置变量等
    * 在**SpringAOP**中，连接点理解为==方法的执行==
* **切入点**(Pointcut):匹配连接点的式子，一般指**要被增强的方法**
    * 在**SpringAOP**中，一个切入点可以==描述一个具体方法，也可也匹配多个方法==
        * 一个具体的方法:如com.itheima.dao包下的BookDao接口中的无形参无返回值的save方法
        * 匹配多个方法:所有的save方法，所有的get开头的方法，所有以Dao结尾的接口中的任意方法，所有带有一个参数的方法
    * 连接点范围要比切入点范围**大**，是切入点的方法也一定是连接点，但是连==接点的方法就不一定要被增强==，所以可能不是切入点。
* **通知**(Advice):在==切入点处执行的操作==，也就是共性功能
    * 在SpringAOP中，功能最终==以方法的形式呈现==
* **通知类**：==定义通知的类==，可自己随便定义
* **切面**(Aspect):描述==通知与切入点的对应关系==。

## 1.0	AOP基本实现步骤

1. ##### 步骤1:添加一个依赖

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.4</version>
</dependency>
```

- 导入AspectJ的jar包,AspectJ是AOP思想的一个具体实现，Spring有自己的AOP实现，但是相比于AspectJ来说比较麻烦，所以我们直接采用Spring整合ApsectJ的方式进行AOP开发。

2. ##### 步骤2:**定义通知类和通知**

```java
public class MyAdvice {
    public void method(){
        System.out.println(System.currentTimeMillis());
    }
}
```

​	类名和方法名没有要求，可以任意。

3. ##### 步骤3:**定义切入点**-->@Pointcut(“execution(type 方法的全路径)”)

​	**说明**:

* 切入点定义依托一个==不具有实际意义的方法进行==，即无参数、无返回值、方法体无实际逻辑。
* execution及后面编写的内容，后面会有章节专门去学习。

```java
public class MyAdvice {
    @Pointcut("execution(void com.itheima.dao.BookDao.update())")
    private void pt(){}
    
    @Before("pt()")
    public void method(){
        System.out.println(System.currentTimeMillis());
    }
}
```

​	绑定切入点与通知关系，并指定通知添加到原始连接点的具体执行==位置==。

​	注解**@Before**意味着通知类会在切入点之前执行。

4. ##### 步骤4:将通知类配给容器并标识其为切面类-->@Aspect和@Component

```java
@Component
@Aspect
public class MyAdvice {
    @Pointcut("execution(void com.itheima.dao.BookDao.update())")
    private void pt(){}
    
    @Before("pt()")
    public void method(){
        System.out.println(System.currentTimeMillis());
    }
}
```

5. ##### 步骤5:开启注解格式AOP功能-->@EnableAspectJAutoProxy

```java
@Configuration
@ComponentScan("com.itheima")
@EnableAspectJAutoProxy
public class SpringConfig {
}
```

### 	知识点1：@EnableAspectJAutoProxy  

| 名称 | @EnableAspectJAutoProxy |
| ---- | ----------------------- |
| 类型 | 配置类注解              |
| 位置 | 配置类定义上方          |
| 作用 | 开启注解格式AOP功能     |

### 	知识点2：@Aspect

| 名称 | @Aspect               |
| ---- | --------------------- |
| 类型 | ==类==注解            |
| 位置 | 切面类定义上方        |
| 作用 | 设置当前类为AOP切面类 |

### 	知识点3：@Pointcut   

| 名称 | @Pointcut                   |
| ---- | --------------------------- |
| 类型 | 方法注解                    |
| 位置 | 切入点方法定义上方          |
| 作用 | 设置切入点方法              |
| 属性 | value（默认）：切入点表达式 |

### 	知识点4：@Before

| 名称 | @Before                                                      |
| ---- | ------------------------------------------------------------ |
| 类型 | 方法注解                                                     |
| 位置 | 通知方法定义上方                                             |
| 作用 | 设置当前通知方法与切入点之间的绑定关系，当前通知方法在原始切入点方法前运行 |

## 2.0 AOP工作流程

##### 	流程1:Spring容器启动

- 会被加载的类有:
    - 需要被增强的类，如:BookServiceImpl
    - 通知类，如:MyAdvice
    - 注意此时bean对象还没有创建成功

##### 	流程2:读取所有切面配置中的切入点

- 没有被使用的切入点不会被读取。

##### 	流程3:初始化bean

判定bean对应的类中的方法是否匹配到任意切入点

* 注意第1步在容器启动的时候，bean对象还没有被创建成功。
* 要被实例化bean对象的类中的方法和切入点进行匹配
* ![1630152538083](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630152538083.png)
    * 匹配**失败**，创建原始对象,如`UserDao`
        * 匹配失败说明不需要增强，直接调用原始对象的方法即可。
    * 匹配**成功**，创建原始对象（==目标对象==）的==代理==对象,如:`BookDao`
        * 匹配成功说明需要对其进行增强
        * 对哪个类做增强，这个类对应的对象就叫做目标对象
        * 因为要对目标对象进行功能增强，而采用的技术是动态代理，所以会为其创建一个代理对象
        * 最终运行的是代理对象的方法，在该方法中会对原始方法进行功能增强

##### 	流程4:获取bean执行方法

* 获取的bean是原始对象时，调用方法并执行，完成操作
* 获取的bean是代理对象时，根据代理对象的运行模式运行原始方法与增强的内容，完成操作

### 2.1	关于容器中对象的问题

​	如果匹配成功，得到的会是代理对象(toString()方法被重写过的)。

​	如果匹配失败，就是一般的bean对象。

## 3.0	AOP切入点表达式

​	首先我们先要明确两个概念:

* 切入点:要进行增强的方法
* 切入点表达式:要进行增强的方法的描述方式

​	对于切入点的描述，我们其实是有两中方式的，先来看下前面的例子

![1630156172790](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630156172790.png)

​	描述方式一==（接口）==：执行com.itheima.dao包下的BookDao接口中的无参数update方法

```java
execution(void com.itheima.dao.BookDao.update())
```

​	描述方式二==（实现类）==：执行com.itheima.dao.impl包下的BookDaoImpl类中的无参数update方法

```
execution(void com.itheima.dao.impl.BookDaoImpl.update())
```

​	因为调用接口方法的时候最终运行的还是其实现类的方法，所以上面两种描述方式==都是可以的==。

​	对于切入点表达式的语法为:

* 切入点表达式标准格式：`动作关键字`(`访问修饰符`  `返回值`  `包名.类/接口名.方法名(参数)` `异常名`）

```
execution(public User com.itheima.service.UserService.findById(int))
```

* **execution**：动作关键字，描述切入点的行为动作，例如execution表示执行到指定切入点
* **public**:访问修饰符,还可以是public，private等，可以省略
* **User**：返回值，写返回值类型
* **com.itheima.service**：包名，多级包使用点连接
* **UserService**:类/接口名称
* **findById**：方法名
* **int**:参数，直接写参数的类型，多个类型用逗号隔开
* **异常名**：方法定义中抛出指定异常，可以省略

​	切入点表达式就是要找到需要增强的方法，所以它就是对一个具体方法的描述。

### 3.1	切入点表达式的通配符

​	使用通配符描述切入点，主要的目的就是简化之前的配置:

* `*`:单个独立的任意符号，可以独立出现，也可以作为前缀或者后缀的匹配符出现

    ```
    execution（public * com.itheima.*.UserService.find*(*))
    ```

    匹配com.itheima包下的任意包中的UserService类或接口中所有==find开头==的带有一个参数的方法

* `..`：多个连续的任意符号，可以独立出现，常用于简化包名与参数的书写。匹配任意多层

    ```
    execution（public User com..UserService.findById(..))
    ```

    匹配com包下的任意包中的UserService类或接口中所有名称为findById的方法

* `+`：专用于匹配子类类型

    ```
    execution(* *..*Service+.*(..))
    ```

    这个使用率较低，描述子类的，咱们做JavaEE开发，继承机会就一次，使用都很慎重，所以很少用它。*Service+，表示所有以Service结尾的接口的子类。

### 3.2	书写技巧

对于切入点表达式的编写其实是很灵活的，那么在编写的时候，有没有什么好的技巧让我们用用:

- 所有代码按照标准规范开发，否则以下技巧全部失效
- 描述切入点通**==常描述接口==**，而不描述实现类,如果描述到实现类，就出现紧耦合了
- 访问控制修饰符针对接口开发均采用public描述（**==可省略访问控制修饰符描述==**）
- 返回值类型对于增删改类使用精准类型加速匹配，对于查询类使用\*通配快速描述
- **==包名==**书写**==尽量不使用..匹配==**，效率过低，常用\*做单个包描述匹配，或精准匹配
- **==接口名/类名==**书写名称与模块相关的**==采用\*匹配==**，例如UserService书写成\*Service，绑定业务层接口名
- **==方法名==**书写以**==动词==**进行**==精准匹配==**，名词采用*匹配，例如getById书写成getBy*,selectAll书写成selectAll
- 参数规则较为复杂，根据业务方法灵活调整
- 通常**==不使用异常==**作为**==匹配==**规则

## 4.0	AOP通知

​	AOP通知描述了==抽取的共性功能==，根据共性功能抽取的位置不同，最终运行代码时要将其加入到合理的位置。Spring共提供了5种通知类型:

- 前置通知
- 后置通知
- **==环绕通知(重点)==**:环绕通知,环绕通知功能比较强大，它可以追加功能到方法执行的前后，这也是比较常用的方式，它可以实现其他四种通知类型的功能
- 返回后通知(了解):追加功能==到方法执行后==，只有方法正常执行结束后才进行,类似于在代码3添加内容，如果方法执行抛出异常，返回后通知将**不会**被添加-->@AfterReturning()
- 抛出异常后通知(了解):追加功能==到方法抛出异常后==，只有方法执行出异常才进行,类似于在代码4添加内容，**只有**方法抛出异常后才会被添加-->@AfterThrowing  ()

![1630166147697](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630166147697.png)

### 4.1	环绕通知-->@Around()

```java
@Component
@Aspect
public class MyAdvice {
    @Pointcut("execution(void com.itheima.dao.BookDao.update())")
    private void pt(){}
    
    @Around("pt()")
    public void around(ProceedingJoinPoint pjp) throws Throwable{
        System.out.println("around before advice ...");
        //表示对原始操作的调用
        pjp.proceed();
        System.out.println("around after advice ...");
    }
}
```

​	如果我们使用环绕通知的话，需要在增强方法中显式地调用`ProceedingJoinPoint`对象的方法`Object proceed() throws Throwable`表示原始方法的执行。且要根据==原始方法==的返回值来设置==环绕通知的返回值==，proceed()方法的返回值就是原始方法运行完return的值，可以用一个变量接住再强转。最后还要再==增强方法中return相应类型的值==。

### 4.2	通知类型总结

##### 知识点1：@After

| 名称 | @After                                                       |
| ---- | ------------------------------------------------------------ |
| 类型 | 方法注解                                                     |
| 位置 | 通知方法定义上方                                             |
| 作用 | 设置当前通知方法与切入点之间的绑定关系，当前通知方法在原始切入点方法后运行 |

##### 知识点2：@AfterReturning  

| 名称 | @AfterReturning                                              |
| ---- | ------------------------------------------------------------ |
| 类型 | 方法注解                                                     |
| 位置 | 通知方法定义上方                                             |
| 作用 | 设置当前通知方法与切入点之间绑定关系，当前通知方法在原始切入点方法正常执行完毕后执行 |

##### 知识点3：@AfterThrowing  

| 名称 | @AfterThrowing                                               |
| ---- | ------------------------------------------------------------ |
| 类型 | 方法注解                                                     |
| 位置 | 通知方法定义上方                                             |
| 作用 | 设置当前通知方法与切入点之间绑定关系，当前通知方法在原始切入点方法运行抛出异常后执行 |

##### 知识点4：@Around

| 名称 | @Around                                                      |
| ---- | ------------------------------------------------------------ |
| 类型 | 方法注解                                                     |
| 位置 | 通知方法定义上方                                             |
| 作用 | 设置当前通知方法与切入点之间的绑定关系，当前通知方法在原始切入点方法前后运行 |

​	==**环绕通知注意事项**==

1. 环绕通知必须依赖形参ProceedingJoinPoint才能实现对原始方法的调用，进而实现原始方法调用前后同时添加通知
2. 通知中如果未使用ProceedingJoinPoint对原始方法进行调用==将跳过原始方法==的执行
3. 对原始方法的调用可以不接收返回值，通知方法设置成void即可，如果接收返回值，==最好设定为Object类型==
4. 原始方法的返回值如果是void类型，通知方法的返回值类型可以设置成void,也可以设置成Object
5. 由于无法预知原始方法运行后是否会抛出异常，因此环绕通知方法必须要处理Throwable异常

### 4.3	AOP通知获取数据 

​	我们将从`获取参数`、`获取返回值`和`获取异常`三个方面来研究切入点的相关信息。

* 获取切入点方法的参数，所有的通知类型都可以获取参数
    * JoinPoint：适用于前置、后置、返回后、抛出异常后通知
    * ProceedingJoinPoint：适用于环绕通知
* 获取切入点方法返回值，前置和抛出异常后通知是没有返回值，后置通知可有可无，所以不做研究
    * 返回后通知
    * 环绕通知
* 获取切入点方法运行异常信息，前置和返回后通知是不会有，后置通知可有可无，所以不做研究
    * 抛出异常后通知
    * 环绕通知

#### 4.3.1	非环绕通知获取参数方式-->JoinPoint

​	在方法形参上添加JoinPoint类的形参,在方法内通过==JoinPoint.getArgs()==方法来获取参数。

```java
@Component
@Aspect
public class MyAdvice {
    @Pointcut("execution(* com.itheima.dao.BookDao.findName(..))")
    private void pt(){}

    @Before("pt()")
    public void before(JoinPoint jp) 
        Object[] args = jp.getArgs();
        System.out.println(Arrays.toString(args));
        System.out.println("before advice ..." );
    }
	//...其他的略
}
```

​	此方法返回之为一个Object类型的数组。

​	**说明:**

​	使用JoinPoint的方式获取参数适用于`前置`、`后置`、`返回后`、`抛出异常后`通知。

#### 4.3.2	环绕通知获取参数方式-->ProceedingJoinPoint

​	环绕通知使用的是ProceedingJoinPoint，因为ProceedingJoinPoint是JoinPoint类的子类，所以对于ProceedingJoinPoint类中应该也会有对应的`getArgs()`方法。

**注意:**

* pjp.proceed()方法是有两个构造方法，分别是:

    ![1630234756123](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630234756123.png)

    * 调用无参数的proceed，当原始方法有参数，会在调用的过程中自动传入参数。所以调用这两个方法的任意一个都可以完成功能

    * 但是当需要==修改原始方法的参数==时，就只能采用带有参数的方法,如下:

        ```java
        @Component
        @Aspect
        public class MyAdvice {
            @Pointcut("execution(* com.itheima.dao.BookDao.findName(..))")
            private void pt(){}
        
            @Around("pt()")
            public Object around(ProceedingJoinPoint pjp) throws Throwable{
                Object[] args = pjp.getArgs();
                System.out.println(Arrays.toString(args));
                args[0] = 666;
                Object ret = pjp.proceed(args);
                return ret;
            }
        	//其他的略
        }
        ```

        有了这个特性后，我们就可以在环绕通知中对原始方法的==参数进行拦截过滤==，避免由于参数的问题导致程序无法正确运行，保证代码的健壮性。

#### 4.3.3	获取返回值

​	对于返回值，只有返回后`AfterReturing`和环绕`Around`这两个通知类型可以获取。

##### 4.3.31	环绕通知

​	就是`proceed(args)`方法的返回值，具体上面有。

##### 4.3.32	返回后通知获取返回值-->注解参数value和returning

​	返回后通知可以用丰富的注解参数和方法的形参获得返回值，比环绕通知更简单方便。

```java
@Component
@Aspect
public class MyAdvice {
    @Pointcut("execution(* com.itheima.dao.BookDao.findName(..))")
    private void pt(){}

    @AfterReturning(value = "pt()",returning = "ret")
    public void afterReturning(Object ret) {
        System.out.println("afterReturning advice ..."+ret);
    }
	//其他的略
}
```

​	==注意:==

​	(1)参数名的问题

![1630237320870](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630237320870.png)

​	(2)afterReturning方法参数类型的问题

​	参数类型可以写成String，但是为了能匹配更多的参数类型，建议==写成Object类型==

​	(3)afterReturning方法参数的顺序问题

![1630237586682](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630237586682.png)

​	如果有JoinPoint参数，参数必须要放==第一位==

#### 4.3.4	获取异常 

​	对于获取抛出的异常，只有抛出异常后`AfterThrowing`和环绕`Around`这两个通知类型可以获取。

##### 4.3.41	环绕通知获取异常

​	直接用try{}catch{}获取即可

##### 4.3.42	抛出异常后通知获取异常

​	具体方式与**返回后通知获取**返回值的操作很像

```java
@Component
@Aspect
public class MyAdvice {
    @Pointcut("execution(* com.itheima.dao.BookDao.findName(..))")
    private void pt(){}

    @AfterThrowing(value = "pt()",throwing = "t")
    public void afterThrowing(Throwable t) {
        System.out.println("afterThrowing advice ..."+t);
    }
	//其他的略
}
```

​	==注意:==

![1630239939043](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1630239939043.png)


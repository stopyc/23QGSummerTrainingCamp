## spring学习笔记

spring框架是一个必学，市场占有率极高的框架。学习它我从网上的学习笔记下手，以下是我自己通过学习资料的感悟和过程。

###### 首先学习肯定要先安装

    直接用idea建立一个标准maven格式的工程，然后我们直接再pom.xml中导入一下依赖即可

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.2.10.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    写完记得更新maven


###### 然后就是对spring的初认识

其中一个就是依赖注入：
这个听起来十分的高深，以至于我在刚开始的时候都不知道它在说什么，直到我自己暂停翻笔记才明白：
    依赖注入用大白话来说，就是我们定义一个接口，定义一个继承类，把他们认定为服务层的东西，然后再定义一个接口，定义一个继承类，把他们两认定为是DAO层的东西。不知道什么是服务层和DAO层也没有关系，只要知道服务层依赖于DAO层就行。依赖就是需要再服务层的类中再新建一个DAO层的类才可以进行服务层的操作。而这必须要进行的新建类才可以继续编写服务层的现象用行业黑话来解释就是“依赖”，而spring做的事情就是“依赖注入”解决这个繁琐的步骤。
    那spring是怎么样进行“依赖注入”的呢？   就是通过spring框架的内部手段，所有的类都存储在一个容器中，需要的时候直接从容器中拿即可。
    具体表现为
    ![Alt text](image.png)
    上面的applicationContext.xml就是容器要装谁的清单。在里面写入bean的标签，类就会被装入其中。
    ![Alt text](image-1.png)


###### 对依赖注入的进一步认识

这里大家看![Alt text](image-2.png)
图中的配置到底是怎样形成的呢？  笔记中的形成依赖注入步骤有
1.去掉需要注入依赖的类中的new
2.直接定义需要的私有成员类，然后设置set方法给那个类，这一步没做的话会导致配置找不到类中的属性而出错
3.修改配置成上图中的模样

![Alt text](image-3.png)
奇怪，这里说的对应接口指的是什么呢？

```xml
<!-- 这配置我觉得有点像一个bean标签就是存下了一种对象，然后如果你想直接进行对象注入的话，可以在bean标签内在写一个property标签，表示你要注入ioc容器中的哪个对象到你这个bean中，ref的属性值，也可也是另一个bean的name属性值，不过此处还是建议使用其id来进行注入 -->
<bean id="bookDao" class="org.example.BookDaoImpl"/>
    <bean id="bookService" class="org.example.BookServiceImpl">
    <!--配置server与dao的关系-->
    <!--property标签表示配置当前bean的属性
            name属性表示配置哪一个具体的属性
            ref属性表示参照哪一个bean
    -->
        <property name="bookDao" ref="bookDao"/>
    </bean>
```



#### 2.3.2 IOC、IOC容器、Bean、DI

1. ==IOC（Inversion of Control）控制反转==

(1)什么是控制反转呢？

* 使用对象时，由主动new产生对象转换为由==外部==提供对象，此过程中对象创建控制权由程序转移到外部，此思想称为控制反转。
  * 业务层要用数据层的类对象，以前是自己`new`的
  * 现在自己不new了，交给`别人[外部]`来创建对象
  * `别人[外部]`就反转控制了数据层对象的创建权
  * 这种思想就是控制反转
  * 别人[外部]指定是什么呢?继续往下学

(2)Spring和IOC之间的关系是什么呢?

* Spring技术对IOC思想进行了实现
* Spring提供了一个容器，称为==IOC容器==，用来充当IOC思想中的"外部"
* IOC思想中的`别人[外部]`指的就是Spring的IOC容器

(3)IOC容器的作用以及内部存放的是什么?

* IOC容器负责对象的创建、初始化等一系列工作，其中包含了数据层和业务层的类对象
* 被创建或被管理的对象在IOC容器中统称为==Bean==
* IOC容器中放的就是一个个的Bean对象

(4)当IOC容器中创建好service和dao对象后，程序能正确执行么?

* 不行，因为service运行需要依赖dao对象
* IOC容器中虽然有service和dao对象
* 但是service对象和dao对象没有任何关系
* 需要把dao对象交给service,也就是说要绑定service和dao对象之间的关系

像这种在容器中建立对象与对象之间的绑定关系就要用到DI:

2. ==DI（Dependency Injection）依赖注入==

![1629735078619](assets/1629735078619.png)

(1)什么是依赖注入呢?

* 在容器中建立bean与bean之间的依赖关系的整个过程，称为依赖注入
  * 业务层要用数据层的类对象，以前是自己`new`的
  * 现在自己不new了，靠`别人[外部其实指的就是IOC容器]`来给注入进来
  * 这种思想就是依赖注入

(2)IOC容器中哪些bean之间要建立依赖关系呢?

* 这个需要程序员根据业务需求提前建立好关系，如业务层需要依赖数据层，service就要和dao建立依赖关系

介绍完Spring的IOC和DI的概念后，我们会发现这两个概念的最终目标就是:==充分解耦==，具体实现靠:

* 使用IOC容器管理bean（IOC）
* 在IOC容器内将有依赖关系的bean进行关系绑定（DI）
* 最终结果为:使用对象时不仅可以直接从IOC容器中获取，并且获取到的bean已经绑定了所有的依赖关系.

#### 2.3.3 核心概念小结

这节比较重要，重点要理解`什么是IOC/DI思想`、`什么是IOC容器`和`什么是Bean`：

(1)什么IOC/DI思想?

* IOC:控制反转，控制反转的是对象的创建权
* DI:依赖注入，绑定对象与对象之间的依赖关系

(2)什么是IOC容器?

Spring创建了一个容器用来存放所创建的对象，这个容器就叫IOC容器

(3)什么是Bean?

容器中所存放的一个个对象就叫Bean或Bean对象


![Alt text](image-4.png)


1. Spring
2. SpringMVC
3. Maven高级
4. SpringBoot
5. MyBatisPlus


课程收获
基于SpringBoot实现基础SSM框架整合
掌握第三方技术与SpringBoot整合思想



Spring技术是JavaEE开发必备技能，企业开发技术选型命中率>90%
专业角度
简化开发，降低企业级开发的复杂性
框架整合，高效整合其他技术，提高企业级应用开发与运行效率


简化开发
Ioc
AOP
事务处理
框架整合
MyBatis
MyBatis-plus
Struts
Struts2
Hibernate


目标:充分解耦
使用IoC容器管理bean (IoC)
在IoC容器内将有依赖关系的bean进行关系绑定（DI）
最终效果
使用对象时不仅可以直接从IoC容器中获取，并且获取到的bean已经绑定了所有的依赖关系


<dependency>
<groupId>org.springframework</groupId>
<artifactId>spring-context</artifactId>
<version>5.2.10.RELEASE</version>
</dependency>

通过已有的项目进行SSM框架的学习。

SSM框架包括spring、springMVC、Mybatis等开发框架，但它们不能使项目进行前后端分离，简而言之就是，它们的用处是简化开发，并不是前后端分离开发。

其中spring的用法就是依赖注入和代码增强，代码增强用到的是AOP编程的思想。其中springMVC是spring框架中的一部分。Mybatis是一个持久层框架，用来简化DAO类的编写。
那么，这三个框架这么好用，那我们应该怎样从零开始使用这三个框架开发呢？

第一步，是用maven标准建立项目，然后先是按照数据库中的表的属性建立pojo类。第二步，是创建接口Mapper类，觉得相当于DAO的接口。第三步就是service类记得，先建立serviceimpl给服务类继承，记得分好包。然后就是建立controller类控制器，用于接收后台的数据返还给视图层展示，或接收前端数据请求，处理后交给后台对应方法执行。


当前我认识到了以下注解：
@Service    用于声明这是一个服务类
@Autowired    把东西自动装配进入所注释的类
@Controller    声明当前类是一个控制器
@RequestMapping    映射路径访问


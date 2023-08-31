# Redis笔记

​	Redis是一种键值型的NoSql数据库。其中**键值型**，是指Redis中存储的数据都是以key、value对的形式存储，而value的形式多种多样，可以是字符串、数值、甚至json。**NoSql**可以翻译做Not Only Sql（不仅仅是SQL），或者是No Sql（非Sql的）数据库。是相对于传统关系型数据库而言，有很大差异的一种特殊的数据库，因此也称之为**非关系型数据库**。

### 0.1	结构化与非结构化

​	传统关系型数据库是结构化数据，每一张表都有严格的约束信息：字段名、字段数据类型、字段约束等等信息，插入的数据必须遵守这些约束：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/4tUgFo6.png)



​	而NoSql则对数据库格式没有严格约束，往往形式松散，自由。

​	可以是键值型：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/GdqOSsj.png)

​	也可以是文档型：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/zBBQfcc.png)



​	甚至可以是图格式：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/zBnKxWf.png)



### 0.2	关联和非关联

​	传统数据库的表与表之间往往存在关联，例如外键：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/tXYSl5x.png)



​	而非关系型数据库不存在关联关系，要维护关系要么靠代码中的业务逻辑，要么靠数据之间的耦合：

```json
{
  id: 1,
  name: "张三",
  orders: [
    {
       id: 1,
       item: {
	 id: 10, title: "荣耀6", price: 4999
       }
    },
    {
       id: 2,
       item: {
	 id: 20, title: "小米11", price: 3999
       }
    }
  ]
}
```

​	此处要维护“张三”的订单与商品“荣耀”和“小米11”的关系，不得不冗余的将这两个商品保存在张三的订单文档中，不够优雅。还是建议用业务来维护关联关系。



### 0.3	查询方式

​	传统关系型数据库会基于Sql语句做查询，语法有统一标准；

​	而不同的非关系数据库查询语法差异极大，五花八门各种各样。

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/AzaHOTF.png)



### 0.4	事务

​	传统关系型数据库能满足事务ACID的原则。

![](../../../../../笔记/黑马笔记/Redis/01-入门篇/讲义/Redis注释版/assets/J1MqOJM.png)



​	而非关系型数据库往往不支持事务，或者不能严格保证ACID的特性，只能实现基本的一致性。



### 0.5	总结

​	除了上述四点以外，在存储方式、扩展性、查询性能上关系型与非关系型也都有着显著差异，总结如下：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/kZP40dQ.png)

- 存储方式
    - 关系型数据库基于磁盘进行存储，会有大量的磁盘IO，对性能有一定影响
    - 非关系型数据库，他们的操作更多的是依赖于内存来操作，内存的读写速度会非常快，性能自然会好一些

* 扩展性
    * 关系型数据库集群模式一般是主从，主从数据一致，起到数据备份的作用，称为垂直扩展。
    * 非关系型数据库可以将数据拆分，存储在不同机器上，可以保存海量数据，解决内存大小有限的问题。称为水平扩展。
    * 关系型数据库因为表之间存在关联关系，如果做水平扩展会给数据查询带来很多麻烦

## 1.0	关于Redis和下载安装

​	略

## 2.0	Redis中的命令和数据结构

​	通用指令是部分数据类型的，都可以使用的指令，常见的有：

- KEYS：查看符合模板的所有key
- DEL：删除一个指定的key
- EXISTS：判断key是否存在
- EXPIRE：给一个key设置有效期，有效期到期时该key会被自动删除
- TTL：查看一个KEY的剩余有效期

通过help [command] 可以查看一个命令的具体用法，例如：

```sh
# 查看keys命令的帮助信息：
127.0.0.1:6379> help keys

KEYS pattern
summary: Find all keys matching the given pattern
since: 1.0.0
group: generic
```



### 2.1	String类型

​	String类型，也就是字符串类型，是Redis中最简单的存储类型。

​	其value是字符串，不过根据字符串的格式不同，又可以分为3类：

- string：普通字符串
- int：整数类型，可以做自增、自减操作
- float：浮点类型，可以做自增、自减操作

不管是哪种格式，底层都是字节数组形式存储，只不过是编码方式不同。字符串类型的最大空间不能超过512m.

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/VZqpv73.png)



### 2.2	String的常见命令

String的常见命令有：

- SET：添加或者修改已经存在的一个String类型的键值对
- GET：根据key获取String类型的value
- MSET：批量添加多个String类型的键值对
- MGET：根据多个key获取多个String类型的value
- INCR：让一个整型的key自增1
- INCRBY:让一个整型的key自增并指定步长，例如：incrby num 2 让num值自增2
- INCRBYFLOAT：让一个浮点类型的数字自增并指定步长
- SETNX：添加一个String类型的键值对，前提是这个key不存在，否则不执行
- SETEX：添加一个String类型的键值对，并且指定有效期





### 2.3	Key结构

​	Redis没有类似MySQL中的Table的概念。

​	例如，需要存储用户、商品信息到redis，有一个用户id是1，有一个商品id恰好也是1，此时如果使用id作为key，那就会冲突了。

​	此时我们可以通过给key添加前缀加以区分，不过这个前缀不是随便加的，有一定的规范：

​	Redis的key允许有多个单词形成层级结构，多个单词之间用**':'**隔开，格式如下：

```
	项目名:业务名:类型:id
```

​	这个格式并非固定，也可以根据自己的需求来删除或添加词条。这样以来，我们就可以把==不同类型的数据区分开==了。从而避免了key的冲突问题。

​	例如我们的项目名称叫 heima，有user和product两种不同类型的数据，我们可以这样定义key：

- user相关的key：**heima:user:1**

- product相关的key：**heima:product:1**



如果Value是一个Java对象，例如一个User对象，则可以将对象序列化为JSON字符串后存储：

| **KEY**         | **VALUE**                                  |
| --------------- | ------------------------------------------ |
| heima:user:1    | {"id":1,  "name": "Jack", "age": 21}       |
| heima:product:1 | {"id":1,  "name": "小米11", "price": 4999} |

并且，在Redis的桌面客户端中，还会以相同前缀作为层级结构，让数据看起来层次分明，关系清晰：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/InWMfeD.png)



## 2.4	Hash类型

​	Hash类型，也叫散列，其value是一个**无序字典**，==类似于Java中的HashMap结构==。

​	String结构是将对象序列化为JSON字符串后存储，当需要修改对象某个字段时很不方便：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/x2zDBjf.png)



Hash结构可以将对象中的**==每个字段独立存储==**，可以针对单个字段做CRUD：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/VF2EPt0.png)

Hash的常见命令有：

- HSET key field value：添加或者修改hash类型key的field的值

- HGET key field：获取一个hash类型key的field的值

- HMSET：批量添加多个hash类型key的field的值

- HMGET：批量获取多个hash类型key的field的值

- HGETALL：获取一个hash类型的key中的所有的field和value
- HKEYS：获取一个hash类型的key中的所有的field
- HINCRBY:让一个hash类型key的字段值自增并指定步长
- HSETNX：添加一个hash类型的key的field值，前提是这个field不存在，否则不执行





## 2.4	List类型

​	Redis中的List类型与==Java中的LinkedList类似==，可以看做是一个**双向链表**结构。既可以支持正向检索和也可以支持反向检索。

特征也与LinkedList类似：

- 有序
- 元素可以重复
- **插入和删除快**
- **查询速度一般**

常用来存储一个有序数据，例如：朋友圈点赞列表，评论列表等。



List的常见命令有：

- LPUSH key element ... ：向列表左侧插入一个或多个元素
- LPOP key：移除并返回列表左侧的第一个元素，没有则返回nil
- RPUSH key element ... ：向列表右侧插入一个或多个元素
- RPOP key：移除并返回列表右侧的第一个元素
- LRANGE key star end：返回一段角标范围内的所有元素
- BLPOP和BRPOP：与LPOP和RPOP类似，只不过在没有元素时等待指定时间，而不是直接返回nil





## 2.5	Set类型

Redis的Set结构与Java中的HashSet类似，可以看做是一个value为null的HashMap。因为也==是一个hash表==，因此具备与HashSet类似的特征：

- 无序

- 元素不可重复

- **查找快**

- 支持**交集、并集、差集**等功能



Set的常见命令有：

- SADD key member ... ：向set中添加一个或多个元素
- SREM key member ... : 移除set中的指定元素
- SCARD key： 返回set中元素的个数
- SISMEMBER key member：判断一个元素是否存在于set中
- SMEMBERS：获取set中的所有元素
- SINTER key1 key2 ... ：求key1与key2的交集



例如两个集合：s1和s2:

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/ha8x86R.png)

​	求交集：`SINTER s1 s2`

​	求s1与s2的不同：`SDIFF s1 s2`

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/L9vTv2X.png)



## 2.6	SortedSet类型

​	Redis的SortedSet是一个可排序的set集合，与Java中的TreeSet有些类似，但底层数据结构却差别很大。SortedSet中的每一个元素都带有一个score属性，可以基于score属性对元素排序，底层的实现是一个跳表（SkipList）加 hash表。

​	SortedSet具备下列特性：

- 可排序
- 元素不重复
- 查询速度快

​	因为SortedSet的可排序特性，经常被用来实现排行榜这样的功能。



#### 2.6.1	SortedSet的常见命令有：

- ZADD key score member：添加一个或多个元素到sorted set ，如果已经存在则更新其score值
- ZREM key member：删除sorted set中的一个指定元素
- ZSCORE key member : 获取sorted set中的指定元素的score值
- ZRANK key member：获取sorted set 中的指定元素的排名
- ZCARD key：获取sorted set中的元素个数
- ZCOUNT key min max：统计score值在给定范围内的所有元素的个数
- ZINCRBY key increment member：让sorted set中的指定元素自增，步长为指定的increment值
- ZRANGE key min max：按照score排序后，获取指定排名范围内的元素
- ZRANGEBYSCORE key min max：按照score排序后，获取指定score范围内的元素
- ZDIFF、ZINTER、ZUNION：求差集、交集、并集+
- 

​	注意：所有的排名默认都是升序，如果要降序则在命令的Z后面添加REV即可，例如：

- **升序**获取sorted set 中的指定元素的排名：ZRANK key member

- **降序**获取sorted set 中的指定元素的排名：ZREVRANK key memeber

## 3.0	Redis的JavaAPI

​	JavaAPI也包含很多：

![image-20220609102817435](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20220609102817435-165735883948534.png)

​	标记为*的就是推荐使用的java客户端，包括：

- `Jedis`和`Lettuce`：这两个主要是提供了Redis命令对应的API，方便我们操作Redis，而SpringDataRedis又对这两种做了抽象和封装，因此我们后期会直接以**==SpringDataRedis==**来学习。
- Redisson：是在Redis基础上实现了分布式的可伸缩的java数据结构，例如Map、Queue等，而且支持跨进程的同步机制：Lock、Semaphore等待，比较适合用来实现特殊的功能需求。

### 3.1	Jedis下载

​	maven即可

### 3.2	连接池

​	Jedis本身是线程不安全的，并且频繁的创建和销毁连接会有性能损耗，因此我们推荐大家使用Jedis连接池代替Jedis的直连方式。

```java
package com.heima.jedis.util;

import redis.clients.jedis.*;

public class JedisConnectionFactory {

    private static JedisPool jedisPool;

    static {
        // 配置连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setMaxWaitMillis(1000);
        // 创建连接池对象，参数：连接池配置、服务端ip、服务端端口、超时时间、密码
        jedisPool = new JedisPool(poolConfig, "192.168.150.101", 6379, 1000, "123321");
    }

    public static Jedis getJedis(){
        return jedisPool.getResource();
    }
}
```

### 3.3	pringDataRedis客户端

SpringData是Spring中数据操作的模块，包含对各种数据库的集成，其中对Redis的集成模块就叫做SpringDataRedis。

- 提供了对不同Redis客户端的整合（Lettuce和Jedis）
- 提供了RedisTemplate统一API来操作Redis
- 支持Redis的发布订阅模型
- 支持Redis哨兵和Redis集群
- 支持基于Lettuce的响应式编程
- 支持基于JDK、JSON、字符串、Spring对象的数据序列化及反序列化
- 支持基于Redis的JDKCollection实现



​	SpringDataRedis中提供了RedisTemplate工具类，其中封装了各种对Redis的操作。并且将不同数据类型的操作API封装到了不同的类型中：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/UFlNIV0.png)



### 3.3	快速入门

SpringBoot已经提供了对SpringDataRedis的支持，使用非常简单。

#### 1）引入依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.5.7</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.heima</groupId>
    <artifactId>redis-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>redis-demo</name>
    <description>Demo project for Spring Boot</description>
    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <!--redis依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <!--common-pool-->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
        <!--Jackson依赖-->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```



#### 2）配置Redis

```yaml
spring:
  redis:
    host: 192.168.150.101
    port: 6379
    password: 123321
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: 100ms
```



#### 3）注入RedisTemplate

因为有了SpringBoot的自动装配，我们可以拿来就用：

```java
@SpringBootTest
class RedisStringTests {

    @Autowired
    private RedisTemplate redisTemplate;
}
```



### 3.4	自定义序列化

​	RedisTemplate可以接收==**任意Object作为值**==写入Redis：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/OEMcbuu.png)





​	只不过写入前会把Object序列化为**==字节形式==**，默认是采用JDK序列化，得到的结果是这样的：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/5FjtWk5.png)

​	缺点：

- 可读性差
- 内存占用较大





​	我们可以自定义RedisTemplate的序列化方式，代码如下：

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
        // 创建RedisTemplate对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置连接工厂
        template.setConnectionFactory(connectionFactory);
        // 创建JSON序列化工具
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = 
            							new GenericJackson2JsonRedisSerializer();
        // 设置Key的序列化
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置Value的序列化
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        // 返回
        return template;
    }
}
```



​	这里采用了JSON序列化来代替默认的JDK序列化方式。最终结果如图：

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/XOAq3cN.png)

​	整体可读性有了很大提升，并且能==**将Java对象自动的序列化为JSON字符串**==，并且==**查询时能自动把JSON反序列化为Java对象**==。不过，其中记录了序列化时对应的class名称，目的是为了查询时实现自动反序列化。这会带来**额外的内存开销**。



### 3.5	StringRedisTemplate

为了节省内存空间，我们可以不使用JSON序列化器来处理value，而是统一使用String序列化器，要求只能存储String类型的key和value。当需要存储Java对象时，手动完成对象的序列化和反序列化。

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/Ip9TKSY.png)

因为存入和读取时的序列化及反序列化都是我们自己实现的，SpringDataRedis就不会将class信息写入Redis了。



这种用法比较普遍，因此SpringDataRedis就提供了RedisTemplate的子类：StringRedisTemplate，它的key和value的序列化方式默认就是String方式。

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/zXH6Qn6.png)



省去了我们自定义RedisTemplate的序列化方式的步骤，而是直接使用：

```java
@Autowired
private StringRedisTemplate stringRedisTemplate;
// JSON序列化工具
private static final ObjectMapper mapper = new ObjectMapper();

@Test
void testSaveUser() throws JsonProcessingException {
    // 创建对象
    User user = new User("虎哥", 21);
    // 手动序列化
    String json = mapper.writeValueAsString(user);
    // 写入数据
    stringRedisTemplate.opsForValue().set("user:200", json);

    // 获取数据
    String jsonUser = stringRedisTemplate.opsForValue().get("user:200");
    // 手动反序列化
    User user1 = mapper.readValue(jsonUser, User.class);
    System.out.println("user1 = " + user1);
}

```

- 其实这里用了Jackson包的ObjectMapper()对象去序列化对象，用的也是其的方法反序列号。
- opsForValue()方法还有两个可选参数以设置存入数据的**==过期时间==**
    - 也可以单独调用stringRedisTemplare.expire方法设置有效期




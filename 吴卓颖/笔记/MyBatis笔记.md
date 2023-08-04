# MyBatis笔记

​	MyBatis 是一款优秀的==持久层框架==，用于简化 JDBC 开发。	

## 1.0	MyBatis(xml版),启动!

##### 	第一步:导入坐标

​	**第二步:创建logback.xml的配置文件和mybatis的配置文件 `mybatis-config.xml`,后者内容如下：**

​	分别配置好==数据库连接信息==和==sql映射文件==

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <typeAliases>
        <package name="com.itheima.pojo"/>
    </typeAliases>
    
    <!--
    environments：配置数据库连接环境信息。可以配置多个environment，通过default属性切换不同的environment
    -->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <!--数据库连接信息-->
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql:///mybatis?useSSL=false"/>
                <property name="username" value="root"/>
                <property name="password" value="1234"/>
            </dataSource>
        </environment>

        <environment id="test">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <!--数据库连接信息-->
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql:///mybatis?useSSL=false"/>
                <property name="username" value="root"/>
                <property name="password" value="1234"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
       <!--加载sql映射文件-->
       <mapper resource="UserMapper.xml"/>
    </mappers>
</configuration>
```

##### 	第三步:编写 SQL 映射文件 --> 统一管理sql语句，解决硬编码问题

* 在模块的 `resources` 目录下创建映射配置文件 `UserMapper.xml`，内容如下：

    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <!--namespace的名字可以先随便取-->
    <mapper namespace="test">
        <select id="selectAll" resultType="com.itheima.pojo.User">
            select * from tb_user;
        </select>
    </mapper>
    ```

* 编码

    * 在 `com.itheima.pojo` 包下创建 User类

        ```java
        public class User {
            private int id;
            private String username;
            private String password;
            private String gender;
            private String addr;
            
            //省略了 setter 和 getter
        }
        ```

- 编写使用类

​	获取sqlSessionFactory对象的代码相对固定。

```java
public class MyBatisDemo {

    public static void main(String[] args) throws IOException {
        //1. 加载mybatis的核心配置文件，获取 SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        //2. 获取SqlSession对象，用它来执行sql
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //3. 执行sql
        List<User> users = sqlSession.selectList("test.selectAll"); //参数是一个字符串，该字符串必须是映射配置文件的namespace.id
        System.out.println(users);
        //4. 释放资源
        sqlSession.close();
    }
}
```

## 2.0	Mapper代理开发

​	上述代码也存在硬编码的问题，如下：

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210726214648112.png" alt="image-20210726214648112" style="zoom:80%;" />

​	这里调用 `selectList()` 方法传递的参数是映射配置文件中的 namespace.id值。这样写也不便于后期的维护。如果使用 Mapper 代理方式（如下图）则不存在硬编码问题。

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210726214636108.png" alt="image-20210726214636108" style="zoom:80%;" />

​	通过上面的描述可以看出 Mapper 代理方式的目的：

* 解决原生方式中的硬编码
* 简化后期执行SQL

### 2.1	使用Mapper代理要求

​	使用Mapper代理方式，必须满足以下要求：

* 定义与==SQL映射文件同名的Mapper接口==，并且将Mapper接口和SQL映射文件放置在同一目录下。如下图：

    ![image-20210726215946951](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210726215946951.png)

* 设置SQL映射文件的namespace属性为==Mapper接口全限定名==

    <img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210726220053883.png" alt="image-20210726220053883" style="zoom:80%;" />

* 在 Mapper 接口中定义方法，==方法名==就是SQL映射文件中==sql语句的id==，并==保持参数类型和返回值类型一致==

    <img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210726223216517.png" alt="image-20210726223216517" style="zoom:70%;" />

#### 2.1.1	具体实现

* 在 `com.itheima.mapper` 包下创建 UserMapper接口，代码如下：

    ```java
    public interface UserMapper {
        List<User> selectAll();
        User selectById(int id);
    }
    ```

* 在 `resources` 下创建 `com/itheima/mapper` 目录，并在该目录下创建 UserMapper.xml 映射配置文件

    ```xml
    <!--
        namespace:名称空间。必须是对应接口的全限定名
    -->
    <mapper namespace="com.itheima.mapper.UserMapper">
        <select id="selectAll" resultType="com.itheima.pojo.User">
            select *
            from tb_user;
        </select>
    </mapper>
    ```

## 2.0	类型别名

​	在映射配置文件中的 `resultType` 属性需要配置数据封装的类型（类的全限定名）。而每次这样写是特别麻烦的，Mybatis 提供了 `类型别名`(typeAliases) 可以简化这部分的书写。

​	首先需要现在核心配置文件中配置类型别名，也就意味着给pojo包下所有的类起了别名==（别名就是类名）==，如例中`user`。不区分大小写。内容如下：

```xml
<typeAliases>
    <!--name属性的值是实体类所在包-->
    <package name="com.itheima.pojo"/> 
</typeAliases>
```

通过上述的配置，我们就可以简化映射配置文件中 `resultType` 属性值的编写

```xml
<mapper namespace="com.itheima.mapper.UserMapper">
    <select id="selectAll" resultType="user">
        select * from tb_user;
    </select>
</mapper>
```



## 3.0	参数占位符和多条件查询

​	mybatis提供了两种参数占位符：

* #{} ：执行SQL时，会将 #{} 占位符替换为？，将来自动设置参数值。从上述例子可以看出使用#{} 底层使用的是 `PreparedStatement`

* ${} ：拼接SQL。底层使用的是 `Statement`，会存在SQL注入问题。如下图将 映射配置文件中的 #{} 替换成 ${} 来看效果

    ```xml
    <select id="selectById"  resultMap="brandResultMap">
        select *
        from tb_brand where id = ${id};
    </select>
    ```

    重新运行查看结果如下：

    <img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729184156019.png" alt="image-20210729184156019" style="zoom:70%;" />

> ==注意：==从上面两个例子可以看出，以后开发我们使用 #{} 参数占位符。

### 3.1	parameterType使用

​	对于有参数的mapper接口方法，我们在映射配置文件中应该配置 `ParameterType` 来指定参数类型。只不过该属性都可以省略。如下图：

```xml
<select id="selectById" parameterType="int" resultMap="brandResultMap">
    select *
    from tb_brand where id = ${id};
</select>
```

### 3.2	SQL语句中特殊字段处理

​	以后肯定会在SQL语句中写一下特殊字符，比如某一个字段大于某个值，如下图

<img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729184756094.png" alt="image-20210729184756094" style="zoom:80%;" />

​	可以看出报错了，因为映射配置文件是xml类型的问题，而 > < 等这些字符在xml中有特殊含义，所以此时我们需要将这些符号进行转义，可以使用以下两种方式进行转义

* 转义字符

    下图的 `&lt;` 就是 `<` 的转义字符。

    <img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729185128686.png" alt="image-20210729185128686" style="zoom:60%;" />

* <![CDATA[内容]]> 输入cd即可自动补全

    <img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729185030318.png" alt="image-20210729185030318" style="zoom:60%;" />

### 3.3	多条件查询的处理方法

​	定义多条件查询的方法时，我们就需要考虑定义接口时，参数应该如何定义。Mybatis针对多参数有多种实现

* 使用 ==`@Param("参数名称")` 标记每一个参数==，在映射配置文件中就需要使用 `#{参数名称}` 进行占位

    ```java
    List<Brand> selectByCondition(@Param("status") int status, @Param("companyName") String companyName,@Param("brandName") String brandName);
    ```

* 将==多个参数封装成一个 实体对象== ，将该实体对象作为接口的方法参数。该方式要求在映射配置文件的SQL中使用 `#{内容}` 时，里面的==内容必须和实体类属性名==保持一致。

    ```java
    List<Brand> selectByCondition(Brand brand);
    ```

* 将==多个参数封装到map集合==中，将map集合作为接口的方法参数。该方式要求在映射配置文件的SQL中使用 `#{内容}` 时，里面的==内容必须和map集合中键的名称==一致。

    ```
    List<Brand> selectByCondition(Map map);
    ```

​	==**注意:**==在 `BrandMapper.xml` 映射配置文件中编写 `statement`，使用 `resultMap` 而不是使用 `resultType`

```xml
<select id="selectByCondition" resultMap="brandResultMap">
    select *
    from tb_brand
    where status = #{status}
    and company_name like #{companyName}
    and brand_name like #{brandName}
</select>
```

## 4.0	动态SQL

​	用户在输入条件时，可能不会所有的条件都填写。我们需要对此进行处理。

​	针对上述的需要，Mybatis对动态SQL有很强大的支撑：

> * if
>
> * choose (when, otherwise)
>
> * trim (where, set)
>
> * foreach

​	先学习 if 标签和 where 标签：

* **if 标签**：条件判断

    * test 属性：逻辑表达式

    ```xml
    <select id="selectByCondition" resultMap="brandResultMap">
        select *
        from tb_brand
        where
            <if test="status != null">
                and status = #{status}
            </if>
            <if test="companyName != null and companyName != '' ">
                and company_name like #{companyName}
            </if>
            <if test="brandName != null and brandName != '' ">
                and brand_name like #{brandName}
            </if>
    </select>
    ```

    但此语句有问题，`and`关键字有可能直接跟在`where`后面，此时就要用==where标签==

​	**where 标签**

* 作用：
    * 替换where关键字
    * 会动态的去掉第一个条件前的 and 
    * 如果所有的参数没有值则不加where关键字

```xml
<select id="selectByCondition" resultMap="brandResultMap">
    select *
    from tb_brand
    <where>
        <if test="status != null">
            and status = #{status}
        </if>
        <if test="companyName != null and companyName != '' ">
            and company_name like #{companyName}
        </if>
        <if test="brandName != null and brandName != '' ">
            and brand_name like #{brandName}
        </if>
    </where>
</select>
```

> 注意：需要给每个条件前都加上 and 关键字。

## 5.0	update修改和insert插入和delete删除

### 5.1	insert

* 编写接口方法

    <img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729215351651.png" alt="image-20210729215351651" style="zoom:80%;" />

    参数：除了id之外的所有的数据。id对应的是表中主键值，而主键我们是 ==自动增长== 生成的。

* 编写SQL语句

    <img src="https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20210729215537167.png" alt="image-20210729215537167" style="zoom:80%;" />

#### 5.11	主键返回

​	在数据添加成功后，有时候需要获取插入数据库数据的主键（主键是自增长）。此时只需要在 insert 标签上添加如下属性：

* useGeneratedKeys：是够获取自动增长的主键值。true表示获取
* keyProperty  ：指定将获取到的主键值封装到哪个属性里

```xml
<insert id="add" useGeneratedKeys="true" keyProperty="id">
    insert into tb_brand (brand_name, company_name, ordered, description, status)
    values (#{brandName}, #{companyName}, #{ordered}, #{description}, #{status});
</insert>
```

### 5.2	update

​	**set标签**: set标签可以用于动态包含需要更新的列，忽略其它不更新的列。

```xml
<update id="update">
    update tb_brand
    <set>
        <if test="brandName != null and brandName != ''">
            brand_name = #{brandName},
        </if>
        <if test="companyName != null and companyName != ''">
            company_name = #{companyName},
        </if>
        <if test="ordered != null">
            ordered = #{ordered},
        </if>
        <if test="description != null and description != ''">
            description = #{description},
        </if>
        <if test="status != null">
            status = #{status}
        </if>
    </set>
    where id = #{id};
</update>
```

​	brandMapper接口中定义修改方法:`void update(Brand brand);`

### 5.3	delete

​	这里只讲批量删除。首先在 `BrandMapper` 接口中定义删除多行数据的方法。参数是一个**数组**，数组中存储的是多条数据的id

```java
/**
  * 批量删除
  */
void deleteByIds(int[] ids);
```

​	**foreach 标签**

​	用来迭代任何可迭代的对象（如数组，集合）。

* collection 属性：
    * mybatis会将数组参数，封装为一个Map集合。
        * 默认：array ,即数组
        * 使用@Param注解改变map集合的默认key的名称
* item 属性：本次迭代获取到的元素。
* separator 属性：集合项迭代之间的分隔符。`foreach` 标签不会错误地添加多余的分隔符。也就是最后一次迭代不会加分隔符。
* open 属性：该属性值是在拼接SQL语句之前拼接的语句，只会拼接一次
* close 属性：该属性值是在拼接SQL语句拼接后拼接的语句，只会拼接一次

```xml
<delete id="deleteByIds">
    delete from tb_brand where id
    in
    <foreach collection="array" item="id" separator="," open="(" close=")">
        #{id}
    </foreach>
    ;
</delete>
```

> 假如数组中的id数据是{1,2,3}，那么拼接后的sql语句就是：
>
> ```sql
> delete from tb_brand where id in (1,2,3);
> ```

## 6.0	Mybatis的参数传递

​	Mybatis 接口方法中可以接收各种各样的参数，如下：

* 多个参数
* 单个参数：单个参数又可以是如下类型
    * POJO 类型
    * Map 集合类型
    * Collection 集合类型
    * List 集合类型
    * Array 类型
    * 其他类型

​	Mybatis底层会将方法形参打包成一个Map集合

* POJO 类型

    直接使用。要求 `属性名` 和 `参数占位符名称` 一致

* Map 集合类型

    直接使用。要求 `map集合的键名` 和 `参数占位符名称` 一致

* Collection 集合类型

    Mybatis 会将集合封装到 map 集合中，如下：

    > map.put("arg0"，collection集合);
    >
    > map.put("collection"，collection集合;

    ==可以使用 `@Param` 注解替换map集合中默认的 arg 键名。==

* List 集合类型

    Mybatis 会将集合封装到 map 集合中，如下：

    > map.put("arg0"，list集合);
    >
    > map.put("collection"，list集合);
    >
    > map.put("list"，list集合);

    ==可以使用 `@Param` 注解替换map集合中默认的 arg 键名。==

* Array 类型

    Mybatis 会将集合封装到 map 集合中，如下：

    > map.put("arg0"，数组);
    >
    > map.put("array"，数组);

    ==可以使用 `@Param` 注解替换map集合中默认的 arg 键名。==

* 其他类型

    比如int类型，`参数占位符名称` 叫什么都可以。尽量做到见名知意

## 7.0	注解实现CRUD

​	使用注解开发会比配置文件开发更加方便。如下就是使用注解进行开发

```java
@Select(value = "select * from tb_user where id = #{id}")
public User select(int id);
```

> ==注意：==
>
> * 注解是用来替换映射配置文件方式配置的，所以使用了注解，就不需要再映射配置文件中书写对应的 `statement`

​	Mybatis 针对 CURD 操作都提供了对应的注解，已经做到见名知意。如下：

* 查询 ：@Select
* 添加 ：@Insert
* 修改 ：@Update
* 删除 ：@Delete

​	但是注解无法实现动态SQL那种复杂的功能，所以，==注解完成简单功能，配置文件完成复杂功能。==
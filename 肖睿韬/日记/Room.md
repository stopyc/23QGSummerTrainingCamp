# Room

导依赖   implementation "androidx.room:room-runtime:2.5.2"
              annotationProcessor 'androidx.room:room-compiler:2.5.2'

#### 表的列

默认情况下，Room 将类名称用作数据库表名称。如果您希望表具有不同的名称，请设置 @Entity 注解的 tableName 属性。
同样，Room 默认使用字段名称作为数据库中的列名称。如果您希望列具有不同的名称，请将 @ColumnInfo 注解添加到该字段并设置 name 属性。
每个 Room 实体都必须定义一个主键，用于唯一标识相应数据库表中的每一行。执行此操作的最直接方式是使用 @PrimaryKey 为单个列添加注解（注意：如果您需要 Room 为实体实例分配自动 ID，请将 @PrimaryKey 的 autoGenerate 属性设为 true。）
默认情况下，Room 会为实体中定义的每个字段创建一个列。 如果某个实体中有您不想保留的字段，则可以使用 @Ignore 为这些字段添加注解，

```java
package com.example.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "mAge")
    public int age;
    public String name;
    public String phone;
}

////Ignore亦可注解字段，让Room忽略此方法或者字段
    //由于Room只能识别一个构造器，如果需要定义多个构造器，可以使用Ignore注解让Room忽略这个构造器
    //Room不会持久化被注解Ignore标记过的字段

```



#### DAO

当您使用 Room 持久性库存储应用的数据时，您可以通过定义数据访问对象 (DAO) 与存储的数据进行交互。每个 DAO 都包含一些方法，这些方法提供对应用数据库的抽象访问权限。在编译时，Room 会自动为您定义的 DAO 生成实现。您可以将每个 DAO 定义为一个接口或一个抽象类。对于基本用例，您通常应使用接口。无论是哪种情况，您都必须始终使用 @Dao 为您的 DAO 添加注解。DAO 不具有属性，但它们定义了一个或多个方法，可用于与应用数据库中的数据进行交互。

```java
package com.example.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertAll(User user);

    @Delete
    void deleteAll(User user);

    @Query("select * from users")
    List<User> getAll();
}
```



#### Database

以下代码定义了用于保存数据库的 AppDatabase 类。 AppDatabase 定义数据库配置，并作为应用对持久性数据的主要访问点。数据库类必须满足以下条件：

该类必须带有 @Database 注解，该注解包含列出所有与数据库关联的数据实体的 entities 数组。
该类必须是一个抽象类，用于扩展 RoomDatabase。
对于与数据库关联的每个 DAO 类，数据库类必须定义一个具有零参数的抽象方法，并返回 DAO 类的实例。

如果您的应用在单个进程中运行，在实例化 AppDatabase 对象时应遵循单例设计模式。

```java
package com.example.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = User.class,version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();
}
```

#### Activity

```java
UserDatabase userDatabase = Room.databaseBuilder(MainActivity.this, UserDatabase.class, "Mytest.db")
        .allowMainThreadQueries()
        .build();
 				//是否允许在主线程进行查询
                .allowMainThreadQueries()
                //数据库创建和打开后的回调
                //.addCallback()
                //设置查询的线程池
                //.setQueryExecutor()
                //.openHelperFactory()
                //room的日志模式
                //.setJournalMode()
                //数据库升级异常之后的回滚
                //.fallbackToDestructiveMigration()
                //数据库升级异常后根据指定版本进行回滚
                //.fallbackToDestructiveMigrationFrom()
                // .addMigrations(CacheDatabase.sMigration)


```







多个数据除主键都一样，则要修改数据要指定主键

#### 增

如果 @Insert 方法接收单个参数，则会返回 long 值，这是插入项的新 rowId。如果参数是数组或集合，则该方法应改为返回由 long 值组成的数组或集合，并且每个值都作为其中一个插入项的 rowId。

```
User jay = new User(18, "jay", "110");
userDao.insertAll(jay);
```

```
@Dao
public interface UserDao {
//onConflict = OnConflictStrategy.REPLACE：表示在插入数据时发生冲突时采取替换策略。即如果插入的数据与已存在的数据发生冲突（例如主键冲突），则替换已存在的数据。

这个注解可以应用于Room DAO（数据访问对象）中的插入方法，用于指定插入数据时的冲突处理策略。其他的冲突处理策略还包括OnConflictStrategy.IGNORE（忽略冲突，不进行插入操作）和OnConflictStrategy.ABORT（终止插入操作并抛出异常）等。
	返回值自己设
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUsers(User... users);

    @Insert
    public void insertBothUsers(User user1, User user2);

    @Insert
    public void insertUsersAndFriends(User user, List<User> friends);
}

```

#### 删

```java
jay.id = 2;
userDao.deleteAll(jay);
```

```java
@Dao
public interface UserDao {
    @Delete
    public void deleteUsers(User... users);
}
```

#### 改

```java
jay.id = 3;
userDao.updateUser(jay);
```

```java
@Dao
public interface UserDao {
    @Update
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateUsers(User... users);
}
```

#### 查

```java
userList = userDao.getAll();
System.out.println(userList.size());
```

```java
* 注意，冒号后面必须紧跟参数名，中间不能有空格。大于小于号和冒号中间是有空格的。
     * select *from cache where【表中列名】 =:【参数名】------>等于
     * where 【表中列名】 < :【参数名】 小于
     * where 【表中列名】 between :【参数名1】 and :【参数2】------->这个区间
     * where 【表中列名】like :参数名----->模糊查询
     * where 【表中列名】 in (:【参数名集合】)---->查询符合集合内指定字段值的记录




@Query("SELECT * FROM users where id= :userId")
    List<User> findUserById(int userId);
    
    //根据name查询 users 表，将参数集合传递给查询
    @Query("SELECT * FROM users WHERE name IN (:usernames)")
    List<User> loadAllByNames(int[] usernames);

//将简单参数传递给查询
    @Query("SELECT * FROM users WHERE age > :minAge")
    public User[] loadAllUsersOlderThan(int minAge);
```



#### 数据库迁移

创建一个migration对象：

```
static Migration sMigration = new Migration(1, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("alter table teacher rename to student");
            database.execSQL("alter table teacher add column teacher_age INTEGER NOT NULL default 0");
        }
    };
    
    Activity
    Room.databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, "ppjoke_cache")
                .addMigrations(CacheDatabase.sMigration)
                .build();
```


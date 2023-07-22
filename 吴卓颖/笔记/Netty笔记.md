# Netty笔记

## 0.0	Netty是什么?

​	Netty 是一个异步的、基于事件驱动的网络应用框架，用于快速开发可维护、高性能的网络服务器和客户端。

​	BIO的处理流程

![](https://img-blog.csdnimg.cn/img_convert/717924ba2ac191144fa37ed0d2939f90.png)

​	基于NIO的Netty的处理流程

![](https://img-blog.csdnimg.cn/img_convert/97711a9d9b7d4927858ad935d574da26.png)

### 0.1	Netty 的优势

* Netty vs NIO，工作量大，bug 多
    * 如果使用NIO开发:
        * 需要自己构建协议
        * 解决 TCP 传输问题，如粘包、半包
        * epoll 空轮询导致 CPU 100%
    * NIO对 API 进行增强，使之更易用，如 FastThreadLocal => ThreadLocal，ByteBuf => ByteBuffer
* Netty vs 其它网络应用框架
    * Mina 由 apache 维护，将来 3.x 版本可能会有较大重构，破坏 API 向下兼容性，Netty 的开发迭代更迅速，API 更简洁、文档更优秀
    * 久经考验，16年，Netty 版本
        * 2.x 2004
        * 3.x 2008
        * 4.x 2013
        * 5.x 已废弃（没有明显的性能提升，维护成本高）

## 1.0	Netty,启动!

​	**目标**

​	开发一个简单的服务器端和客户端

* 客户端向服务器端发送 hello, world
* 服务器仅接收，不返回

### 1.1	加入依赖

```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.39.Final</version>
</dependency>
```

### 1.2	服务器端

```java
new ServerBootstrap()
    .group(new NioEventLoopGroup()) // 1 创建 NioEventLoopGroup，可以简单理解为 线程池 + Selector
    .channel(NioServerSocketChannel.class) // 2 选择服务 Scoket 实现类，其中 NioServerSocketChannel 表示基于 NIO 的服务器端实现
    .childHandler(new ChannelInitializer<NioSocketChannel>() { // 3 之所以方法叫 childHandler，是接下来添加的处理器都是给SocketChannel用的，而不是给 ServerSocketChannel.ChannelInitializer处理器（仅执行一次），它的作用是待客户端 SocketChannel 建立连接后，执行 initChannel 以便添加更多的处理器
        protected void initChannel(NioSocketChannel ch) {
            ch.pipeline().addLast(new StringDecoder()); // 5 SocketChannel 的处理器，解码 ByteBuf => String
            ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() { // 6 SocketChannel 的业务处理器，使用上一个处理器的处理结果
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                    System.out.println(msg);
                }
            });
        }
    })
    .bind(8080); // 4  ServerSocketChannel 绑定的监听端口


```

​	代码解读

* 1 处，创建 NioEventLoopGroup，可以简单理解为 `线程池 + Selector` 后面会详细展开

* 2 处，选择服务 Scoket 实现类，其中 NioServerSocketChannel 表示基于 NIO 的服务器端实现，其它实现还有

    ![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/0006.png)

* 3 处，之所以方法叫 `childHandler`，是接下来添加的处理器都是给` SocketChannel `用的，而**不**是给 `ServerSocketChannel`。`ChannelInitializer` 处理器（仅执行一次），它的作用是待客户端 `SocketChannel `建立连接后，执行` initChannel 以`便添加更多的处理器

* 4 处，ServerSocketChannel 绑定的监听端口

* 5 处，SocketChannel 的处理器，解码 ByteBuf => String

* 6 处，SocketChannel 的业务处理器，使用上一个处理器的处理结果

### 1.3	客户端

```java
new Bootstrap()
    .group(new NioEventLoopGroup()) // 1 创建 NioEventLoopGroup，同 Server
    .channel(NioSocketChannel.class) // 2 选择客户 Socket 实现类，NioSocketChannel 表示基于 NIO 的客户端实现
    .handler(new ChannelInitializer<Channel>() { // 3 添加 SocketChannel 的处理器，ChannelInitializer 处理器（仅执行一次）
        @Override
        protected void initChannel(Channel ch) {
            ch.pipeline().addLast(new StringEncoder()); // 8 消息会经过通道 handler 处理，这里是将 String => ByteBuf 发出
        }
    })
    .connect("127.0.0.1", 8080) // 4 指定要连接的服务器和端口
    .sync() // 5 Netty 中很多方法都是异步的，如 connect，这时需要使用 sync 方法等待 connect 建立连接完毕
    .channel() // 6 获取 channel 对象，它即为通道抽象，可以进行数据读写操作
    .writeAndFlush(new Date() + ": hello world!"); // 7 写入消息并清空缓冲区
```

代码解读

* 1 处，创建 NioEventLoopGroup，同 Server

* 2 处，选择客户 Socket 实现类，NioSocketChannel 表示基于 NIO 的客户端实现，其它实现还有

    ![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/0007.png)

* 3 处，添加 SocketChannel 的处理器，ChannelInitializer 处理器（仅执行一次），它的作用是待客户端 SocketChannel 建立连接后，执行 initChannel 以便添加更多的处理器

* 4 处，指定要连接的服务器和端口

* 5 处，Netty 中很多方法都是异步的，如 connect，这时需要使用 sync 方法等待 connect 建立连接完毕

* 6 处，获取 channel 对象，它即为通道抽象，可以进行数据读写操作

* 7 处，写入消息并清空缓冲区

* 8 处，消息会经过通道 handler 处理，这里是将 String => ByteBuf 发出

* 数据经过网络传输，到达服务器端，服务器端 5 和 6 处的 handler 先后被触发，走完一个流程

### 1.4	流程梳理

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/0040.png)

#### 💡 提示

需要树立正确的的理解

* 把` channel `理解为数据的通道
* 把 msg 理解为流动的数据，最开始输入是 ByteBuf，但经过 pipeline 的加工，会变成其它类型对象，最后输出又变成 ByteBuf
* 把 handler 理解为数据的处理工序
    * 工序有多道，合在一起就是 pipeline，pipeline 负责发布事件（读、读取完成...）传播给每个 handler， handler 对自己感兴趣的事件进行处理（重写了相应事件处理方法）
    * handler 分 Inbound 和 Outbound 两类
* 把 eventLoop 理解为处理数据的工人
    * 工人可以管理多个 channel 的 io 操作，并且一旦工人负责了某个 channel，就要负责到底（绑定）
    * 工人既可以执行 io 操作，也可以进行任务处理，每位工人有任务队列，队列里可以堆放多个 channel 的待处理任务，任务分为普通任务、定时任务
    * 工人按照 pipeline 顺序，依次按照 handler 的规划（代码）处理数据，可以为每道工序指定不同的工人

## 2.0	Netty组件

### 2.1	EventLoop事件循环对象

​	EventLoop 本质是一个单线程执行器（同时维护了一个 Selector），里面有 run 方法处理 Channel 上源源不断的 io 事件。

​	它的继承关系比较复杂:

* 一条线是继承自 j.u.c.ScheduledExecutorService 因此包含了线程池中所有的方法
* 另一条线是继承自 netty 自己的 OrderedEventExecutor，
    * 提供了 boolean inEventLoop(Thread thread) 方法判断一个线程是否属于此 EventLoop
    * 提供了 parent 方法来看看自己属于哪个 EventLoopGroup



​	![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200318043130005-146174877.png)



### 2.2	EventLoopGroup事件循环组

​	EventLoopGroup 是一个多线程的事件循环组，它可以包含一个或多个 EventLoop，并为它们分配 Channel。EventLoop 和 EventLoopGroup 的关系如下图所示。Channel 一般会调用 ==EventLoopGroup 的 register== 方法来绑定其中一个 ==**EventLoop**==，后续这个 Channel 上的 io 事件==都由此 EventLoop 来处理==（保证了 io 事件处理时的线程安全）

* ​	EventLoopGroup继承自 netty 自己的 EventExecutorGroup
    * 实现了 Iterable 接口提供遍历 EventLoop 的能力
    * 另有 next 方法获取集合中下一个 EventLoop

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200318045029762-366045920.png)

​	　关于EventLoop以及EventLoopGroup的映射关系为：

- 一个EventLoopGroup 包含一个或者多个EventLoop；
- ==一个EventLoop== 在它的生命周期内==只和一个Thread 绑定；==
- 所有由EventLoop 处理的I/O 事件都==将在它专有的Thread 上被处理==；
- ==一个Channel== 在它的生命周期内==只注册于一个EventLoop==；
- ==一个EventLoop== 可能会被分配给==一个或多个Channel==。

​	EventLoop进行的是**`Selector`**的维护

​	![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200318045454621-632746501.png)

​	EventLoopGroup用于**`线程组维护，并发控制，任务处理`**。

​	![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200318045517798-166353930.png)



#### 2.21	NioEventLoopGroup实现

​	NioEventLoopGroup对象可以理解为一个线程池，内部维护了一组线程，每个线程负责处理多个Channel上的事件，而一个Channel只对应于一个线程，这样可以回避多线程下的数据同步问题。如下例子代码

```java
　　　　　// 服务器端应用程序使用两个NioEventLoopGroup创建两个EventLoop的组，EventLoop这个相当于一个处理线程，是Netty接收请求和处理IO请求的线程。
        // 主线程组, 用于接受客户端的连接，但是不做任何处理，跟老板一样，不做事
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 从线程组, 当boss接受连接并注册被接受的连接到worker时，处理被接受连接的流量。
        EventLoopGroup workerGroup = new NioEventLoopGroup();
```

NioEventLoopGroup其职责如下:

- 作为服务端 `Acceptor线程`时，负责处理客户端的请求接入。
- 作为客户端 `Connector线程`时，负责注册监听连接操作位，用于判断异步连接结果。
- 作为 `IO 线程`时，监听网络读操作位，负责从 SocketChannel 中读取报文。
- 作为 `IO `线程时，负责向 SocketChannel 写入报文发送给对方，如果发生写半包，会自动注册监听写事件，用 于后续继续发送半包数据，直到数据全部发送完成。
- 作为`定时任务线程`时，可以执行定时任务，例如链路空闲检测和发送心跳消息等。
- 作为线程执行器时可以执行普通的任务线程（Runnable）。

#### 2.22	观察NioEventLoopGroup构造器原码

​	得出结论:

1. `NioEventLoopGroup`初始化时未指定线程数，那么会使用默认线程数，即 `线程数 = CPU核心数 * 2`；
2. 每个NioEventLoopGroup对象内部都有一组可执行的`NioEventLoop数组`，其大小是 nThreads, 这样就构成了一个线程池， `一个NIOEventLoop可以理解成就是一个线程`。
3. 所有的`NioEventLoop线程`是==使用相同的 **executor**、**SelectorProvider**、**SelectStrategyFactory**、**RejectedExecutionHandler**以及是属于某一个**NioEventLoopGroup**==的。这一点从 newChild(executor, args); 方法就可以看出：newChild()的实现是在NIOEventLoopGroup中实现的。
4. 当有IO事件来时，需要从线程池中选择一个线程出来执行，这时候的`NioEventLoop`选择策略是由==GenericEventExecutorChooser==实现的，并调用该类==的next()方法==。
5.  每个==**NioEventLoopGroup**==对象都有一个==**NioEventLoop选择器(Selector)**==与之对应，其会根据==`NioEventLoop`的个数==，动态选择chooser（如果是2的幂次方，则按位运算，否则使用普通的轮询）

​	　综上所述，得出NioEventLoopGroup主要功能就是为了创建一定数量的NioEventLoop，而真正的重点就在NioEventLoop中，它是整个netty线程执行的关键。



#### 2.23	💡 优雅关闭方法shutdownGracefully

​	 `shutdownGracefully()` 方法。该方法会首先切换 `EventLoopGroup` 到关闭状态从而拒绝新的任务的加入，然后在任务队列的任务都处理完成后，停止线程的运行。从而确保整体应用是在正常有序的状态下退出的。



### 2.3	核心组件AbstractBootstrap、Bootstrap、ServerBootstrap

​	Netty的核心组件的设计都很模块化，如果想要实现一个应用程序，就需要将这些组件组装到一起。

​	Netty通过**==Bootstrap类==**对一个Netty应用程序进行配置（组装各个组件），并最终使它运行起来。对于客户端程序和服务器程序所使用到的Bootstrap类是**不同**的，后者需要使用ServerBootstrap，这样设计是因为，在如TCP这样有连接的协议中，服务器程序往往需要一个以上的Channel，通过父Channel来接受来自客户端的连接，然后创建子Channel用于它们之间的通信，而像UDP这样无连接的协议，它不需要每个连接都创建子Channel，只需要一个Channel即可。

　　一个比较明显的差异就是Bootstrap与ServerBootstrap的group()方法，后者提供了一个接收2个EventLoopGroup的版本。

|                       | Bootstrap            | ServerBootstrap    |
| --------------------- | -------------------- | ------------------ |
| 网络编程中的作用      | 连接到远程主机和端口 | 绑定到一个本地端口 |
| EventLoopGroup 的数目 | 1                    | 2                  |

### 2.31	AbstractBootstrap类

​	AbstractBootstrap是一个工具类，用于服务器通道的一系列配置，==**绑定NioEventLoopGroup线程组**==、==**指定NIO的模式**==、==**指定子处理器**==，==**用于处理workerGroup**==、==**指定端口**==等。其有`ServerBootstrap`、`Bootstrap`两个具体的实现。

​	总的来说:

1. 提供了一个**ChannelFactory**对象用来创建Channel,一==个Channel会对应一个EventLoop==用于IO的事件处理，在**一个Channel**的整个生命周期中**只会绑定一个EventLoop**,这里可理解给Channel分配一个线程进行IO事件处理，结束后回收该线程。
2. AbstractBootstrap没有提供EventLoop而是提供了一个EventLoopGroup，上文讲过EventLoopGroup对象就是一个含有EventLoop的数组。  但是==当一个连接到达==，==Netty会注册一个Channel==，然后EventLoopGroup会==分配一个EventLoop绑定到这个channel==。

3. 不管是**服务器**还是**客户端的Channel**都需要绑定一个本地端口这就有了**SocketAddress类**的对象**localAddress**。

4. Channel有很多选项，所以有了options对象LinkedHashMap<channeloption<?>, Object>

5. 一个事件处理器==**ChannelHandler对象**==可以被创建用来处理**==Channel的IO事件==**

### 2.32	Bootstrap类

​	Bootstrap 是 Netty 提供的一个便利的工厂类, 我们可以通过它来完成客户端或服务器端的 Netty 初始化。

​	`Bootstrap`: 用于客户端，==**只需要一个单独的Channel**==，来与服务端进行数据交互，对应server端的子Channel。

​	`作用职责`:**==EventLoop初始化==**, **==channel的注册过程==**, **==关于pipeline的初始化==**, **==handler的添加过程==**, **==客户端连接分析==**。

```java
public class SimpleNettyClient {

    public void connect(String host, int port) throws Exception {
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            // 客户端启动类程序
            Bootstrap bootstrap = new Bootstrap();
            /**
             *EventLoop的组
             */
            bootstrap.group(worker);
            /**
             * 用于构造socketchannel工厂
             */
            bootstrap.channel(NioSocketChannel.class);
            /**设置选项
             * 参数：Socket的标准参数（key，value），可自行百度
             * */
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            /**
             * 自定义客户端Handle（客户端在这里搞事情）
             */
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new SimpleNettyClientHandler());
                }
            });

            /** 开启客户端监听，连接到远程节点，阻塞等待直到连接完成*/
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            /**阻塞等待数据，直到channel关闭(客户端关闭)*/
            channelFuture.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        SimpleNettyClient client = new SimpleNettyClient();
        client.connect("127.0.0.1", 8088);

    }

}
```

​	从上文的客户端代码展示了 Netty 客户端初始化时所需的所有内容：

1. `EventLoopGroup`: 不论是服务器端还是客户端, 都必须指定 EventLoopGroup. 在这个例子中, 指定了 NioEventLoopGroup, 表示一个 NIO的EventLoopGroup.
2. `ChannelType`: 指定 Channel 的类型. 因为是客户端, 因此使用了 NioSocketChannel.
3. `Handler`: 设置数据的处理器.
4. `option`: 提供了一系列的TCP参数可设置

### 2.33	深入探究客户端通过Bootstrap启动后,都做了哪些工作

#### 2.33.1	group(group)

```java
 /**
  * 直接调用父类AbstractBootstrap的方法
  */
public B group(EventLoopGroup group) {
    if (group == null) {
        throw new NullPointerException("group");
    }
    if (this.group != null) {
        throw new IllegalStateException("group set already");
    }
    this.group = group;
    return self();
}
```

　　直接调用父类的方法 ，说明该EventLoopGroup，作为客户端 Connector 线程，负责注册监听连接操作位，用于判断异步连接结果。

#### 2.33.2	channel(NioServerSocketChannel.class)

　　在 Netty 中, Channel是一个Socket的抽象, 它为用户提供了关于 Socket 状态(是否是连接还是断开) 以及对 Socket 的读写等操作. 每当 Netty 建立了一个连接后, 都会有一个对应的 Channel 实例。

```java
/**
 * 同样也是直接调用父类AbstractBootstrap的方法
 */
    public B channel(Class<? extends C> channelClass) {
        if (channelClass == null) {
            throw new NullPointerException("channelClass");
        }
        return channelFactory(new ReflectiveChannelFactory<C>(channelClass));
    }
```

#### 2.33.3　　**ReflectiveChannelFactory类:**

```java
    public class ReflectiveChannelFactory<T extends Channel> implements ChannelFactory<T> {

        private final Class<? extends T> clazz;

        /**
         * 通过构造函数 传入 clazz
         */
        public ReflectiveChannelFactory(Class<? extends T> clazz) {
            if (clazz == null) {
                throw new NullPointerException("clazz");
            }
            this.clazz = clazz;
        }

        /**
         * 只用这一个方法 通过传入不同的Channel.class 创建不同的Channel 对象。
         * newChannel() 什么时候调用呢 仔细追源码 发现是在绑定 IP 和 端口的 doResolveAndConnect方法里会调用
         */
        @Override
        public T newChannel() {
            try {
                return clazz.getConstructor().newInstance();
            } catch (Throwable t) {
                throw new ChannelException("Unable to create Channel from class " + clazz, t);
            }
        }
```

​	 仔细追源码 发现newChannel() fangfa是在绑定 IP 和 端口的 doResolveAndConnect方法里会调用

#### 2.33.4	channelFactory(new ReflectiveChannelFactory(channelClass)) 方法：

```java
   /**
     * 创建好Channel后，返回对象Bootstrap本身
     */
    @Deprecated
    public B channelFactory(ChannelFactory<? extends C> channelFactory) {
        if (channelFactory == null) {
            throw new NullPointerException("channelFactory");
        }
        if (this.channelFactory != null) {
            throw new IllegalStateException("channelFactory set already");
        }
        this.channelFactory = channelFactory;
        return self();
    }
```

　　因此对于我们这个例子中的客户端的 Bootstrap 而言, 生成的的 Channel 实例就是 NioSocketChannel。



### 2.4	handler(ChannelHandler handler)处理器

​	Netty 的一个强大和灵活之处就是基于 ==**Pipeline**== 的`自定义 handler 机制`。基于此, 我们可以像添加插件一样自由组合各种各样的 handler 来完成业务逻辑。例如我们需要处理 HTTP 数据, 那么就可以在 pipeline 前添加一个 Http 的编解码的 Handler, 然后接着添加我们自己的业务逻辑的 handler, 这样网络上的数据流就向通过一个管道一样, 从不同的 handler 中流过并进行编解码, 最终在到达我们自定义的 handler 中。

​	定义handler一般使用handler方法

```java
/**
 * 同样也是 直接调用父类 AbstractBootstrap 的方法
 */
public B handler(ChannelHandler handler) {
    if (handler == null) {
        throw new NullPointerException("handler");
    }
    this.handler = handler;
    return self();
}
```

​	但更常用的是:使用ChannelInitializer作为形参进行channel配置：

```java
.handler(new ChannelInitializer<SocketChannel>() {
         @Override
         public void initChannel(SocketChannel ch) throws Exception {
             ChannelPipeline p = ch.pipeline();
             p.addLast(new EchoClientHandler());
        }
     })
```

​	　ootstrap.handler() 方法接收一个 ChannelHandler, 而我们传递的是一个派生于ChannelInitializer 的匿名类, 它正好也实现了 ChannelHandler 接口。

​	ChannelInitializer 类部分代码:

```java
/**
     * ChannelInboundHandlerAdapter 父类的父类 最终会继承 ChannelHandler 
     * 那么ChannelInitializer 也就是 ChannelHandler的 子类
     */
    public abstract class ChannelInitializer<C extends Channel> extends ChannelInboundHandlerAdapter {

        private static final InternalLogger logger
            =InternalLoggerFactory.getInstance(ChannelInitializer.class);

        /**
         * 这里只有这一个抽象类 所以我们只需重写这一个方法就可以了
         */
        protected abstract void initChannel(C ch) throws Exception;

        @Override
        @SuppressWarnings("unchecked")
        public final void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            initChannel((C) ctx.channel());
            ctx.pipeline().remove(this);   // 移除自身
            ctx.fireChannelRegistered();
        }
   
    }
```

​	关注一下 ==**channelRegistered 方法**==. 从上面的源码中, 我们可以看到, 在 channelRegistered 方法中, 会调用 **==initChannel==** 方法, **==将自定义的 handler 添加到 ChannelPipeline==** 中, 然后调用 ctx.pipeline().remove(this) 将自己从 ChannelPipeline 中删除. 上面的分析过程, 可以用如下图片展示:

- 一开始, ChannelPipeline 中只有三个 handler：head, tail 和我们添加的 ChannelInitializer。

　　　　　　　　　　![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1010726-20200319050637884-1966125960.png)

- 接着 initChannel 方法调用后, 添加了自定义的 handler：

　　　　　　![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1010726-20200319050715169-1413008651.png)

- 最后将 ChannelInitializer 删除，仅剩下head、tail以及自己添加的handler：

　　　　　　　　![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1010726-20200319050820530-1780455005.png) 



### 2.5	ChannelPipeline对象

#### 2.5.1	ChannelHandler

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200319182913079-1543270668.png)

​	　如上图所示ChannelHandler下主要是两个子接口

- ChannelInboundHandler(入站): 处理输入数据和Channel状态类型改变。
- 适配器: ChannelInboundHandlerAdapter（适配器设计模式）
- 常用的: SimpleChannelInboundHandler
- ChannelOutboundHandler(出站): 处理输出数据
- 适配器: ChannelOutboundHandlerAdapter

　Netty 以适配器类的形式提供了大量默认的ChannelHandler 实现，帮我们简化应用程序处理逻辑的开发过程。每一个Handler都一定会处理出站或者入站(可能两者都处理数据),例如对于入站的Handler可能会继承SimpleChannelInboundHandler或者ChannelInboundHandlerAdapter, 而SimpleChannelInboundHandler又是继承于ChannelInboundHandlerAdapter，最大的区别在于SimpleChannelInboundHandler会对没有外界引用的资源进行一定的清理, 并且入站的消息可以通过泛型来规定。

　　采用**适配器模式**，是因为我们在写自定义Handel时候,很少会直接实现上面两个接口,因为接口中有很多默认方法需要实现,所以这里就采用了设配器模式,ChannelInboundHandlerAdapter和

​	`ChannelInboundHandlerAdapter`就是设配器模式的产物,让它去实现上面接口,实现它所有方法。那么你自己写自定义Handel时,==**只要继承它,就无须重写上面接口的所有方法了**==。

##### 2.5.1.1	ChannelHandler 生命周期

- handlerAdded： 当 ChannelHandler 添加到 ChannelPipeline 调用
- handlerRemoved： 当 ChannelHandler 从 ChannelPipeline 移除时调用
- exceptionCaught： 当 ChannelPipeline 执行抛出异常时调用

#### 2.5.2	ChannelPipeline概述

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200319184039846-132901867.png)

​	如图所示`ChannelPipeline类`是**ChannelHandler实例对象的链表**，用于处理或截获通道的接收和发送数据。它提供了一种高级的截取过滤模式（类似serverlet中的filter功能），让用户可以在ChannelPipeline中完全控制一个事件以及处理ChannelHandler与ChannelPipeline的交互。

​    ==对于每个新的通道**Channel**，都会创建一个新的**ChannelPipeline**，并将**pipeline**附加到**channel**中。==下图描述ChannelHandler与pipeline中的关系，一个io操作可以由一个`ChannelInboundHandler`或`ChannelOutboundHandle`进行处理，并通过调用`ChannelInboundHandler`处理入站io或通过`ChannelOutboundHandler`处理出站IO。

​	![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200319184228275-1588964189.png)

#### 2.5.3	常用方法

```java
addFirst(...)   //添加ChannelHandler在ChannelPipeline的第一个位置
addBefore(...)   //在ChannelPipeline中指定的ChannelHandler名称之前添加ChannelHandler
addAfter(...)   //在ChannelPipeline中指定的ChannelHandler名称之后添加ChannelHandler
addLast(...)   //在ChannelPipeline的末尾添加ChannelHandler
remove(...)   //删除ChannelPipeline中指定的ChannelHandler
replace(...)   //替换ChannelPipeline中指定的ChannelHandler
```

​	ChannelPipeline可以动态添加、删除、替换其中的ChannelHandler，这样的机制可以提高灵活性。示例:

```
ChannelPipeline pipeline = ch.pipeline(); 
　　FirstHandler firstHandler = new FirstHandler(); 
　　pipeline.addLast("handler1", firstHandler); 
　　pipeline.addFirst("handler2", new SecondHandler()); 
　　pipeline.addLast("handler3", new ThirdHandler()); 
　　pipeline.remove("“handler3“"); 
　　pipeline.remove(firstHandler); 
　　pipeline.replace("handler2", "handler4", new FourthHandler());
```

#### 2.5.4	入站出站Handler执行顺序

​	和Fliter一样，以全部addLast为序的话，inboundHandler是先new先Do，outboundHandler是先new后do。

#### 2.5.5	ChannelPipeline中channelHandler协作规则

- 业务执行后需要 ChannelHandlerContext.fire\*() 或者 Channel\*Handler.super\*(), 否则不会传递到下一个handler，如下图方法：

![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1010726-20200319191643622-835622905.png)

- 如果outhandler在inhandler之后添加，所有inhandler中的最后一个inhandler需要写个ctx.channel().write, 这样能进入outhandler的write()中执行，如下图：

　　　　　　　　　　![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1010726-20200319191841015-1100058453.png)

- 在第4.2中，如果不希望使用ctx.channel().write，那么需要把outhandler在inhandler之前添加到pipeline（重要），该情况下还使用ctx.channel().write，会触发两次outhandler.write，如下图：

　　　　　　　![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1010726-20200319192101353-1566080049.png)

　　通常来说outhandler都放到前面添加。netty的`findChannelHandler`机制寻找读事件会先找outhanlder的read方法，在inhandler前面添加的outhandler不能在write方法内调用fireChannelRead事件，否则将pipeline会进入死循环，死循环为：outHandler(read)-->inhandler(read)-->**outhandler(write)-->inhandler(read) ·\**·\**\**· \*\*·\*\*\*\*·\*\*\*\*·\*\**\***最后这次read触发就是因为outhandler的write方法出现了fireChannelRead事件。

- outhandler使用ctx.write(msg,promise)传递给下一个outhandler，如下图：

![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1010726-20200319192234887-1875923147.png)

- 所有的inhandler的最后一个使用ctx.writeAndFlush(msg)触发给outhandler所有的outhandler的出口，outhandler最后也需要通过ctx.writeAndFlush(msg)才能发送给客户端。
- 如果多个inhandler执行ctx.writeAndFlush(msg) 客户端则会收到多个返回数据，因为这样outhandler会被触发多次。
- 针对客户端和服务端而言，outBound和Inbound谁先执行这个问题答案截然相反
    - 客户端是发起请求再接受数据，先outbound再inbound
    - 服务端则是接受数据再发送信息，先inbound再outbound













































































#### 2.5.9	ChannelPipeline中的channelHandler协作规则

```java
 /**
     * 我们在initChannel抽象方法的实现方法中 通过 SocketChannel获得 ChannelPipeline对象
     */
    ChannelPipeline p = ch.pipeline();
    p.addLast(newEchoClientHandler());
```

​	在==**实例化一个 Channel 时,**== 会==**伴随着一个 ChannelPipeline 的实例化**==, 并且==**此 Channel 会与这个 ChannelPipeline 相互关联**==, 这一点可以通过NioSocketChannel 的父类 AbstractChannel 的构造器可以看出

```java
protected AbstractChannel(Channel parent) {
    this.parent = parent;
    unsafe = newUnsafe();
    //这个可以看出
    pipeline = new DefaultChannelPipeline(this);
}
```

　　当实例化一个 Channel(这里以 SimpleNettyClient 为例, 那么 Channel 就是 NioSocketChannel), 其 pipeline 字段就是我们新创建的 DefaultChannelPipeline 对象, 那么我们就来看一下 DefaultChannelPipeline 的构造方法。

```java
public DefaultChannelPipeline(AbstractChannel channel) {
    if (channel == null) {
        throw new NullPointerException("channel");
    }
    this.channel = channel;

    tail = new TailContext(this);
    head = new HeadContext(this);

    head.next = tail;
    tail.prev = head;
}
```

​	我们调用 `DefaultChannelPipeline` 的构造器, 传入了一个 channel, 而这个 channel 其实就是我们实例化的 **==NioSocketChannel==**, `DefaultChannelPipeline` 会将这个 **NioSocketChannel** 对象保存在channel 字段中。`DefaultChannelPipeline` 中, 还有两个特殊的字段, 即`head` 和`tail`, 而这两个字段是一个`双向链表的头和尾`. 其实在 `DefaultChannelPipeline` 中, 维护了一个以 ==**AbstractChannelHandlerContext**== 为节点的双向链表, 这个链表是 Netty 实现 Pipeline 机制的关键。

### 2.6	ServerBootstrap类

​	ServerBootstrap可以理解为服务器启动的工厂类，我们可以通过它来完成服务器端的 Netty 初始化。

​	作用职责: **==EventLoop初始化,==** **==channel的注册过程==** , **==关于pipeline的初始化==**, **==handler的添加过程==**, **==服务端连接分析==**。

​	下面是一个服务端程序的demo:

```java
// 服务器端应用程序使用两个NioEventLoopGroup创建两个EventLoop的组，EventLoop这个相当于一个处理线程，是Netty接收请求和处理IO请求的线程。
        // 主线程组, 用于接受客户端的连接，但是不做任何处理，跟老板一样，不做事
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 从线程组, 当boss接受连接并注册被接受的连接到worker时，处理被接受连接的流量。
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // netty服务器启动类的创建, 辅助工具类，用于服务器通道的一系列配置
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            /**
             * 使用了多少线程以及如何将它们映射到创建的通道取决于EventLoopGroup实现，甚至可以通过构造函数进行配置。
             * 设置循环线程组，前者用于处理客户端连接事件，后者用于处理网络IO(server使用两个参数这个)
             * public ServerBootstrap group(EventLoopGroup group)
             * public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup)
             */
            serverBootstrap.group(bossGroup, workerGroup)           //绑定两个线程组
                    // 用于构造socketchannel工厂
                    .channel(NioServerSocketChannel.class)   //指定NIO的模式
                    /**
                     * @Description: 初始化器，channel注册后，会执行里面的相应的初始化方法，传入自定义客户端Handle（服务端在这里操作）
                     *
                     @Override
                     protected void initChannel(SocketChannel channel) throws Exception {
                     // 通过SocketChannel去获得对应的管道
                     ChannelPipeline pipeline = channel.pipeline();

                     // 通过管道，添加handler
                     pipeline.addLast("nettyServerOutBoundHandler", new NettyServerOutBoundHandler());
                     pipeline.addLast("nettyServerHandler", new NettyServerHandler());
                     }
                      * 子处理器也可以通过下面的内部方法来实现。
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>() {  // 子处理器，用于处理workerGroup
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new SimpleNettyServerHandler());

                        }
                    });

            // 启动server，绑定端口，开始接收进来的连接，设置8088为启动的端口号，同时启动方式为同步
            ChannelFuture channelFuture = serverBootstrap.bind(8088).sync();

            System.out.println("server start");
            // 监听关闭的channel，等待服务器 socket 关闭 。设置位同步方式
            channelFuture.channel().closeFuture().sync();
        } finally {
            //退出线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
```

#### 2.61	group(bossGroup, workerGroup)

​	跟客户端Bootstrap明显的一个区别就是，客户端只传入了一个NioEventLoopGroup, 而服务端传入了两个。看源码

```java
/**
  * 这里调用的是 ServerBootstrap 类本身的 group 方法 发现传入的两个EventLoopGroup
  * 一个赋值给父类（AbstractBootstrap）,另一个赋值给 该对象本身属性
  */
 public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {

     //调用父类的group方法
     super.group(parentGroup);
     if (childGroup == null) {
         throw new NullPointerException("childGroup");
     }
     if (this.childGroup != null) {
         throw new IllegalStateException("childGroup set already");
     }
     this.childGroup = childGroup;
     return this;
 }
```

#### 2.61.1	bossGroup和workerGroup的分工

- bossGroup 是用于服务端 的 accept 的, 即用于处理客户端的连接请求。
- workerGroup 它们负责客户端连接通道的 IO 操作
- 关于 bossGroup 与 workerGroup 的关系, 我们可以用如下图来展示:

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200318181718232-1807589271.png)

​	首先, 服务器端 bossGroup 不断地监听是否有客户端的连接, 当发现有一个新的客户端连接到来时, bossGroup 就会为此连接初始化各项资源,然后从 workerGroup 中选出一个 EventLoop 绑定到此客户端连接中. 那么接下来的服务器与客户端的交互过程就全部在此分配的 EventLoop 中了。

​	关于 bossGroup 和 workerGroup 和 channel 如何联系到一起的，答案是通过==**bind(host)**==方法

#### 2.62	channel(NioServerSocketChannel.class)方法

　　这里传入的类是NioServerSocketChannel,而客户端是NioSocketChannel,但他们都是通过类的反射机制获得类的对象的。同样真正用到该对象的时候，也是在bind(host)方法里。

#### 2.63	handler()和childHandler()

　　跟客户端比较发现还是有明显区别的, 和 EventLoopGroup 一样, 服务器端的 handler 也有两个, 一个是通过 handler() 方法设置 handler 字段, 另一个是通过childHandler() 设置 childHandler 字段。不过==handler()方法并不是必须的，而**childHandler()方法是必须调用**的==。看代码：

```java
/**handler(new LoggingHandler(LogLevel.INFO))
     * 
     * 我们发现channel方法调用的是父类(AbstractBootstrap)的方法
     * 所以这个 handler  字段与 accept 过程有关, 即这个 handler 负责处理客户端的连接请求
     */
    public B handler(ChannelHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        this.handler = handler;
        return self();
    }

    /** 再看childHandler(class)
     * 
     *很明显 这个childHandler 方法是属于ServerBootstrap 本身的方法
     * 所以推测: 这个childHandler 就是负责和客户端的连接的 IO 交互
     */
    public ServerBootstrap childHandler(ChannelHandler childHandler) {
        if (childHandler == null) {
            throw new NullPointerException("childHandler");
        }
        this.childHandler = childHandler;
        return this;
    }
```

#### 2.64	bind(host)方法:interrobang:

​	bind(host)才是整个流程的关键，前面做得只是初始化了一些netty客户端运行的对象(可以理解成只是创建了对象，并没有使用它），但真正用到这些这些对象，还是在`bind(host)`方法里。如源码：

```java
/**
         * 1、调用父类(AbstractBootstrap)的方法
         * <p>
         * 作用: 根据端口号 创建一个InetSocketAddress对象,用于连接连接服务器
         */
        public ChannelFuture bind(int inetPort) {
            return bind(new InetSocketAddress(inetPort));
        }

        /**
         * 2、继续调用父类(AbstractBootstrap)的方法
         * <p>
         * 作用: 做一些校验工作
         */
        public ChannelFuture bind(SocketAddress localAddress) {
            validate();
            if (localAddress == null) {
                throw new NullPointerException("localAddress");
            }
            return doBind(localAddress);
        }


        /**
         * 3、继续调用父类(AbstractBootstrap)的方法
         * <p>
         * 作用: 这个方法做了很多事情
         */
        private ChannelFuture doBind(final SocketAddress localAddress) {
            //3、1 具体看下面3、1的代码部分
            final ChannelFuture regFuture = initAndRegister();
            final Channel channel = regFuture.channel();

            ChannelPromise promise = channel.newPromise();
            doBind0(regFuture, channel, localAddress, promise);
            return promise;
        }
    }

    /**
     * 3、1  这步做了很多重要的事情
     */
    final ChannelFuture initAndRegister() {
        Channel channel = null;

        //这里终于调用newChannel方法了，这里就是之前BootStrap讲的ReflectiveChannelFactory对象的方法，这里的
        //channel 对象是NioServerSocketChannel。
        channel = channelFactory.newChannel();
        //这个方法也太重要了 和handle有关 下面3.1.1 讲它
        init(channel);

        //这里的group()获取的就是bootstrap ,这里面会调用next方法 来循环获取下一个channel
        //这里group().register(channel) 将 bossGroup 和 NioServerSocketChannel 关联起来了.
        ChannelFuture regFuture = config().group().register(channel);

        return regFuture;
    }

    /**
     * 3.1.1 首先可以看到init的方法在父类(AbstractBootstrap)已经提供，只是子类写具体实现代码
     */
    abstract void init(Channel channel) throws Exception;

    /**
     * 我们再来看ServerBootstrap实现了init方法，这里面做了很多事
     * 比如workerGroup相关，还有handel相关
     */
    @Override
    void init(Channel channel) throws Exception {

        //通过channel获得ChannelPipeline，说明每一个channel都会对应一个ChannelPipeline
        ChannelPipeline p = channel.pipeline();

        //这里终于获得workerGroup 对象
        final EventLoopGroup currentChildGroup = childGroup;
        //这里获得childHandler对象
        final ChannelHandler currentChildHandler = childHandler;
        final Entry<ChannelOption<?>, Object>[] currentChildOptions;
        final Entry<AttributeKey<?>, Object>[] currentChildAttrs;

        p.addLast(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(final Channel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                //获得handel方法传入的对象
                ChannelHandler handler = config.handler();

                //这一步说明 .handler(new LoggingHandler(LogLevel.INFO))方法不是必须要的
                //如果你没有调handler方法也没有关系 ，因为它会在这路做一层判断
                if (handler != null) {
                    pipeline.addLast(handler);
                }

                //到这里线程就开始启动运行了 发现已经讲Channel,ChannelPipeline,workerGroup,childHandler等全部联系到了一起。
                ch.eventLoop().execute(new Runnable() {
                    @Override
                    public void run() {
                        pipeline.addLast(new ServerBootstrapAcceptor(
                                ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
                    }
                });
            }
        });
    }
```



### 2.7	Netty中的数据传输通道Channel

​	与channel相关的概念有以下四个

![](https://upload-images.jianshu.io/upload_images/1089449-afd9e14197e1ef11.png?imageMogr2/auto-orient/strip|imageView2/2/w/751/format/webp)

- **Channel**，表示一个连接，可以理解为每一个请求，就是一个`Channel`。
- **ChannelHandler**，核心处理业务就在这里，用于处理业务请求。
- **ChannelHandlerContext**，用于传输业务数据。
- **ChannelPipeline**，用于保存处理过程需要用到的`ChannelHandler`和`ChannelHandlerContext`。

#### 2.71	Channel 类型

　　除了 TCP 协议以外, Netty 还支持很多其他的连接协议, 并且每种协议还有 NIO(异步 IO) 和 OIO(Old-IO, 即传统的阻塞 IO) 版本的区别. 不同协议不同的阻塞类型的连接都有不同的 Channel 类型与之对应，下面是一些常用的 Channel 类型：

```
- NioSocketChannel, 代表异步的客户端 TCP Socket 连接.
- NioServerSocketChannel, 异步的服务器端 TCP Socket 连接.
- NioDatagramChannel, 异步的 UDP 连接
- NioSctpChannel, 异步的客户端 Sctp 连接.
- NioSctpServerChannel, 异步的 Sctp 服务器端连接.
- OioSocketChannel, 同步的客户端 TCP Socket 连接.
- OioServerSocketChannel, 同步的服务器端 TCP Socket 连接.
- OioDatagramChannel, 同步的 UDP 连接
- OioSctpChannel, 同步的 Sctp 服务器端连接.
- OioSctpServerChannel, 同步的客户端 TCP Socket 连接.
```



#### 2.7.2	Channel 生命周期(执行顺序也是从上倒下)

- channelRegistered: channel注册到一个EventLoop。
- channelActive: 变为活跃状态（连接到了远程主机），可以接受和发送数据
- channelInactive: channel处于非活跃状态，没有连接到远程主机
- channelUnregistered: channel已经创建，但是未注册到一个EventLoop里面，也就是没有和Selector绑定

### 2.8	.connect(host, port)

​	此方法是客户端发起TCP连接的IP地址和端口号的方法。底层最终是会调用 **doConnect** 方法。



## 3.0	核心组件ByteBuf及API

​	ByteBuf提供读访问索引(readerIndex)和写访问索引(writerIndex)来控制字节数组。ByteBuf API具有以下优点:

- 允许用户自定义缓冲区类型扩展
- 通过内置的复合缓冲区类型实现透明的零拷贝
- 容量可按需增长
- 读写这两种模式之间不需要调用类似于JDK的ByteBuffer的flip()方法进行切换
- 读和写使用不同的索引
- 支持方法的链式调用
- 支持引用计数
- 支持池化

### 3.1	ByteBuf工作原理

​	ByteBuf维护两个不同的索引: 读索引(readerIndex)和写索引(writerIndex)。

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200323163504327-1584261037.png)

- ByteBuf维护了readerIndex和writerIndex索引。
- 当readerIndex > writerIndex时，则抛出IndexOutOfBoundsException。
- ByteBuf容量 = writerIndex。
- ByteBuf可读容量 = writerIndex - readerIndex。
- readXXX()和writeXXX()方法将会推进其对应的索引，自动推进。
- getXXX()和setXXX()方法对writerIndex和readerIndex无影响，不会改变index值。

​	readerIndex和WriterIndex将整个ByteBuf分成了三个区域：可丢弃字节、可读字节、可写字节，如下图：

　　当尚未读取时，却已经写入部分内容时，ByteBuf拥有可读字节区域以及可写字节区域。

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200323165843102-1164869462.png)

​	当已经读过部分区域后，变成了可丢弃字节、可读字节、可写字节三个区域。

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200323165935001-403099973.png)

### 3.2	ByteBuf的使用模式

　　ByteBuf本质是: 一个由不同的索引分别控制读访问和写访问的字节数组。ByteBuf共有三种模式: 堆缓冲区模式(Heap Buffer)、直接缓冲区模式(Direct Buffer)和复合缓冲区模式(Composite Buffer)，相较于NIO的ByteBuffer多了一种复合缓冲区模式。

#### 3.2.1	堆缓冲区模式(Heap Buffer)

​	　堆缓冲区模式又称为：支撑数组(backing array)。将数据存放在JVM的堆空间，通过将数据存储在数组中实现。

- 堆缓冲的优点: 由于数据存储在Jvm堆中可以快速创建和快速释放，并且提供了数组直接快速访问的方法。
- 堆缓冲的缺点: 每次数据与I/O进行传输时，都需要将数据拷贝到直接缓冲区。

```java
public static void heapBuffer() {
    // 创建Java堆缓冲区
    ByteBuf heapBuf = Unpooled.buffer(); 
    if (heapBuf.hasArray()) { // 是数组支撑
        byte[] array = heapBuf.array();
        int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();
        int length = heapBuf.readableBytes();
        handleArray(array, offset, length);
    }
}
```

#### 3.2.2	直接缓冲区模式(Direct Buffer)

　　Direct Buffer属于堆外分配的直接内存，不会占用堆的容量。适用于套接字传输过程，避免了数据从内部缓冲区拷贝到直接缓冲区的过程，性能较好。

- Direct Buffer的优点: 使用Socket传递数据时性能很好，避免了数据从Jvm堆内存拷贝到直接缓冲区的过程，提高了性能。
- Direct Buffer的缺点: 相对于堆缓冲区而言，Direct Buffer分配内存空间和释放更为昂贵。

　　对于涉及大量I/O的数据读写，建议使用Direct Buffer。而对于用于后端的业务消息编解码模块建议使用Heap Buffer。

　　代码如下:

```java
public static void directBuffer() {
    ByteBuf directBuf = Unpooled.directBuffer();
    if (!directBuf.hasArray()) {
        int length = directBuf.readableBytes();
        byte[] array = new byte[length];
        directBuf.getBytes(directBuf.readerIndex(), array);
        handleArray(array, 0, length);
    }
}
```

#### 3.2.3	复合缓冲区模式(Composite Buffer)

​	Composite Buffer是Netty特有的缓冲区。本质上类似于提供一个或多个ByteBuf的组合视图，可以根据需要添加和删除不同类型的ByteBuf。

- 想要理解Composite Buffer，请记住：它是一个组合视图。它提供一种访问方式让使用者自由的组合多个ByteBuf，避免了拷贝和分配新的缓冲区。
- Composite Buffer不支持访问其支撑数组。因此如果要访问，需要先将内容拷贝到堆内存中，再进行访问
- 下图是将两个ByteBuf：头部+Body组合在一起，没有进行任何复制过程。仅仅创建了一个视图

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200323164711684-508833478.png)

```java
public static void byteBufComposite() {
    // 复合缓冲区，只是提供一个视图
    CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
    ByteBuf headerBuf = Unpooled.buffer(); // can be backing or direct
    ByteBuf bodyBuf = Unpooled.directBuffer();   // can be backing or direct
    messageBuf.addComponents(headerBuf, bodyBuf);
    messageBuf.removeComponent(0); // remove the header
    for (ByteBuf buf : messageBuf) {
        System.out.println(buf.toString());
    }
}
```

#### 3.2.4	三种ByteBuf使用区别对比

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200323164955265-1369803019.png)

### 3.3	ByteBuf的池化与非池化

　　内存的申请和销毁都有一定性能开销，内存池化技术可以有效的减少相关开销。Netty在4引入了该技术。Netty的池化分为对象池和内存池，对应的ByteBuf的堆缓冲区和直接缓冲区。

　　是否使用池化取决于ByteBufAllocator使用的实例对象（参考分配方式ByteBufAllocator相关说明，本文后部分有说明）

　　PooledByteBufAllocator可以通过ctx.alloc获得，如下图：

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200323165555566-240986969.png)

​	Netty默认使用池化byteBuf，如果想要声明不池化的可以使用Unpooled工具类。

### 3.4	字节级操作

#### 3.4.1	随机访问索引

　　ByteBuf的索引与普通的Java字节数组一样。第一个字节的索引是0，最后一个字节索引总是capacity()-1。ByteBuf的API分为4大类：`get\*、set\*、read\*、write*。使用有以下两条规则：

- readXXX()和writeXXX()方法将会推进其对应的索引readerIndex和writerIndex。自动推进
- getXXX()和setXXX()方法用于访问数据，对writerIndex和readerIndex无影响

　　代码如下:

#### 3.4.2	可丢弃字节区域

​	可丢弃字节区域是指:==**[0，readerIndex]之间的区域**==。可调用**discardReadBytes**()方法丢弃已经读过的字节。

-  **discardReadBytes()**效果: 将可读字节区域(CONTENT)[readerIndex, writerIndex)往前移动readerIndex位，同时修改读索引和写索引。
-  **discardReadBytes()**方法会移动可读字节区域内容(CONTENT)。如果频繁调用，会有多次数据复制开销，对性能有一定的影响。

#### 3.4.3	可读字节区域

​	可读字节区域是指:[readerIndex, writerIndex]之间的区域。任何名称以**read和skip**开头的操作方法，都会改变readerIndex索引。

#### 3.4.4	可写字节区域

​	可写字节区域是指:[writerIndex, capacity]之间的区域。任何名称以**write**开头的操作方法都将改变writerIndex的值。

#### 3.4.5	索引管理

- `markReaderIndex()+resetReaderIndex()` ----->
    -  `markReaderIndex`()是先备份当前的readerIndex，`resetReaderIndex()`则是将刚刚备份的readerIndex恢复回来。常用于dump ByteBuf的内容，又不想影响原来ByteBuf的readerIndex的值
- readerIndex(int) ----- 设置readerIndex为固定的值
- writerIndex(int) ----- 设置writerIndex为固定的值
- clear() ----- 效果是: readerIndex(**0**), writerIndex(**0**)。**==不会==**清除内存
- 调用clear()比调用discardReadBytes()轻量的多。仅仅重置readerIndex和writerIndex的值，不会拷贝任何内存，开销较小。

#### 3.4.6	查找操作(indexOf)

　　查找ByteBuf指定的值。类似于，String.indexOf("str")操作

- 最简单的方法 ----- indexOf(）
- 利用ByteProcessor作为参数来查找某个指定的值。

#### 3.4.7	其余访问操作

​	除去get、set、read、write类基本操作，还有一些其余的有用操作，如下图：

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200323172037704-911141730.png)

- hasArray() ：如果ByteBuf由一个字节数组支撑，则返回true。通俗的讲：ByteBuf是堆缓冲区模式，则代表其内部存储是由字节数组支撑的。
- array() ：如果ByteBuf是由一个字节数组支撑则返回数组，否则抛出UnsupportedOperationException异常。也就是，ByteBuf是堆缓冲区模式。

### 3.5	ByteBufHolder的使用

​	我们时不时的会遇到这样的情况：即需要另外存储除有效的实际数据各种属性值。HTTP响应就是一个很好的例子；与内容一起的字节的还有状态码，cookies等。

　　Netty 提供的 ByteBufHolder 可以对这种常见情况进行处理。 ByteBufHolder 还提供了对于 Netty 的高级功能，如缓冲池，其中保存实际数据的 ByteBuf 可以从池中借用，如果需要还可以自动释放。

　　ByteBufHolder 有那么几个方法。到底层的这些支持接入数据和引用计数。如下图所示：

　　　　　　　　![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/1010726-20200323174734013-744851912.png)

　　ByteBufHolder是ByteBuf的容器，可以通过子类实现ByteBufHolder接口，根据自身需要添加自己需要的数据字段。可以用于自定义缓冲区类型扩展字段。Netty提供了一个默认的实现DefaultByteBufHolder：

```java
public class CustomByteBufHolder extends DefaultByteBufHolder{
 
    private String protocolName;
 
    public CustomByteBufHolder(String protocolName, ByteBuf data) {
        super(data);
        this.protocolName = protocolName;
    }
 
    @Override
    public CustomByteBufHolder replace(ByteBuf data) {
        return new CustomByteBufHolder(protocolName, data);
    }
 
    @Override
    public CustomByteBufHolder retain() {
        super.retain();
        return this;
    }
 
    @Override
    public CustomByteBufHolder touch() {
        super.touch();
        return this;
    }
 
    @Override
    public CustomByteBufHolder touch(Object hint) {
        super.touch(hint);
        return this;
    }
    ...
}
```

### 3.6	ByteBuf分配

​	　创建和管理ByteBuf实例的多种方式：按需分配(ByteBufAllocator)、Unpooled缓冲区和ByteBufUtil类。

#### 3.6.1	按序分配: ByteBufAllocator接口

　　Netty通过接口ByteBufAllocator实现了ByteBuf的池化。Netty提供池化和非池化的ButeBufAllocator，是否使用池是由应用程序决定的: 

- ctx.channel().alloc().buffer() ----- 本质就是: ByteBufAllocator.DEFAULT
- ByteBufAllocator.DEFAULT.buffer() ----- 返回一个基于堆或者直接内存存储的Bytebuf。默认是堆内存
- ByteBufAllocator.DEFAULT ----- 有两种类型: UnpooledByteBufAllocator.DEFAULT(非池化)和PooledByteBufAllocator.DEFAULT(池化)。对于Java程序，默认使用PooledByteBufAllocator(池化)。对于安卓，默认使用UnpooledByteBufAllocator(非池化)
- 可以通过BootStrap中的Config为每个Channel提供独立的ByteBufAllocator实例

　　ByteBufAllocator提供的操作如下图:

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200323175602624-417660144.png)

​	注意：

- 上图中的buffer()方法，返回一个基于堆或者直接内存存储的Bytebuf ----- 默认是堆内存。源码: AbstractByteBufAllocator() { this(false); }
- ByteBufAllocator.DEFAULT ----- 可能是池化，也可能是非池化。默认是池化(PooledByteBufAllocator.DEFAULT)
- 通过一些方法接受整型参数允许用户指定 ByteBuf 的初始和最大容量值。

​	得到一个 ByteBufAllocator 的引用很简单。你可以得到从 Channel （在理论上，每 Channel 可具有不同的 ByteBufAllocator ），或通过绑定到的 ChannelHandler 的 ChannelHandlerContext 得到它，如代码：

```java
Channel channel = ...;
ByteBufAllocator allocator = channel.alloc(); //1、Channel

ChannelHandlerContext ctx = ...;
ByteBufAllocator allocator2 = ctx.alloc(); //2、 ChannelHandlerContext
```

​	　第一种是从 channel 获得 ByteBufAllocator，第二种是从 ChannelHandlerContext 获得 ByteBufAllocator。

　　Netty 提供了两种 ByteBufAllocator 的实现，一种是 PooledByteBufAllocator,用ByteBuf 实例池改进性能以及内存使用降到最低，此实现使用一个“jemalloc”内存分配。其他的实现不池化 ByteBuf 情况下，每次返回一个新的实例。Netty 默认使用 PooledByteBufAllocator，我们可以通过 ChannelConfig 或通过引导设置一个不同的实现来改变。

#### 3.6.2	Unpooled缓冲区：非池化

​	Unpooled提供静态的辅助方法来创建未池化的ByteBuf。其包含方法如下：

![](https://img2020.cnblogs.com/i-beta/1010726/202003/1010726-20200323180306284-1355399899.png)

注意:

- 上图的buffer()方法，返回一个未池化的基于堆内存存储的ByteBuf
- wrappedBuffer() ：创建一个视图，返回一个包装了给定数据的ByteBuf。非常实用

　　创建ByteBuf代码:

```java
public void createByteBuf(ChannelHandlerContext ctx) {
    // 1. 通过Channel创建ByteBuf，实际上也是使用ByteBufAllocator,因为ctx.channel().alloc()返回的就是一个ByteBufAllocator对象
    ByteBuf buf1 = ctx.channel().alloc().buffer();
    // 2. 通过ByteBufAllocator.DEFAULT创建
    ByteBuf buf2 =  ByteBufAllocator.DEFAULT.buffer();
    // 3. 通过Unpooled创建
    ByteBuf buf3 = Unpooled.buffer();
}
```

#### 3.6.3	ByteBufUtil类

　　ByteBufUtil类提供了用于操作ByteBuf的静态的辅助方法: `hexdump()`和`equals()`

- hexdump() ：以十六进制的表示形式打印ByteBuf的内容，可以用于调试程序时打印 ByteBuf 的内容。非十六进制字符串相比字节而言对用户更友好。 而且十六进制版本可以很容易地转换回实际字节表示。
- boolean equals(ByteBuf, ByteBuf) ：==**判断两个ByteBuf实例的相等性**==，在 实现自己 ByteBuf 的子类时经常用到

### 3.7	派生缓冲区和引用计数
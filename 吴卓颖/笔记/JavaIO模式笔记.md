# JavaIO模式笔记

## 0.0	I/O模型

​	I/O 模型：就是用什么样的通道或者说是通信模式和架构进行数据的传输和接收，很大程度上决定了程序通信的性能，Java 共支持 3 种网络编程的/IO 模型：**BIO、NIO、AIO**
实际通信需求下，要根据不同的业务场景和性能需求决定选择不同的I/O模型

####  	Java BIO

​	同步并阻塞(传统阻塞型)，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器
端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销 【简单示意图

![image-20200615181255063](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20200615181255063.png)

#### 	Java NIO

​	Java NIO ： 同步非阻塞，服务器实现模式为一个线程处理多个请求(连接)，即客户端发送的连接请求都会注
册到多路复用器上，多路复用器轮询到连接有 I/O 请求就进行处理 【简单示意图】

![image-20200615180441015](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20200615180441015.png)

####  	Java AIO

​	Java AIO(NIO.2) ： 异步 异步非阻塞，服务器实现模式为一个有效请求一个线程，客户端的I/O请求都是由OS先完成了再通知服务器应用去启动线程进行处理，一般适用于连接数较多且连接时间较长的应用。

### 0.1	BIO、NIO、AIO 适用场景分析

1. **BIO** 方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4以前的唯一选择，但程序简单易理解。

2. **NIO** 方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，弹幕系统，服务器间通讯等。
    编程比较复杂，JDK1.4 开始支持。

3. **AIO** 方式使用于连接数目多且连接比较长（重操作）的架构，比如相册服务器，充分调用 OS 参与并发操作，编程比较复杂，JDK7 开始支持。

## 1.0	BIO

* Java BIO 就是传统的 java io  编程，其相关的类和接口在 java.io
* BIO(blocking I/O) ： 同步阻塞，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需
    要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，可以通过线程池机制改善(实现多个客户连接服务器).

### 1.1	Java BIO 工作机制

![image-20200618222916021](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20200618222916021.png)

​	对 BIO  编程流程的梳理

1) 服务器端启动一个 **ServerSocket**，注册端口，调用accpet方法监听客户端的Socket连接。
2) 客户端启动 **Socket** 对服务器进行通信，默认情况下服务器端需要对每个客户 建立一个线程与之通讯

### 1.2	传统的BIO编程实例回顾

​	网络编程的基本模型是Client/Server模型，也就是两个进程之间进行相互通信，其中服务端提供位置信（绑定IP地址和端口），客户端通过连接操作向服务端监听的端口地址发起连接请求，基于TCP协议下进行三次握手连接，连接成功后，双方通过网络套接字（Socket）进行通信。

​	传统的同步阻塞模型开发中，服务端ServerSocket负责绑定IP地址，启动监听端口；客户端Socket负责发起连接操作。连接成功后，双方通过输入和输出流进行同步阻塞式通信。 

​	基于BIO模式下的通信，客户端 - 服务端是完==全同步，完全耦合的==。只要一方连接断开，另一方也会跟着断开。 

### 1.3	一对一的BIO

​	Java提供了一个包：java.net下的类都是用于网络通信。Java提供了基于套接字（端口）Socket的网络通信模式，我们基于这种模式就可以直接实现TCP通信。只要用Socket通信，那么就是基于TCP可靠传输通信。

​	功能1：客户端发送一个消息，服务端接口一个消息，通信结束！！

- 创建客户端对象：
        （1）创建一个Socket的通信管道，请求与服务端的端口连接。
        （2）从Socket管道中得到一个字节输出流。
        （3）把字节流改装成自己需要的流进行数据的发送

 - 创建服务端对象：

    ​    （1）注册端口
    ​    （2）开始等待接收客户端的连接,得到一个端到端的Socket管道
    ​    （3）从Socket管道中得到一个字节输入流。
    ​    （4）把字节输入流包装成自己需要的流进行数据的读取。

  - Socket的使用：

    ​    构造器：`public Socket(String host, int port)`
    ​    方法：  `public OutputStream getOutputStream()`：获取字节输出流
    ​           `public InputStream getInputStream()` :获取字节输入流

- ServerSocket的使用：
       	 构造器：`public ServerSocket(int port)`

### 1.4	多发和多收消息

​	在1.3的案例中，**只能实现客户端发送消息，服务端接收消息**，并不能实现反复的收消息和反复的发消息，我们只需要在客户端案例中，加上反复按照行发送消息的逻辑即可！案例代码如下：

#### 			客户端代码如下

```java
public class ClientDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("==客户端的启动==");
        // （1）创建一个Socket的通信管道，请求与服务端的端口连接。
        Socket socket = new Socket("127.0.0.1",8888);
        // （2）从Socket通信管道中得到一个字节输出流。
        OutputStream os = socket.getOutputStream();
        // （3）把字节流改装成自己需要的流进行数据的发送
        PrintStream ps = new PrintStream(os);
        // （4）开始发送消息
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("请说:");
            String msg = sc.nextLine();
            ps.println(msg);
            ps.flush();
        }
    }
}
```

#### 			服务端代码如下

```java
public class ServerDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("==服务器的启动==");
        //（1）注册端口
        ServerSocket serverSocket = new ServerSocket(8888);
        //（2）开始在这里暂停等待接收客户端的连接,得到一个端到端的Socket管道
        Socket socket = serverSocket.accept();
        //（3）从Socket管道中得到一个字节输入流。
        InputStream is = socket.getInputStream();
        //（4）把字节输入流包装成  自己需要的流进行数据的读取。
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        //（5）读取数据
        String line ;
        while((line = br.readLine())!=null){
            System.out.println("服务端收到："+line);
        }
    }
}
```

#### 			小结

* 本案例中确实可以实现客户端多发多收
* 但是服务端只能处理一个客户端的请求，因为服务端是单线程的。一次只能与一个客户端进行消息通信。

### 1.5	BIO模式下接收多个客户端 

​	**如果服务端需要处理很多个客户端的消息通信请求**，此时我们就需要==在服务端引入线程了==，也就是说客户端每发起一个请求，服务端就==创建一个新的线程来处理这个客户端的请求==，这样就实现了一个客户端一个线程的模型，图解模式如下：

![image-20200615181141593](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20200615181141593.png)

#### 	客户端案例代码如下

```java
public class ClientDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("==客户端的启动==");
        // （1）创建一个Socket的通信管道，请求与服务端的端口连接。
        Socket socket = new Socket("127.0.0.1",7777);
        // （2）从Socket通信管道中得到一个字节输出流。
        OutputStream os = socket.getOutputStream();
        // （3）把字节流改装成自己需要的流进行数据的发送
        PrintStream ps = new PrintStream(os);
        // （4）开始发送消息
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("请说:");
            String msg = sc.nextLine();
            ps.println(msg);
            ps.flush();
        }
    }
}
```

#### 	服务端案例代码如下

```java
public class ServerDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("==服务器的启动==");
        // （1）注册端口
        ServerSocket serverSocket = new ServerSocket(7777);
        while(true){
            //（2）开始在这里暂停等待接收客户端的连接,得到一个端到端的Socket管道
            Socket socket = serverSocket.accept();
            new ServerReadThread(socket).start();
            System.out.println(socket.getRemoteSocketAddress()+"上线了！");
        }
    }
}

class ServerReadThread extends Thread{
    private Socket socket;

    public ServerReadThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            //（3）从Socket管道中得到一个字节输入流。
            InputStream is = socket.getInputStream();
            //（4）把字节输入流包装成自己需要的流进行数据的读取。
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            //（5）读取数据
            String line ;
            while((line = br.readLine())!=null){
                System.out.println("服务端收到："+socket.getRemoteSocketAddress()+":"+line);
            }
        }catch (Exception e){
            System.out.println(socket.getRemoteSocketAddress()+"下线了！");
        }
    }
}
```

#### 	小结

* 每个Socket接收到，都会创建一个线程，线程的竞争、切换上下文影响性能；
* .每个线程都会占用栈空间和CPU资源；
* 并不是每个socket都进行IO操作，无意义的线程处理；  
* 客户端的并发访问增加时。服务端将呈现1:1的线程开销，访问量越大，系统将发生线程栈溢出，线程创建失败，最终导致进程宕机或者僵死，从而不能对外提供服务。

### 1.6	伪异步I/O编程

​	在上述案例中：客户端的并发访问增加时。服务端将呈现1:1的线程开销，访问量越大，系统将发生线程栈溢出，线程创建失败，最终导致进程宕机或者僵死，从而不能对外提供服务。

​	面对这种情况，我们可以采用一个伪异步I/O的通信框架，采用线程池和任务队列实现，当客户端接入时，将客户端的==Socket封装成一个Task(该任务实现java.lang.Runnable线程任务接口)==交给后端的线程池中进行处理。

​	JDK的线程池维护一个消息队列和N个活跃的线程，对消息队列中Socket任务进行处理，由于线程池可以设置消息队列的大小和最大线程数，因此，它的资源占用是可控的，无论多少个客户端并发访问，都不会导致资源的耗尽和宕机。

![image-20200619085953166](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20200619085953166.png)

#### 客户端源码分析

```java
public class Client {
   public static void main(String[] args) {
      try {
         // 1.建立一个与服务端的Socket对象
         Socket socket = new Socket("127.0.0.1", 9999);
         // 2.从socket管道中获取一个输出流，写数据给服务端 
         OutputStream os = socket.getOutputStream() ;
         // 3.把输出流包装成一个打印流 
         PrintWriter pw = new PrintWriter(os);
         // 4.反复接收用户的输入 
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         String line = null ;
         while((line = br.readLine()) != null){
            pw.println(line);
            pw.flush();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
```

#### 线程池处理类

```java
public class HandlerSocketThreadPool {
    private ThreadFactory threadFactory;
    private ExecutorService executor;

    public HandlerSocketThreadPool(int maxPoolSize, int queueSize) {
        threadFactory = new ThreadFactory() {
            private AtomicInteger count = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("HandlerSocketThreadPool-" + count.incrementAndGet());
                return t;
            }
        };
        this.executor = new ThreadPoolExecutor(
                8, // 8
                maxPoolSize,
                120L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize),
                threadFactory // 将自定义的ThreadFactory传递给线程池
        );
    }
    public void execute(Runnable task) {
        this.executor.execute(task);
    }
}
```

#### 服务端源码分析

```java
public class Server {
   public static void main(String[] args) {
      try {
         System.out.println("----------服务端启动成功------------");
         ServerSocket ss = new ServerSocket(9999);

         // 一个服务端只需要对应一个线程池
         HandlerSocketThreadPool handlerSocketThreadPool =
               new HandlerSocketThreadPool(3, 1000);

         // 客户端可能有很多个
         while(true){
            Socket socket = ss.accept() ; // 阻塞式的！
            System.out.println("有人上线了！！");
            // 每次收到一个客户端的socket请求，都需要为这个客户端分配一个
            // 独立的线程 专门负责对这个客户端的通信！！
            handlerSocketThreadPool.execute(new ReaderClientRunnable(socket));
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
class ReaderClientRunnable implements Runnable{

   private Socket socket ;

   public ReaderClientRunnable(Socket socket) {
      this.socket = socket;
   }

   @Override
   public void run() {
      try {
         // 读取一行数据
         InputStream is = socket.getInputStream() ;
         // 转成一个缓冲字符流
         Reader fr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(fr);
         // 一行一行的读取数据
         String line = null ;
         while((line = br.readLine())!=null){ // 阻塞式的！！
            System.out.println("服务端收到了数据："+line);
         }
      } catch (Exception e) {
         System.out.println("有人下线了");
      }

   }
}
```

### 小结

* 伪异步io采用了线程池实现，因此避免了为每个请求创建一个独立线程造成线程资源耗尽的问题，但由于底层依然是采用的同步阻塞模型，因此无法从根本上解决问题。
* 如果单个消息处理的缓慢，或者服务器线程池中的全部线程都被阻塞，那么后续socket的i/o消息都将在队列中排队。新的Socket请求将被拒绝，客户端会发生大量连接超时。

## 1.7	基于BIO形式下的文件上传

#### 目标

支持任意类型文件形式的上传。

#### 客户端开发

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

#### 服务端开发

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

### 1.8	Java BIO模式下的端口转发思想

​	需要实现一个客户端的消息可以发送给所有的客户端去接收。（群聊实现）

![image-20200619123304241](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20200619123304241.png)

​	服务端用一个静态的列表维护在线的客户端。发送时再遍历即可。

## 2.0	NIO

	- Java NIO（New IO）也有人称之为 java non-blocking IO是从Java 1.4版本开始引入的一个新的IO API，可以替代标准的Java IO API。NIO与原来的IO有同样的作用和目的，但是使用的方式完全不同，NIO支持==面**向缓冲区**的、基于**通道**==的IO操作。NIO将以更加高效的方式进行文件的读写操作。NIO可以理解为**非阻塞IO**,传统的IO的read和write只能阻塞执行，线程在读写IO期间不能干其他事情，比如调用socket.read()时，如果服务器一直没有数据传输过来，线程就一直阻塞，而NIO中可以配置socket为非阻塞模式。
	- NIO 相关类都被放在 `java.nio` 包及子包下，并且对原 `java.io` 包中的很多类进行改写。
- NIO 有三大核心部分：**Channel( 通道) ，Buffer( 缓冲区), Selector( 选择器)**
- Java NIO 的非阻塞模式，使一个线程从某通道发送请求或者读取数据，但是它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取，而不是保持线程阻塞，所以直至数据变的可以读取之前，该线程可以继续做其他的事情。 非阻塞写也是如此，一个线程请求写入一些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情。
- 通俗理解：NIO 是可以做到用一个线程来处理多个操作的。假设有 1000 个请求过来,根据实际情况，可以分配20 或者 80个线程来处理。不像之前的阻塞 IO 那样，非得分配 1000 个。

### 2.1	NIO 和 BIO 的比较

- BIO 以**流**的方式处理数据,而 NIO 以**块**的方式处理数据,**块 I/O** 的效率比**流 I/O** 高很多

- BIO 是阻塞的，NIO 则是非阻塞的
- BIO 基于==字节流和字符流==进行操作，而 NIO 基于 ==Channel(通道)和 Buffer(缓冲区)==进行操作，数据总是从通道
    读取到缓冲区中，或者从缓冲区写入到通道中。Selector(选择器)用于监听多个通道的事件（比如：连接请求，数据到达等），因此使用单个线程就可以监听多个客户端通道

| NIO                       | BIO                 |
| ------------------------- | ------------------- |
| 面向缓冲区（Buffer）      | 面向流（Stream）    |
| 非阻塞（Non Blocking IO） | 阻塞IO(Blocking IO) |
| 选择器（Selectors）       |                     |

### 2.2	NIO三大核心原理示意图

​	NIO 有三大核心部分：**Channel( 通道) ，Buffer( 缓冲区), Selector( 选择器)**

### Buffer缓冲区

缓冲区本质上是一块可以写入数据，然后可以从中读取数据的内存。这块内存被包装成NIO Buffer对象，并提供了一组方法，用来方便的访问该块内存。相比较直接对数组的操作，Buffer API更加容易操作和管理。

### **Channel（通道）**

​	Java NIO的**通道**类似**流**，但又有些不同：既可以从通道中读取数据，又可以写数据到通道。但流的（input或output)读写通常是单向的。 通道可以非阻塞读取和写入通道，通道可以支持读取或写入缓冲区，也支持异步地读写。

### Selector选择器

Selector是 一个Java NIO组件，可以能够检查一个或多个 NIO 通道，并确定哪些通道已经准备好进行读取或写入。这样，一个单独的线程可以管理多个channel，从而管理多个网络连接，提高效率

![image-20200619153658139](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20200619153658139.png)

* 每个 ==channel== 都会对应一个 ==Buffer==
* 一个==线程==对应==Selector== ， 一个==Selector==对应多个 ==channel(连接)==
* 程序切换到哪个 channel 是由**==事件==**决定的
* Selector 会==根据不同的事件==，在各个通道上切换
* Buffer 就是一个==内存块== ， 底层是一个==数组==
* 数据的读取写入是通过 ==Buffer==完成的 , BIO 中要么是==输入流==，或者是==输出流==, 不能双向，但是 NIO 的 Buffer 是可以读也可以写。
* Java NIO系统的核心在于：**通道(Channel)**和**缓冲区 (Buffer)**。**通道**表示打开到 IO 设备(例如：文件、 套接字)的连接。若需要使用 NIO 系统，需要获取 用于连接 IO 设备的通道以及用于容纳数据的缓冲区。然后操作**缓冲区**，对数据进行处理。简而言之，Channel 负责传输， Buffer 负责存取数据

### 2.3	 NIO核心一：缓冲区(Buffer)

### 缓冲区（Buffer）

​	一个用于特定基本数据类 型的容器。由 java.nio 包定义的，所有缓冲区 都是 Buffer 抽象类的子类.。Java NIO 中的 Buffer 主要用于与 NIO 通道进行 交互，数据是==从通道读入缓冲区，从缓冲区写入通道中的==

![image-20200619163952309](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20200619163952309.png)

### **Buffer 类及其子类**

​	**Buffer** 就像一个数组，可以保存多个相同类型的数据。根据数据类型不同 ，有以下 Buffer 常用子类： 

* ByteBuffer 
* CharBuffer 
* ShortBuffer 
* IntBuffer 
* LongBuffer 
* FloatBuffer 
* DoubleBuffer 

​	上述 Buffer 类 他们都采用相似的方法进行管理数据，只是各自 管理的数据类型不同而已。都是通过如下方法获取一个 Buffer 对象：

```java
static XxxBuffer allocate(int capacity) : 创建一个容量为capacity 的 XxxBuffer 对象
```

### 缓冲区的基本属性

Buffer 中的重要概念： 

* **容量 (capacity)** ：作为一个内存块，Buffer具有一定的固定大小，也称为"容量"，缓冲区容量不能为负，并且==创建后不能更改==。 
* **限制 (limit)**：表示缓冲区中可以操作数据的大小（limit 后数据不能进行读写）。缓冲区的限制不能为负，并且不能大于其容量。 **==写入模式，限制等于buffer的容量。读取模式下，limit等于写入的数据量==**。
* **位置 (position)**：下一个要读取或写入的数据的索引。缓冲区的位置不能为 负，并且不能大于其限制 
* **标记 (mark)与重置 (reset)**：标记是一个索引，通过 Buffer 中的 mark() 方法 指定 Buffer 中一个特定的 **position**，之后可以通过调用 reset() 方法恢复到这 个 position.
    **标记、位置、限制、容量遵守以下不变式： 0 <= mark <= position <= limit <= capacity**
* **图示:**
* ![image-20200619172434538](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20200619172434538.png)

### Buffer常见方法

```java
Buffer clear() 清空缓冲区并返回对缓冲区的引用
Buffer flip() 为 将缓冲区的界限设置为当前位置，并将当前位置重置为 0
int capacity() 返回 Buffer 的 capacity 大小
boolean hasRemaining() 判断缓冲区中是否还有元素
int limit() 返回 Buffer 的界限(limit) 的位置
Buffer limit(int n) 将设置缓冲区界限为 n, 并返回一个具有新 limit 的缓冲区对象
Buffer mark() 对缓冲区设置标记
int position() 返回缓冲区的当前位置 position
Buffer position(int n) 将设置缓冲区的当前位置为 n , 并返回修改后的 Buffer 对象
int remaining() 返回 position 和 limit 之间的元素个数
Buffer reset() 将位置 position 转到以前设置的 mark 所在的位置
Buffer rewind() 将位置设为为 0， 取消设置的 mark
```

​	**注意**

	- rewind 和 flip 都会清除 mark 位置
	- 可以调用 rewind 方法将 position 重新置为 0
- 或者调用 get(int i) 方法获取索引 i 的内容，它不会移动读指针

### ByteBuffer的结构

ByteBuffer 有以下重要属性

* capacity
* position
* limit

一开始

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/0021.png)

写模式下，position 是写入位置，limit 等于容量，下图表示写入了 4 个字节后的状态

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/0018.png)

flip 动作发生后，position 切换为读取位置，limit 切换为读取限制

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/0019.png)

读取 4 个字节后，状态

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/0020.png)

clear 动作发生后，状态

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/0021.png)

compact 方法，是把未读完的部分向前压缩，然后切换至写模式

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/0022.png)



### 缓冲区的数据操作

```java
Buffer 所有子类提供了两个用于数据操作的方法：get()put() 方法
取获取 Buffer中的数据
get() ：读取单个字节
get(byte[] dst)：批量读取多个字节到 dst 中
get(int index)：读取指定索引位置的字节(不会移动 position)
    
放到 入数据到 Buffer 中 中
put(byte b)：将给定单个字节写入缓冲区的当前位置
put(byte[] src)：将 src 中的字节写入缓冲区的当前位置
put(int index, byte b)：将指定字节写入缓冲区的索引位置(不会移动 position)
```

**使用Buffer读写数据一般遵循以下四个步骤：**

* 1.写入数据到Buffer
* 2.调用flip()方法，转换为读取模式
* 3.从Buffer中读取数据
* 4.调用buffer.clear()方法或者buffer.compact()方法清除缓冲区

### 直接与非直接缓冲区

​	根据官方文档的描述：

​	`byte byffer`可以是两种类型，一种是基于直接内存（也就是**非堆内存**）；另一种是非直接内存（也就是**堆内存**）。对于直接内存来说，JVM将会在IO操作上具有更高的性能，因为它直接作用于本地系统的IO操作。而非直接内存，也就是堆内存中的数据，如果要作IO操作，会先从本进程内存复制到直接内存，再利用本地IO处理。

​	从数据流的角度，非直接内存是下面这样的作用链：

```
本地IO-->直接内存-->非直接内存-->直接内存-->本地IO
```

​	而直接内存是：

```
本地IO-->直接内存-->本地IO
```

​	很明显，在做IO处理时，比如网络发送大量数据时，直接内存会具有更高的效率。直接内存使用`allocateDirect`创建，但是它比申请普通的堆内存需要耗费更高的性能。不过，这部分的数据是在JVM之外的，因此它不会占用应用的内存。所以呢，当==你有很大的数据要缓存，并且它的生命周期又很长，那么就比较适合使用直接内存==。只是一般来说，如果不是能带来很明显的性能提升，还是推荐直接使用堆内存。字节缓冲区是直接缓冲区还是非直接缓冲区可通过调用其 isDirect()  方法来确定。

​	**使用场景**

- 1 有很大的数据需要存储，它的生命周期又很长
- 2 适合频繁的IO操作，比如网络并发场景



#### ⚠️ Buffer 的线程安全

> Buffer 是**非线程安全的**

## 2.4	NIO核心二：通道(Channel)

### 2.41	通道Channel概述

​	**通道**（Channel）：由 java.nio.channels 包定义 的。Channel 表示 IO 源与目标打开的连接。 Channel 类似于传统的“流”。只不过 Channel 本身不能直接访问数据，Channel 只能与 Buffer 进行交互。

1、 NIO 的通道类似于流，但有些区别如下：

* 通道可以==同时进行读写==，而流只能读或者只能写

* 通道可以实现==异步读写数据==

* 通道可以==从缓冲读数据，也可以写数据到缓冲==:

2、BIO 中的 stream 是**单向**的，例如 FileInputStream 对象只能进行读取数据的操作，而 NIO 中的通道(Channel)是双向的，可以读操作，也可以写操作。

3、Channel 在 NIO 中是一个**接口**

```java
public interface Channel extends Closeable{}
```

### 2.42	常用的Channel实现类

* `FileChannel`：用于读取、写入、映射和操作文件的通道。
* `DatagramChannel`：通过 UDP 读写网络中的数据通道。
* `SocketChannel`：通过 TCP 读写网络中的数据。
* `ServerSocketChannel`：可以监听新进来的 TCP 连接，对每一个新进来的连接都会创建一个SocketChannel。 【ServerSocketChanne 类似 ServerSocket , SocketChannel 类似 Socket】

### 2.43	FileChannel 类

​	获取通道的一种方式是==对支持通道的对象调用getChannel() 方法==。支持通道的类如下：

* `FileInputStream`
* `FileOutputStream`
* `RandomAccessFile`
* `DatagramSocket`
* `Socket`
* `ServerSocket`
    获取通道的其他方式是使用 Files 类的静态方法 newByteChannel() 获取字节通道。或者通过通道的静态方法 open() 打开并返回指定通道

### 2.44	FileChannel的常用方法

```java
int read(ByteBuffer dst) 从 从  Channel 到 中读取数据到  ByteBuffer
long  read(ByteBuffer[] dsts) 将 将  Channel 到 中的数据“分散”到  ByteBuffer[]
int  write(ByteBuffer src) 将 将  ByteBuffer 到 中的数据写入到  Channel
long write(ByteBuffer[] srcs) 将 将  ByteBuffer[] 到 中的数据“聚集”到  Channel
long position() 返回此通道的文件位置
FileChannel position(long p) 设置此通道的文件位置
long size() 返回此通道的文件的当前大小
FileChannel truncate(long s) 将此通道的文件截取为给定大小
void force(boolean metaData) 强制将所有对此通道的文件更新写入到存储设备中
```



### 2.45	FileChannel文件编程

#### ⚠️ FileChannel 工作模式

> FileChannel 只能工作在阻塞模式下

#### 2.45.1	获取

​	不能直接打开 FileChannel，必须通过 FileInputStream、FileOutputStream 或者 RandomAccessFile 来获取 FileChannel，它们都有 getChannel 方法

* 通过 FileInputStream 获取的 channel 只能读
* 通过 FileOutputStream 获取的 channel 只能写
* 通过 RandomAccessFile 是否能读写根据构造 RandomAccessFile 时的读写模式决定

#### 2.45.2	读取

​	会从 channel 读取数据填充 ByteBuffer，返回值表示读到了多少字节，-1 表示到达了文件的末尾

```java
int readBytes = channel.read(buffer);
```

#### 2.45.3	写入

​	写入的正确姿势如下， SocketChannel

```java
ByteBuffer buffer = ...;
buffer.put(...); // 存入数据
buffer.flip();   // 切换读模式

while(buffer.hasRemaining()) {
    channel.write(buffer);
}
```

​	在 while 中调用 channel.write 是因为 write 方法并不能保证一次将 buffer 中的内容全部写入 channel

#### 2.45.4	关闭

​	channel 必须关闭，不过调用了 FileInputStream、FileOutputStream 或者 RandomAccessFile 的 close 方法会间接地调用 channel 的 close 方法

#### 2.45.5	位置

​	获取当前位置

```java
long pos = channel.position();
```

​	设置当前位置

```java
long newPos = ...;
channel.position(newPos);
```

​	设置当前位置时，如果设置为文件的末尾

* 这时读取会返回 -1 
* 这时写入，会追加内容，但要注意如果 position 超过了文件末尾，再写入时在新内容和原末尾之间会有空洞（00）

#### 2.45.6	大小

​	使用 size()方法获取文件的大小

#### 2.45.7	强制写入

​	操作系统出于性能的考虑，会将数据缓存，不是立刻写入磁盘。可以调用 force(true)  方法将文件内容和元数据（文件的权限等信息）立刻写入磁盘

## 2.5	两个 Channel 传输数据

```java
String FROM = "helloword/data.txt";
String TO = "helloword/to.txt";
long start = System.nanoTime();
try (FileChannel from = new FileInputStream(FROM).getChannel();
     FileChannel to = new FileOutputStream(TO).getChannel();
    ) {
    from.transferTo(0, from.size(), to);
} catch (IOException e) {
    e.printStackTrace();
}
long end = System.nanoTime();
System.out.println("transferTo 用时：" + (end - start) / 1000_000.0);
```

​	使用`FileChannel.transferTo()`方法。此方法一次最高传输2g的数据。

## 3.0	NIO核心三：选择器(Selector)

### 选择器(Selector)概述

​	选择器（Selector） 是 `SelectableChannle` 对象的多路复用器，Selector 可以同时监控多个 `SelectableChannel` 的 IO 状况，也就是说，利用 Selector可使一个单独的线程管理多个 Channel。Selector 是非阻塞 IO 的核心

![image-20200619230246145](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/image-20200619230246145.png)

* Java 的 NIO，用非阻塞的 IO 方式。可以用一个线程，处理多个的客户端连接，就会使用到 Selector(选择器)
* Selector 能够检测==多个注册的通道上是否有事件发生==(注意:多个 Channel 以事件的方式可以注册到同一个
    Selector)，如果有事件发生，便获取事件然后针对每个事件进行相应的处理。这样就可以只用一个单线程去管
    理多个通道，也就是管理多个连接和请求。
* 只有在 **连接/通道** ==真正有读写事件发生时==，才会进行读写，就大大地减少了系统开销，并且不必为每个连接都
    创建一个线程，不用去维护多个线程
* 避免了多线程之间的上下文切换导致的开销

### 选择器（Selector）的应用

​	创建 Selector ：通过调用 Selector.open() 方法创建一个 Selector。

```java
Selector selector = Selector.open();
```

​	向选择器注册通道：SelectableChannel.register(Selector sel, int ops)

```java
//1. 获取通道
ServerSocketChannel ssChannel = ServerSocketChannel.open();
//2. 切换非阻塞模式
ssChannel.configureBlocking(false);
//3. 绑定连接
ssChannel.bind(new InetSocketAddress(9898));
//4. 获取选择器
Selector selector = Selector.open();
//5. 将通道注册到选择器上, 并且指定“监听接收事件”
ssChannel.register(selector, SelectionKey.OP_ACCEPT);
```

​	当调用 register(Selector sel, int ops) 将通道注册选择器时，选择器对通道的监听事件，需要通过第二个参数 ops 指定。可以监听的事件类型（用 可使用 SelectionKey  的四个常量 表示）：

* 读 : SelectionKey.OP_READ
* 写 : SelectionKey.OP_WRITE
* 连接 : SelectionKey.OP_CONNECT
* 接收 : SelectionKey.OP_ACCEPT
* 若注册时不止监听一个事件，则可以使用==“位或”操作符连接==。

```java
int interestSet = SelectionKey.OP_READ|SelectionKey.OP_WRITE 
```

#### 监听 Channel 事件

可以通过下面三种方法来监听是否有事件发生，方法的返回值代表有多少 channel 发生了事件

方法1，阻塞直到绑定事件发生

```java
int count = selector.select();
```



方法2，阻塞直到绑定事件发生，或是超时（时间单位为 ms）

```java
int count = selector.select(long timeout);
```



方法3，不会阻塞，也就是不管有没有事件，立刻返回，自己根据返回值检查是否有事件

```java
int count = selector.selectNow();
```

####  select 何时不阻塞

> * 事件发生时
>     * 客户端发起连接请求，会触发 accept 事件
>     * 客户端发送数据过来，客户端正常、异常关闭时，都会触发 read 事件，另外如果发送的数据大于 buffer 缓冲区，会触发多次读取事件
>     * channel 可写，会触发 write 事件
>     * 在 linux 下 nio bug 发生时
> * 调用 selector.wakeup()
> * 调用 selector.close()
> * selector 所在线程 interrupt



## 3.0	网络编程

### 3.1	非阻塞 vs 阻塞

#### 阻塞

* 阻塞模式下，相关方法都会导致线程暂停
    * ServerSocketChannel.accept 会在没有连接建立时让线程暂停
    * SocketChannel.read 会在没有数据可读时让线程暂停
    * 阻塞的表现其实就是线程暂停了，暂停期间不会占用 cpu，但线程相当于闲置
* 单线程下，阻塞方法之间相互影响，几乎不能正常工作，需要多线程支持
* 但多线程下，有新的问题，体现在以下方面
    * 32 位 jvm 一个线程 320k，64 位 jvm 一个线程 1024k，如果连接数过多，必然导致 OOM，并且线程太多，反而会因为频繁上下文切换导致性能降低
    * 可以采用线程池技术来减少线程数和线程上下文切换，但治标不治本，如果有很多连接建立，但长时间 inactive，会阻塞线程池中所有线程，因此不适合长连接，只适合短连接

#### 非阻塞

* 非阻塞模式下，相关方法都会不会让线程暂停
    * 在 ServerSocketChannel.accept 在没有连接建立时，会返回 null，继续运行
    * SocketChannel.read 在没有数据可读时，会返回 0，但线程不必阻塞，可以去执行其它 SocketChannel 的 read 或是去执行 ServerSocketChannel.accept 
    * 写数据时，线程只是等待数据写入 Channel 即可，无需等 Channel 通过网络把数据发送出去
* 但非阻塞模式下，即使没有连接建立，和可读数据，线程仍然在不断运行，白白浪费了 cpu
* 数据复制过程中，线程实际还是阻塞的（AIO 改进的地方）

#### 多路复用

单线程可以配合 Selector 完成对多个 Channel 可读写事件的监控，这称之为多路复用

* 多路复用仅针对网络 IO。普通文件 IO 没法利用多路复用
* 如果不用 Selector 的非阻塞模式，线程大部分时间都在做无用功，而 Selector 能够保证
    * 有可连接事件时才去连接
    * 有可读事件才去读取
    * 有可写事件才去写入
        * 限于网络传输能力，Channel 未必时时可写，一旦 Channel 可写，会触发 Selector 的可写事件

















































## 4.0	AIO编程

* Java AIO(NIO.2) ： 异步非阻塞，服务器实现模式为一个有效请求一个线程，客户端的I/O请求都是由OS先完成了再通知服务器应用去启动线程进行处理。

```java
AIO
异步非阻塞，基于NIO的，可以称之为NIO2.0
    BIO                   NIO                              AIO        
Socket                SocketChannel                    AsynchronousSocketChannel
ServerSocket          ServerSocketChannel	       AsynchronousServerSocketChannel
```


与NIO不同，当进行读写操作时，只须直接调用API的read或write方法即可, 这两种方法均为异步的，对于读操作而言，当有流可读取时，操作系统会将可读的流传入read方法的缓冲区,对于写操作而言，当操作系统将write方法传递的流写入完毕时，操作系统主动通知应用程序

即可以理解为，read/write方法都是异步的，完成后会主动调用回调函数。在JDK1.7中，这部分内容被称作NIO.2，主要在Java.nio.channels包下增加了下面四个异步通道：

```java
	AsynchronousSocketChannel
	AsynchronousServerSocketChannel
	AsynchronousFileChannel
	AsynchronousDatagramChannel
```

​	**BIO、NIO、AIO：**

- Java BIO ： 同步并阻塞，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，当然可以通过线程池机制改善。
- Java NIO ： 同步非阻塞，服务器实现模式为一个请求一个线程，即客户端发送的连接请求都会注册到多路复用器上，多路复用器轮询到连接有I/O请求时才启动一个线程进行处理。
- Java AIO(NIO.2) ： 异步非阻塞，服务器实现模式为一个有效请求一个线程，客户端的I/O请求都是由OS先完成了再通知服务器应用去启动线程进行处理。

​	**BIO、NIO、AIO适用场景分析:**

- BIO方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4以前的唯一选择，但程序直观简单易理解。
- NIO方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，并发局限于应用中，编程比较复杂，JDK1.4开始支持。
- AIO方式使用于连接数目多且连接比较长（重操作）的架构，比如相册服务器，充分调用OS参与并发操作，编程比较复杂，JDK7开始支持。Netty!



## 5.0	Path 和 Paths 类和Files类

* Path 用来表示文件路径
* Paths 是工具类，用来获取 Path 实例

```java
Path source = Paths.get("1.txt"); // 相对路径 使用 user.dir 环境变量来定位 1.txt

Path source = Paths.get("d:\\1.txt"); // 绝对路径 代表了  d:\1.txt

Path source = Paths.get("d:/1.txt"); // 绝对路径 同样代表了  d:\1.txt

Path projects = Paths.get("d:\\data", "projects"); // 代表了  d:\data\projects
```

* `.` 代表了当前路径
* `..` 代表了上一级路径

​	例如目录结构如下

```
d:
	|- data
		|- projects
			|- a
			|- b
```

​	代码

```java
Path path = Paths.get("d:\\data\\projects\\a\\..\\b");
System.out.println(path);
System.out.println(path.normalize()); // 正常化路径
```

​	会输出

```
d:\data\projects\a\..\b
d:\data\projects\b
```

### 5.1	Files

	- 方法:检查文件是否存在

```java
Path path = Paths.get("helloword/data.txt");
System.out.println(Files.exists(path));
```

	- 方法:创建一级目录

```java
Path path = Paths.get("helloword/d1");
Files.createDirectory(path);
```

* 如果目录已存在，会抛异常 FileAlreadyExistsException
* 不能一次创建多级目录，否则会抛异常 NoSuchFileException



	- 方法:创建多级目录用

```java
Path path = Paths.get("helloword/d1/d2");
Files.createDirectories(path);
```

	- 方法:拷贝文件

```java
Path source = Paths.get("helloword/data.txt");
Path target = Paths.get("helloword/target.txt");

Files.copy(source, target);
```

* 如果文件已存在，会抛异常 `FileAlreadyExistsException`

- 如果希望用 source 覆盖掉 target，需要用 StandardCopyOption 来控制

```java
Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
```

- 方法:移动文件

```java
Path source = Paths.get("helloword/data.txt");
Path target = Paths.get("helloword/data.txt");

Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
```

* `StandardCopyOption.ATOMIC_MOVE` 保证文件移动的原子性



- 方法:删除文件

```java
Path target = Paths.get("helloword/target.txt");

Files.delete(target);
```

* 如果文件不存在，会抛异常 `NoSuchFileException`



- 方法:删除目录

```java
Path target = Paths.get("helloword/d1");

Files.delete(target);
```

* 如果目录还有内容，会抛异常` DirectoryNotEmptyException`






























































# Java并发工具JUC包笔记

## 0.0	线程安全、线程同步与锁

​	Java无法直接操作硬件，实际上它无权开启线程。

### 0.1	线程安全

 - 安全问题出现的条件

    - 是多线程环境


    - 有共享数据


    - 有多条语句操作共享数据

- 如何解决多线程安全问题呢?

    - 基本思想：让程序没有安全问题的环境

- 怎么实现呢?

    - 把多条语句操作共享数据的代码给**锁**起来，让任意时刻只能有一个线程执行即可

    - Java提供了同步代码块的方式来解决

### 0.2	线程同步

- **同步代码块**格式：

    ```java
    synchronized(任意对象) { 
    	多条语句操作共享数据的代码 
    }
    ```

    **synchronized** */sɪŋkrənaɪzd/*(任意对象)：就相当于给代码加**锁**了，任意对象就可以看成是一把**锁**。**锁对象**一定要是唯一的。

    `wait()`方法只能在同步代码块中使用。

- 同步的好处和弊端  

    - 好处：解决了多线程的数据安全问题

    - 弊端：当线程很多时，因为每个线程都会去判断同步上的锁，这是<u>很耗费资源</u>的，无形中会降低程序的运行效率

- **同步方法**的格式

    **同步方法**：就是把synchronized关键字加到方法上的方法

    ```java
    修饰符 synchronized 返回值类型 方法名(方法参数) { 
    	方法体；
    }
    ```

    同步方法的锁对象是什么呢?

    ​	答:**this**, 此时锁对象不唯一，但是==同一个对象的两个同步方法不能同时运行。==但两个对象的一个同步方法可以同时运行。

- **静态同步**方法

    **静态方法**：就是把synchronized关键字加到静态方法上

    ```java
    修饰符 static synchronized 返回值类型 方法名(方法参数) { 
    	方法体；
    }
    ```

    同步静态方法的锁对象是什么呢?

    ​	类名.**class**, 字节码文件对象，此时锁对象一定是**唯一**的。永远只能有一个线程运行此方法

#### 0.35	sleep和wait的区别

1. sleep是Thread类的本地方法；wait是Object类的方法。
2. sleep**不释放锁**；wait**释放锁**。
3. sleep不需要和synchronized关键字一起使用；wait必须和synchronized代码块一起使用。
4. sleep不需要被唤醒（时间到了自动退出阻塞）；wait需要被唤醒。
5. sleep一般用于==当前线程休眠==，或者轮循暂停操作；wait则多==用于多线程之间的通信==。

### 0.3	锁(Lock)

​	锁（Lock）是一种**同步机制**，用于==控制多个线程对共享资源的访问==。锁提供了互斥（mutual exclusion）的能力，确保同一时刻只有一个线程可以访问被锁定的资源，从而避免数据竞争和不一致性。

​	Java中锁主要有两种类型:隐式锁(synchronized)和显式锁(java.util.concurrent.locks包)

​	虽然我们可以理解同步代码块和同步方法的锁对象问题，但是我们==并没有直接看到==在哪里加上了锁，在哪里释放了锁，为了更清晰的表达如何加锁和释放锁，JDK5以后提供了一个`java.util.concurrent`并发编程包，其中有新的锁对象`Lock`。`Lock`是接口不能直接实例化，

- ReentrantLock类
- ReentrantReadWriteLock.ReadLock
- ReentrantReadWriteLock.WriteLock

​	这里采用它的实现类`ReentrantLock`来实例化:

- ReentrantLock构造方法

    | 方法名          | 说明                        |
    | --------------- | --------------------------- |
    | ReentrantLock() | 创建一个ReentrantLock的实例 |

- 加锁解锁方法

    | 方法名            | 说明           |
    | ----------------- | -------------- |
    | boolean trylock() | 查看锁是否可用 |
    | void lock()       | 获得锁         |
    | void unlock()     | 释放锁         |

#### 0.31	公平锁和非公平锁（锁的底层）

​	公平锁：十分公平，不能插队。
​	非公平锁：十分不公平，可以插队。（默认非公平锁）

#### 0.32	Lock锁和synchronized的区别

1. Synchronized是**内置Java关键字**；Lock是一个**Java类**。
2. Synchronized无法判断获取锁的状态；Lock可以判断==是否获取到了锁==。（boolean b = lock.tryLock();）
3. Synchronized会**自动**释放锁；Lock必须要**手动**释放锁，如果不释放锁，死锁。
4. Synchronized线程1获得锁阻塞时，线程2会一直等待下去；Lock锁线程1获得锁阻塞时，线程2等待足够长的时间后==中断等待，去做其他的事==。
5. Synchronized可重入锁，不可以中断的，非公平；Lock，可重入锁，可以判断锁，非公平（可以自己设置）。
     lock.lockInterruptibly();方法：当两个线程同时通过该方法想获取某个锁时，假若此时线程A获取到了锁，而线程B只有在等待，那么对==线程B调用threadB.interrupt()方法==能够中断线程B的等待过程。
6. Synchronized适合锁少量的代码同步问题；Lock适合锁大量的同步代码。

### 0.4	虚假唤醒问题

​	虚假唤醒是一种现象，它只会出现在多线程环境中，指的是在多线程环境下，多个线程等待同一个条件时，需要其他线程执行`notify()`或者`notifyAll()`方法去唤醒它们，假如多个线程都被唤醒了，但是只有其中一部分是有用的唤醒操作，其余的唤醒都是无用功(甚至负功)；对于不应该被唤醒的线程而言，便是虚假唤醒。

#### 0.41	原因

​	虚假唤醒的原因是在判断条件是否满足时，==使用了if语句而不是while语句==。如果使用了if语句，当线程被唤醒后，就会继续执行下面的代码，而不会再次检查条件是否满足。这样就可能导致线程执行了不应该执行的操作，比如消费了不存在的资源。

#### 0.42	避免方法

​	虚假唤醒的避免方法是在==使用wait()和notify()方法时，使用while循环而不是if语句来判断条件是否满足==。这样可以保证每次被唤醒后都会重新检查条件是否满足，如果不满足就继续等待，直到满足为止

## 1.0	JUC包

​	JUC（Java.util.concurrent）是 Java 标准库中的一个包，提供了对并发编程的支持。JUC 包包含了一组类、接口和工具，用于帮助开发者处理多线程编程、并发控制、并发数据结构等方面的任务。

​	JUC包中的常用类和接口有:

1. `Lock` 接口和 ReentrantLock 类：提供了显示的锁定机制，取代了传统的 synchronized 关键字，提供更灵活的线程同步和互斥机制。

2. `Condition` 接口：与锁（Lock）配合使用，实现线程间的协调和通信。

3. `CountDownLatch`（闭锁） :是一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待

4. `CyclicBarrier`（栅栏）: 之所以叫barrier，是因为是一个同步辅助类，允许一组线程互相等待，直到到达某个公共屏障点 ，并且在释放等待线程后可以重用。

5. `Semaphore` 类：实现信号量机制，用于控制同时访问某个资源的线程数量。

6. `CountDownLatch` 类：实现倒计时门闩，允许一个或多个线程等待其他线程的完成。

7. `CyclicBarrier` 类：实现循环栅栏，允许一组线程相互等待，直到到达某个公共屏障点。

8. `Executors` 类：提供了一系列的工具方法，用于创建和管理线程池。是Java里面线程池的顶级接口，但它只是一个执行线程的工具，真正的线程池接口是ExecutorService，里面包含的类有：

    - `ScheduledExecutorService`: 解决那些需要任务重复执行的问题

    - `ScheduledThreadPoolExecutor`: 周期性任务调度的类实现

    - `atomic(原子性包)`：是JDK提供的一组原子操作类，

9. `ConcurrentHashMap` 类：是线程安全的哈希表实现，支持高并发访问。

10. `BlockingQueue` 接口和相关实现类（如 ArrayBlockingQueue、LinkedBlockingQueue）：提供了可阻塞的队列，用于线程间的数据交换和同步。

11. `Future` 接口和相关实现类（如 FutureTask）：表示一个异步计算的结果，可以用于获取任务的执行状态和结果。

## 2.0	`Condition`——精准的通知和唤醒线程

​	Condition依赖于Lock接口，生成一个Condition的基本代码是`lock.newCondition()`。

​	调用Condition的await()和signal()方法，都必须在lock保护之内，就是说必须在lock.lock()和lock.unlock之间才可以使用。

- Conditon中的await()对应Object的wait()；
- Condition中的signal()对应Object的notify()；

- Condition中的signalAll()对应Object的notifyAll()

​	最后也可以通过==不同锁构造的不同Condition来精确地让其对应的不同线程唤醒和等待(signal()和await())。==

## 3.0	八锁现象

​	八锁现象就是关于锁的八个问题，在synchronized方法、static方法、多个对象的碰撞中出现的各种有关锁和执行权的问题。解决这些问题只需把握两个核心

1. ==哪些代码被锁了==?
2. ==锁是哪个对象==(并判断是否为同一个)?

## 4.0	集合类不安全

​	List、ArrayList、HashSet和HashMap 等集合在并发多线程条件下，不能实现数据共享，多个线程同时调用一个集合对象时候就会出现并发修改异常`ConcurrentModificationException`。

### 4.1	List集合

​	针对List这一种集合，有以下三种解决方案

1. ```java
    List<String> list = new Vector<>();
    ```

2. ```java
    List<String> list = Collections.synchronizedList(new ArrayList<>());
    ```

3. ```java
    List<String> list = new CopyOnWriteArrayList<>();
    ```

​	Vector的解决方案效率较低。使用JUC包的CopyOnWriteArrayList<>效率更高。

​	概念：CopyOnWrite写入时复制，计算机程序设计语言的一种优化策略。(保证效率和性能问题)

### 4.2	HashSet集合

 	解决方案：

1. ```java
    Set<String> strings = Collections.synchronizedSet(new HashSet<>());
    ```

2. ```java
    Set<String> strings = new CopyOnWriteArraySet<>();
    ```

    Hashset集合的底层是Hashmap的key

### 4.3	HashMap集合

​	解决方案：

1. ```java
    Map<String> map = Collections.synchronizedMap(new HashMap<>());
    ```

2. ```java
    Map<String, String> concurrentHashMap = new ConcurrentHashMap<>();
    ```

    

## 5.0	Callable接口和FutureTask

![callable](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/callable.png)

​	Callable接口类似于Runnable接口，其本身是线程第三种创建方式。它有以下特点:

1. 可以抛出异常。
2. 可以有返回值。
3. 方法不同与Runnable接口。其实现类需要重写call()方法。



### 5.1	FutureTask

![futuretask](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/futuretask.png)

​	可使用 `FutureTask` 包装 `Callable或 Runnable` 对象。==因为  `FutureTask` 实现了 `Runnable`==，所以可将 `FutureTask` 提交给 `Executor` 执行。 

​	逻辑关系如下

```java
new Thread(new Runnable()).start();
//	↓
new Thread(new FutureTask<V>()).start();
//	↓
new Thread(new FutureTask<V>(Callable callable)).start();
//	可使用Lambda表达式
```

​	示例代码:

```java
new Thread(new FutureTask<>(()->{
            for (int i = 1; i <= 100; i++) {
                System.out.println( i + "  " + Thread.currentThread().getName() + "  " + UUID.randomUUID().toString().substring(0, 5) + "  " + "表白" + i + "次");
            }
            //返回值就表示线程运行完毕之后的结果
            return "0";
        }), "A").start();
```

​	`futureTask.get()`结果可能需要等待，会阻塞！最好放在最后。

## 6.0	常用的辅助类

### 6.1	CountDownLatch-减少计数（人走完再关门）

​	可以当成一个可以在线程间使用的==减法计数器==。

​	主要方法:

```java
CountDownLatch countDownLatch = new CountDownLatch(int number);

countDownLatch.countDown(); // 数量-1

countDownLatch.await(); // 线程开始阻塞，等待计数器归零，然后再向下执行
```

- CountDownLatch主要有两个方法:`countDown`和`await`。
- 当一个或多个线程调用await方法时，这些线程会阻塞。
- 其它线程调用countDown方法会将计数器减1(调用countDown方法的线程不会阻塞)
- 当计数器的值变为0时，因await方法阻塞的线程会被唤醒，继续执行。

​	代码示例:

```java
	CountDownLatch downLatch = new CountDownLatch(6);
	for (int i = 0; i < 6; i++) {
    	new Thread(() -> {
    	    System.out.println(Thread.currentThread().getName() + "\t离开教室！");
     	   //每走一个同学，就让计数器对象 - 1
      	  downLatch.countDown();
   	 },String.valueOf(i)).start();
	}
	//	在所有同学走人之前，也就是计数器没有为0之前
	//	主线程（班长线程）必须await
	//	必须等到计数器由6变为0，主线程才可以继续运行
	downLatch.await();
	System.out.println(Thread.currentThread().getName() + "\t班长关灯走人");
```

### 6.2	CyclicBarrier-循环栅栏（人到齐再开会）

​	可以当成一个可以在线程间使用的==加法计数器==。

> CyclicBarrier的字面意思是可循环（Cyclic）使用的屏障（Barrier）。它要做的事情是，让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续干活。CyclicBarrier的await()方法类似上文countDown()和await()的结合，会使线程==被“屏障”拦截==

​	主要方法:

```java
CyclicBarrier cyclicbarrier = new CyclicBarrier(int number);
CyclicBarrier cyclicbarrier2 = CyclicBarrier(int parties, Runnable barrierAction)
   //创建一个新的 CyclicBarrier，它将在给定数量的参线程处于等待状态时启动，并在启动 barrier 时执行给定的屏障操作，该操作由最后一个进入 barrier 的线程执行!!!
cyclicbarrier.await(); // 线程开始阻塞，同时计数器++，待到计数器到达规定的number，然后再向下执行
```

- 创建一个CyclicBarrier对象，需要传入要求的线程数和一个Runnable对象，这个对象定义了==**最后一个**进入barrier的线程==要执行的方法

​	示例代码:

```java
// 相当于加法计数器
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        // 集齐七颗龙珠召唤神龙
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> {// 如果计数器为7，线程只有6个，则会等待，不进行召唤神龙
            System.out.println("召唤神龙");
        });
        for (int i = 0; i < 7; i++) {
            final int temp = i;
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "收集" + temp + "个龙珠！");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
```

### 6.3	Semaphore-信号灯（[ˈseməfɔː(r)]）

​	信号量主要用于两个目的，一个是用于==多个共享资源的互斥使用==，另一个==用于并发线程数的控制==。即限流。

​	主要方法:

```javascript
Semaphore semaphore = new CyclicBarrier(int number);// 设置最大流量
semaphore.acquire();//	获得一个信号量，假设已经满了则等待，等待其他线程释放信号量。
semaphore.release();//	释放一个信号量，会将当前的信号量释放+1，然后唤醒一个被阻塞的线程。
```

​	示例代码:

```java
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < 6; i++) {
            new Thread(()->{
                try {
                    semaphore.acquire();// 得到
                    System.out.println(Thread.currentThread().getName()+"抢到车位！");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName()+"离开车位！");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();// 释放
                }
            }).start();
        }
```

​	将信号量初始化为  **1**，使得它在使用时最多只有一个可用的许可，从而可用作一个相互排斥的锁。这通常也称为*二进制信号量*，因为它只能有两种状态：一个可用的许可，或零个可用的许可。按此方式使用时，二进制信号量具有某种属性（与很多  `Lock`的实现不同），即==可以由线程释放“锁”，而不是由所有者（因为信号量没有所有权的概念）==。在某些专门的上下文（如死锁恢复）中这会很有用。

## 7.0	读写锁

​	`ReadWriteLock`接口有一个实现类`ReentrantReadWriteLock`类。其特点是:==读可以被多个线程同时读，写的时候只能有一个线程去写==

​	主要方法：

```java
ReadWriteLock readWriteLock = new ReentrantReadWriteLock();// 获取读写锁
ReadLock readLock();//	readWriteLock对象的方法，获取一个ReadLock。可用链式编程.lock()直接上锁
WriteLock writeLock();//	readWriteLock对象的方法，获取一个WriteLock。可用链式编程.lock()直接上锁
```


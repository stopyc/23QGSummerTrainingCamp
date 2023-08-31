# Websocket笔记

## 0.0	为什么要有websocket协议

​	http协议下，服务器不会主动向客户端发起请求，只会响应。在这种状态下，如果要网页主动刷新反馈，如页游这种，会一直更新数据的情况。

​	一种常见方案是定时向服务器发请求来刷新，用户层面没感知，但其实一直发请求实际是由带宽占用的。微信的网页端登陆采取的就是这种方案，每一到两秒询问一次后台用户是否已经扫码。

​	方案二，**长轮询**，客户端发起请求后超时时间设置的比较长（如1min），若此时间内服务器端有新内容推送过来，则可以立刻响应。而且每1min才发一次请求，超时后立刻重新发，可以减少一些网络带宽占用。

​	http本质还是一个半双工协议，因此像游戏这种需要大量主动发送数据的场景还是不适用。于是就有了websocket协议。

### 0.1	websocket基础知识

​	websocket是基于TCP协议的一种新协议(HTTP协议也是基于TCP协议的)。其完全继承了TCP协议的全双工[^1]特性。

​	在一般的网页应用中(包括页游),如果要建立websocket协议的通信，一般都是先发一次HTTP请求，请求中会携带一些特殊的请求头，例如Connection、Upgrade、Sec-WebSocket-Key等，服务端如果响应成功，则会在响应头中设置响应码为101，并将Sec-WebSocket-Key经加密算法处理后得到字符串放在响应中返回，客户端将自己的Sec-WebSocket-Key经加密算法处理后得到字符串与服务端返回的进行比较，如果相同，则websocket通信就被建立起来了。

### 0.2	ws的数据格式

​	opcde：用定义帧的数据类型

​	paload长度：定义该数据帧的长度，选用最开始7为做标志位，若是0-125则只看这7位（2^7=128），而126则使用扩展payload长度，到128刚好可以扩展三次，每个扩展长度16位。

​	使用扩展位时，扩展位存放实际长度，126 7 8**==仅作标记用==**。

​	payload数据：实际要传输的数据。

![websocket数据组成](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/websocket%E6%95%B0%E6%8D%AE%E7%BB%84%E6%88%90.png)

## 1.0	websocket实操

### 1.1	导入依赖

​	spring-boot-starter-websocket

### 1.2	注入一个ServerEndpointExporter的Bean

​	该Bean会自动注册使用@ServerEndpoint注解申明的websocket endpoint。当时没用SpringBoot其实就是在WebSocketServer类上的注解加了一个属性指定配置器为该类的字节码文件。

![websocketconfig](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/websocketconfig.png)



### 1.3	配置拦截器允许静态资源被访问

​	马赛克部分需要到时候根据实际情况确立。

![resourcehandlers](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/resourcehandlers.png)

### 1.4	编写WebSocketServer的代码

#### 1.41	类

​	其实实际上的实现和我当时最终考核上做的东西很像。只是加上了一些SpringBoot的注解。

![websocketserver1](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/websocketserver1.png)

​	@ServerEndpoint里的url路径就是建立websocket通信通道的路径。路径参数之后有用。

#### 1.42	方法

​	以**@OnOpen**为例

![websocketserver2](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/websocketserver2.png)

​	用静态常量SESSION_MAP维护每个建立的Session。用户名为MAP的key，充当索引的作用。

​	余下还有@**OnClose**、**@OnMessage**、**@OnError**等注解用来标注WebSocket相关操作的方法。具体翻自己项目代码。





[^1]: 客户端和服务端彼此可以双向通信，主动给对方发消息。
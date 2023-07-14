# Nginx笔记

​	Nginx (engine x) 是一个高性能的HTTP和**反向代理**web服务器，同时也提供了IMAP/POP3/SMTP服务。

### 0.1	正向代理与反向代理

​	正向代理是==客户端的代理==。

![img](https://kuangstudy.oss-cn-beijing.aliyuncs.com/bbs/2021/01/25/kuangstudy46bdad36-d3e0-43b0-a223-43360b7e8fc7.png)

​	反向代理是==服务端的代理==。

![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/kuangstudy62a15097-6e2a-4dbe-bcf5-f0d7cab81089.png)

### 0.2	负载均衡和轮询

​	服务器的负载均衡是一种计算机网络技术，用来在多个服务器、网络连接、CPU、磁盘驱动器或其他资源中分配负载，以达到最佳化资源使用、最大化吞吐率、最小化响应时间、同时避免过载的目的。

​	Nginx提供的负载均衡策略有2种：内置策略和扩展策略。内置策略为**轮询**，**加权轮询**，**Ip hash**。扩展策略则有很多。

​	==轮询==
![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/kuangstudy4d33dfac-1949-4b2d-abb8-fe0b6e65b8dc.png)
​	==加权轮询==
![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/kuangstudyb1e3e440-4159-4259-a174-528b56cb04b2.png)

​	==iphash==对客户端请求的ip进行hash操作，然后根据hash结果将同一个客户端ip的请求分发给同一台服务器进行处理，可以解决session不共享的问题。(不常用，实际上一般用Redis共享session)

### 0.3	动静分离

​	动静分离，在我们的软件开发中，有些请求是需要后台处理的，有些请求是不需要经过后台处理的（如：==css、html、jpg、js等等文件==），这些不需要经过后台处理的文件称为静态文件。让动态网站里的动态网页根据一定规则把不变的资源和经常变的资源区分开来，动静资源做好了拆分以后，我们就可以根据静态资源的特点==将其做缓存操作。提高资源响应的速度==。

![img](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/kuangstudyedb1bbd6-e530-4aba-8fde-68658a10e73f.png)

​	此功能可以提高了网站的响应速度，优化了用户体验。

## 1.0	Nginx，启动!

​	输入`whereis nginx`可以查看Nginx相关的目录。

​	若想启动Nginx，需要到`/usr/sbin/nginx`目录下启动`nginx`文件(记得==sudo==，默认拦截监听80端口)。

![welcometoNginx](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/welcometoNginx.png)

​	出现此页面说明启动成功。

## 2.0	Nginx常用命令

| 命令                      | 功能                   |
| ------------------------- | ---------------------- |
| whereis nginx             | 查看Nginx相关的目录。  |
| cd /etc/nginx/conf.d      | 去到配置文件的所在目录 |
| cd /usr/sbin              | 去到启动文件的所在目录 |
| /usr/sbin/nginx           | 启动                   |
| /usr/sbin/nginx -s stop   | 停止                   |
| /usr/sbin/nginx -s quit   | 安全退出               |
| /usr/sbin/nginx -s reload | 重新加载配置文件       |
| ps aux \|grep niginx      | 查看nginx进程          |
| /usr/sbin/nginx -t        | 检查配置文件是否合规   |



## 3.0	跨域请求问题

​	即使按照博客配置配置文件，浏览器拒绝跨域请求的问题依然没有解决。但是经过验证，用apifox发请求后一切正常。	
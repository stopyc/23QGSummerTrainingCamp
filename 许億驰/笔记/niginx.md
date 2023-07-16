tar -xvf pcre-8.37.tar.gz
yum -y install make zlib zlib-devel gcc-c++ libtool openssl openssl-devel









问题：  linux上安装 pcre的时，输入make && make install 报错make: *** 没有指明目标并且找不到 makefile。 停止。
解决办法:  yum install -y gcc-c++ 
结果： 不成功
办法2： yum install openssl* -y
结果： 不成功
办法3： 重新  ./configure
结果： 成功


问题表现： nginx在linux下安装后发现找不到sbin文件夹
解决办法：  用root用户安装的软件会被存储在  /usr/local/nginx/sbin   之中



firewall-cmd --add-service=http -permanent
sudo firewall cmd --add-port=80/tcp --permanent
firewall-cmd-reload


4、关闭 nginx
./nginx –s stop

5、重新加载 nginx
./nginx →s reload




2、nginx 配置文件组成
（1） nginx 配置文件有三部分组成
第一部分 全局块
从配置文件开始到 events 块之间的内容,主要会设置一些影响 nginx 服务器整体运行的配置指令
比如 worker processes
1; worker_processes 值越大，可以支持的并发处理量也越多
第二部分 events 块
events块涉及的指令主要影响Nginx服务器与用户的网络连接
比如worker_connections 1024;支持的最大连接数.
第三部分 http 块.
Nginx服务器配置中最频繁的部分



3.反向代理

记得对外开放端口

firewall-cmd--add-port=8080/tcp --permanent
firewall-cmd-reload

1、实现效果
(1)打开浏览器,在浏览器地址栏输入地址 www.123.com,跳转到liunx系统tomcat主页
面中


第一步

windows的host文件进行配置
配置域名映射的ip地址


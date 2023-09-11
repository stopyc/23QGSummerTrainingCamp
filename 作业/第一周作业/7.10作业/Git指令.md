[TOC]

# Git指令

## 一、命令控制Git

### 1.Git基本知识

![屏幕截图 2023-03-15 212516](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/%25E5%25B1%258F%25E5%25B9%2595%25E6%2588%25AA%25E5%259B%25BE%25202023-03-15%2520212516.png)

![屏幕截图 2023-03-15 212556](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/%25E5%25B1%258F%25E5%25B9%2595%25E6%2588%25AA%25E5%259B%25BE%25202023-03-15%2520212556.png)

![屏幕截图 2023-07-10 082946](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/%E5%B1%8F%E5%B9%95%E6%88%AA%E5%9B%BE%202023-07-10%20082946.png)

----------------------------------------------------------------------------



### 2.git基本操作

----

#### 2.1添加

1.add  2.commit 3.push

----

```java
# git add .                添加所有文件到暂存区
# git commit -m + "文件名"  提交暂存区的内容到本地仓库
# 
```

前端的 npm_moudles不要打包

在idea主目录下建立".gitignore"文件:

```Java
1.*.txt       #忽略所有.txt结尾的文件        //文件中以空行或者井号(#)开头的行会被忽略
2.!lib.txt    #lib.txt文件除外             //以!开头的文件表示出了这个文件不被忽略
3./temp       #忽略根目录以下,temp以上的文件  //(/)路径分隔符
4.build/      #忽略build/目录下的所有文件    //
5.doc/*.txt   #忽略
```



![屏幕截图 2023-03-15 202335](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/%E5%B1%8F%E5%B9%95%E6%88%AA%E5%9B%BE%202023-03-15%20202335.png)

touch     ：创建新的文件（==要加文件后缀名==）

status    ：查看当前文件状态

add        ：添加文件（==可以直接输入文件名，也可以加  .添加所有暂存区==）

commit  -m"XXX"   ：==“XXX里添加注释”==

log          ：查看日志



#### 2.2版本穿越

git reset --hard XXXX（==XXXX为要穿越回去的版本号==）

==穿越回去后，本地数据会变成该版本==



### 3.分支操作

#### 3.1 分支代码

branch 分支名     ：创建分支

branch -v             ：查看分支

checkout 分支名 ：切换分支

merge 分支名      ：将指定分支合并到当前分支上



#### 3.2 合并分支

##### 3.2.1非冲突合并

例：将hot-fix分支合并到master分支上,则打开master分支然后输入merge hot-fix

然后add 和commit

##### 3.3.2冲突合并(两套代码都针对源代码有进行修改)

![image-20230710144822859](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710144822859.png)

<<<<<<<后的代表当前分支，= = = = = =后表示其他冲突的分支，>>>>>>表示该冲突分支的分支名



针对冲突直接查看进行人为修改之后，在进行add和commit且提交的时候不能带文件名，否则会出现以下

![image-20230710145423806](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710145423806.png)

全部示范

![image-20230710145542239](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710145542239.png)

==注意！！！修改后只会更改当前分支，不会更改冲突分支。如上例，main会修改，但是hot-fix不会改变==



### 4.代码团队协作

#### 4.1给远程库创建别名(remote add)

![image-20230710151346366](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710151346366.png)

#### 4.2推送本地库到远程库(push)

![image-20230710152529989](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710152529989.png)

==git push test(仓库链接的别名)   main(要push的分支)==

#### 4.3从远程库拉取新代码(pull)

![image-20230710152841229](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710152841229.png)

==git pull test(仓库链接的别名)   main(要的pull的分支)==

#### 4.4克隆代码(clone)

![image-20230710154648069](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710154648069.png)

==git clone (克隆的仓库链接)==

克隆的作用 : 1.拉取代码  2.初始化本地仓库  3.创建别名



### 5.SSH公钥的创建

C盘->用户文件夹->右键打开Git

输入==ssh-keygen -t rsa -C (邮箱)==

==点击三次回车->cd.ssh->cat id_rsa.pub(公钥的文件名)->复制公钥->GitHub账号settings选择SSH->添加公钥==



## 二、IDEA集成Git

### 1.IDEA切换版本号

![image-20230710185238471](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710185238471.png)

==点击Git->点击log可以查到Log的版本,右键可以切换版本号->Checkout到新版本==



### 2.IDEA创建新分支

![image-20230710190208093](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710190208093.png)

==右键->Git->Repository->Branches==       或者      ==点击右下角master==



### 3.IDEA合并

#### 3.1正常合并

![image-20230710223119770](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710223119770.png)

==第一个绿色箭头表示hot-fix所在的版本号，黄色箭头表示当前查看的版本号，第二个绿色箭头表示master当前所在的版本号==

![image-20230710223352099](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710223352099.png)=

==表示把hot-fix合并到当前的分支，即为右下角显示的分支==



#### 3.2冲突合并

![image-20230710224251279](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230710224251279.png)

==如果两个分支合并时相同地方都有代码修改的话，则会引发冲突，如图所示，点击merge==

![image-20230711081451148](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230711081451148.png)

左侧是master代码，右侧是hot-fix代码，中间是二者没有冲突的代码==>>或<<表示要这行代码，×表示不要这行代码==



### 4.推送（push）

**push不上去可以使用ssh公钥进行推送**



### 4.1Tip：每次改本地代码前都要pull一下远程代码保证本地的版本高于远程库版本



### 5.克隆项目(clone)

![image-20230711092933480](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230711092933480.png)

==New->Project from Version Control==

![image-20230711093035821](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230711093035821.png)

![image-20230711093113525](https://31222004479hekaijie-1316536337.cos.ap-guangzhou.myqcloud.com/image-20230711093113525.png)

==选择一：直接输入SSH公钥链接进行clone，或者点击GitHub选择自己的仓库进行clone==

# Git下载镜像网址:

http://npm.taobao.org/mirrors/git-for-windows/


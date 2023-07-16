---
title: git的使用
tags: 软件
categories: 编程
---
<meta name="referrer" content="no-referrer"/>

### 前言

第一次使用git，有些磕磕绊绊，好在在我不懈地查找解决办法下，终于解决了，来记录下。
23年7月10号我再一次学习了git，作的笔记补充到了后面，可以点击链接跳转
[7月10号笔记](#7月10号补充笔记)
<!-- more -->
我的主要目的是上传本地仓库的内容到远程仓库，下面的步骤也是围绕此展开。



### 正文分享

1.进入想要创建仓库的文件夹，然后右键点击git bash，进入命令行窗口。（如果第一次用
是要输入个人信息的
git config --global user.name "xxx"
git config --global user.email "[xxxx@xxx.com](mailto:xxxx@xxx.com)"
）
git init   初始化成为仓库
git add .    添加此文件夹所有文件到工作区。
git status     查看工作区的内容
git commit -m "项目名称"
（在做push之前要   添加SSH key  不然每次push pull都要输密码
ssh-keygen -t rsa -C "[xxxxx@xxx.com](mailto:xxxxx@xxx.com)"           这是在本地创建ssh key，写自己注册github的邮箱
然后一路回车
找到C盘中的user文件夹，找到生成的.ssh文件夹
用记事本打开里面的ssh public版复制到github的ssh设置窗口）

键盘输入ctrl+L                输入到这，窗口太多东西了，来个清屏，全部东西会上移隐藏

git remote add origin + 你github仓库的网址 + .git               关联本地仓库
git push origin master         master是无分支时创建的分支，有分支时填分支名，作用是执行推送
git remote add origin +你仓库的ssh（在创建仓库的开始界面有）不过创建分支后我找不到了，使用ssh的关联办法的话   //ssh格式  [git@github.com](mailto:git@github.com):hec990/testing.git
git push -u origin master        -u应该是模式的意思
git push origin + branchname          将新分支推送到github
![image.png](https://cdn.nlark.com/yuque/0/2023/png/34892071/1673792153339-5ccc6406-c50d-4e8c-9d1f-c2a6f86062ff.png#averageHue=%23100b04&clientId=u1eed3ae3-0d1a-4&from=paste&height=140&id=u4cb77b65&name=image.png&originHeight=231&originWidth=855&originalType=binary&ratio=1&rotation=0&showTitle=false&size=28084&status=done&style=none&taskId=u06103297-3d23-4803-94f6-c5c46523750&title=&width=518.1817882317174#alt=image.png)
我在这出了问题，错误：failed to push some refs to'github.com:maiugug/Openwork.git'
提示：Updates were rejected because the tip of your current branchis behind
its remote counterpart. Integrate the remote changes
后来怎么搞都不行，查了两个钟，终于找到了。
![image.png](https://cdn.nlark.com/yuque/0/2023/png/34892071/1673792197047-b27ec86b-f5df-4009-948c-85063a4256b0.png#averageHue=%23211e1b&clientId=u1eed3ae3-0d1a-4&from=paste&height=144&id=u86720fb3&name=image.png&originHeight=238&originWidth=697&originalType=binary&ratio=1&rotation=0&showTitle=false&size=31531&status=done&style=none&taskId=u5691f56e-5234-4584-93f8-040f322f10a&title=&width=422.42421800878014#alt=image.png)
暂时不知道哪里出错，到时回来找一下。
git push -u origin main         push的报错是不一样的，它的是
error:src refspec main does not match any
error:failed to push some refs to'github.com: maiugug/openWork.git
![image.png](https://cdn.nlark.com/yuque/0/2023/png/34892071/1673792337552-01145b04-4c1c-4ae2-9fdd-8b238709b7cb.png#averageHue=%230b0703&clientId=u1eed3ae3-0d1a-4&from=paste&height=95&id=u59df749c&name=image.png&originHeight=156&originWidth=780&originalType=binary&ratio=1&rotation=0&showTitle=false&size=20657&status=done&style=none&taskId=u2d531469-06ce-4f11-ad9a-143b906c667&title=&width=472.7272454043738#alt=image.png)
git branch -d + branchname             删除本地分支
*
上传完了，我松了口气，就又扩展了一些git操作
git checkout +branchname           切换到新的分支
git checkout -b +branchname               创建分支的同时切换到该分支
相当于
git branch + branchname
git checkout + branchname

注意远程仓库如果本来有东西的话，就要先进行拉取合并了，再push
法一：将远程仓库的master分支拉取到本地，然后合并到本地的master分支上
git pul1 --rebase origin master
此时冉将本地仓库push到远端仓库中即可
git push -u origin master

法二：将远端仓库的master分支拉取、合并到本地的master分支
$git pull origin masten --allow-unrelated-histories
然后再将本地仓库git push到远端
$git push -u origin master

git pull origin master          本地同步远程仓库，将远程仓库里的内容拉下来
·git rm -r --cached             文件名删除文件【还没用过】？文件名写哪
·git commit -m“delete dir”          提交并添加说明【还没用过】  ？给谁添加说明呀
git push origin master            将本次更改更新到github项目上去
git push origin：[branch name]         删除github远程分支
git branch -r                 -r表示remote  查看远程分支


###7月10号补充笔记
######这次的补充内容是关于合并分支解决版本冲突的，然后还带常见的命令行操作。首先，可以通过键盘输入: cd <tab>  选择你所需要进入的文件夹不知道当前目录到底有什么文件夹可以输入: dir   进行查看然后，为了测试git的功能，我还需要创建一个二进制文件。mkdir 文件名    如果文件名没有后缀的话就是文件夹创建错了可以通过，md 文件名   也可以进行创建   rm 文件名    来进行删除创建完了之后，想要改变二进制文件，可以通过   vim 文件名   来进行修改

#####接下来是vim的使用教学
在前面，我们通过   vim 文件名   进入到vim的操作界面，一开始进入的模式就是vim的普通模式，在普通模式下，可以进行退出vim的操作，还有切换模式的操作。命令如下：
:wq    退出并保存
:q     只退出
i      插入模式
o      插入模式
总体来说不难，只要实践一下就熟练了。记得在输入指令时一定要用英文输入法，不然就会被判断为无效输入。


#####继续说git的冲突版本回退合并分支的命令

git 是一个只记住二进制文件改变的版本控制软件，所以如果我们只是创建文件夹，而没有在文件夹中创建任意文件，我们的改变就并不会被记录到git之中。简而言之，就是你git add然后git status或git commit 没有任何反应，只显示

nothing to commit, working tree clean

接下来，我将会用命令加注释的方式来进行说明
git branch 分支名   创建分支
git branch -v      查看分支
git switch 分支名   切换分支
git merge 分支名    把指定分支合并到当前分支上
git log            获得commit详细记录
git reflog         获得commit简略记录
git diff           对比当前文件与暂存区文件的区别
git diff --cached  对比暂存区文件与最新版本文件的区别


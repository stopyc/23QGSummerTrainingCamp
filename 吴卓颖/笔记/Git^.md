# Git

​	git是一个开源的**分布式版本控制系统**。存在远程仓库，本地仓库。除了默认隐藏的.git外的所有目录都是**工作目录**。

## 0.0	可能会用到的Linux指令

​	ls/ll 查看当前目录

​	cat 查看文件内容

​	touch 创建文件

​	vi vi编辑器（使用vi编辑器是为了方便展示效果。使用记事本、editPlus、notPad++等其它编辑器亦可）

## 1.0	基础操作指令

​	Git工作目录下对于文件的**修改**(增加、删除、更新)会存在几个状态，这些**修改**的状态会随着我们执行Git的命令而发生变化。

​	初始化git仓库:git init

![repositoryindexworkspace](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/repositoryindexworkspace.png)

​	简记:workspace -> index: git add

​	index -> repository: git commit

### 1.10	查看修改的状态(status)

​	作用：查看的修改的状态（暂存区、工作区）

​	命令形式：git status

### 1.20	添加工作区到暂存区(add)

​	作用：添加工作区一个或多个文件的修改到暂存区

​	命令形式：git add 单个文件名|通配符

​	将所有修改加入暂存区：git add .

### 1.30	提交暂存区到本地仓库(commit)

​		作用：提交暂存区内容到本地仓库的当前分支

​		命令形式：git commit -m '注释内容'

### 1.40	查看提交日志(log)

​	此前配置的别名git**-**log就包含了这些参数，所以后续可以直接使用指令git**-**log。

​	作用:查看提交记录

​	命令形式：git log [option]

​		options:

​			--all 显示所有分支

​			--pretty=oneline 将提交信息显示为一行

​			--abbrev-commit 使得输出的commitId更简短

​			--graph 以图的形式显示

### 1.50	版本回退

​	作用：版本切换

​	命令形式：git reset --hard commitID

​	commitID 可以使用 git-log 或 git log 指令查看



​	作用：git会记录你的每一次命令，此命令可以**查看已经删除的记录**

​	命令形式：git reflog

​	这个指令可以看到已经删除的提交记录。

### 1.60	.gitignore——添加文件至忽略列表

​	对于无需纳入Git 的管理，也不希望出现在未跟踪文件列表的文件，我们可以在工作目录中创建一个名为.gitignore的文件(名称固定),在其中列出要忽略的文件模式。

```git
# no .a files
*.a

# but do track lib.a, even though you're ignoring .a files above
!lib.a

# only ignore the TODO file in the current directory, not subdir/TODO
/TODO

# ignore all files in the build/ directory
build/

# ignore doc/notes.txt, but not doc/server/arch.txt
doc/*.txt

# ignore all .pdf files in the doc/ directory
doc/**/*.pdf

```

​	在 文件名.gitignore 文件中，每一行的忽略规则的语法如下：

> 空格不匹配任意文件，可作为分隔符，可用反斜杠转义
> 开头的文件标识注释，可以使用反斜杠进行转义
> ! 开头的模式标识否定，该文件将会再次被包含，如果排除了该文件的父级目录，则使用 ! 也不会再次被包含。可以使用反斜杠进行转义
> / 结束的模式只匹配文件夹以及在该文件夹路径下的内容，但是不匹配该文件
> / 开始的模式匹配项目跟目录
> 如果一个模式不包含斜杠，则它匹配相对于当前 .gitignore 文件路径的内容，如果该模式不在 .gitignore 文件中，则相对于项目根目录
> ** 匹配多级目录，可在开始，中间，结束
> ? 通用匹配单个字符
> \* 通用匹配零个或多个字符
> [] 通用匹配单个字符列表

### 1.70	撤销更改

​	作用:撤销未提交到暂存区的更改。

​	命令形式:git checkout --文件名.txt

​	这里有两种情况：

1. 一种是`文件名.txt`自修改后还没有被放到暂存区，现在，撤销修改就回到和版本库一模一样的状态；
2. 一种是`文件名.txt`已经添加到暂存区后，又作了修改，现在，撤销修改就回到添加到暂存区后的状态。

​	总之，就是让这个文件回到最近一次`git commit`或`git add`时的状态,即用版本库里的版本替换工作区的版本。注意命令中的`--`不能省略

### 1.80	删除文件与将文件从Git的跟踪中移除

​	作用:没什么用，但是记在这里方面日后记Linux命令

​	命令形式:git rm 文件名.txt



​	作用:将某文件从Git的跟踪中移除

​	命令形式:git rm -r --cached &lt;filename>

​	配合.gitignore文件可以让本地库中的某个文件一直不被Git跟踪。*注:在GIt中，若文件名带空格，则输入时需要用'\ '(反斜杠+空格)转义文件名才能被正确识别。*

## 2.0	分支

​	几乎所有的版本控制系统都以某种形式支持分支。 使用分支意味着你可以把你的工作从开发主线上分离开来，以便进行重大的Bug修改、开发新的功能，避免影响开发主线。

### 2.10	查看(创建)本地分支

​	命令形式:git branch (分支名)

​	其查看功能一定程度上可以被前文设置的命令别名git-log代替。

### 2.20	切换分支

#### 2.21	checkout

​	命令形式:git checkout 分支名

​	功能:切换到一个存在的分支。

​	命令形式: git checkout -b 分支名

​	功能: 创建一个分支并切换。

#### 2.22	switch(新版本Git)

​	命令形式:git switch (-c) 分支名

​	功能:切换到一个已存在(新的)分支。

### 2.30	合并分支

​	命令形式:git merge 分支名称

​	功能:将一个分支的提交合并到另一个目标分支。必须先切换到目标分支。

#### 2.31	合并的快进模式（Fast-forward)

​	在某些情况下使用git merge命令后会看到Fast-forward信息,这说明这次合并是快进模式,即<u>直接把 **目标分支** 指向 **merge分支** 的当前提交</u>，因此此次合并速度会非常快。

### 2.40	删除分支

​	命令形式:git branch -d b1

​	功能:做各种检查并通过后，删除分支。

​	命令形式:git branch -D b1

​	功能:不做任何检查，强制删除。

​	注意，不能删除当前分支，只能删除其他分支。

### 2.50	解决冲突

​	当两个分支上对文件的修改可能会存在冲突，例如同时修改了同一个文件的同一行，这时就需要手动解决冲突，解决冲突步骤如下：

​		1、处理文件中冲突的地方(可以**直接**在文件操作)；

​		2、将解决完冲突的文件加入暂存区(add)；

​		3、提交到仓库(commit)；

#### 2.52	在IDEA中解决冲突

​	在使用IDEA内置的Git时，若发生Conflict，则会有提示

![conflict appear](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/conflict%20appear.png)

​	若点击`Merge`按钮则会进入手动解决冲突的界面，但IDEA对冲突产生的地方进行了直观地展示并设计了快捷解决的按钮

![solve conflict](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/solve%20conflict.png)

​	当然有时候还是得自己手动更改。

![手动修改](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/%E6%89%8B%E5%8A%A8%E4%BF%AE%E6%94%B9.png)

### 2.60	工作区的暂存

​	命令形式:git stash

​	功能:在当前工作区无法添加到暂存区并commit且**急需切换到其它分支**工作时，可以使用此命令将工作区"储藏"起来。此时用`git status`查看工作区，就是干净的（除非有没有被Git管理的文件），因此可以放心地创建分支进行修复bug等工作



​	命令形式:git stash list

​	功能:查看暂存的工作区信息



​	命令形式:git stash apply/drop/pop (stash@{})

​	功能:恢复/删除/弹出暂存的(指定)工作区内容

### 2.70	修改(commit)的复制

​	命令形式:git cherry-pick (--edit) &lt;commit-hash>

​	功能: 将指定的提交复制到当前分支中(可重新设置提交信息)。

## 3.0	Git远程仓库

​	Git中存在本地仓库和远程仓库两种类型的仓库，我们可以通过一些网上的代码托管服务平台来搭建Git远程仓库，其中常用的有:Github、码云、GitLab、CODING等。

### 3.10	配置SSH公钥

​	本地生成公钥并在相应平台上设置后，便可以通过SSH操作远程仓库。此为操作远程仓库的方法之一。

### 3.20	操作远程仓库

#### 3.21	添加远程仓库(由下至上)

​	命令形式:git remote add [远端名称] [仓库路径]

​	功能: 初始化本地库，然后与已创建的远程库进行对接，即将本地已有的内容上传的(空白)远程库中并进行对接。

​	远端名称：默认是origin，取决于远端服务器设置。一般填origin即可。

​	仓库路径：从远端服务器获取此URL填入，有HTTPS、SSH等多种形式。

#### 3.22	查看远程仓库

​	命令形式:git remote

​	功能：显示当前本地库连接的远程仓库。

#### 3.23	推送到远程仓库(常用)

​	命令形式:git push [-f] [--set-upstream] [远端名称] [本地分支名(:远端分支名) ]

​	功能:将本地分支推送到远程仓库(的对应分支)。

​	参数功能:

​		①如果远程分支名和本地分支名称相同，则可以只写本地分支：

​			git push origin master

​		②-f 表示强制覆盖，无视警告

​		③--set-upstream 表示推送到远端的同时并且建立起和远端分支的关联关系:

​			git push --set-upstream origin master

​		如果当前分支已经和远端分支关联，则可以省略分支名和远端名直接使用git push——将master分支推送到已关联的远端分支。 

#### 3.24	查看本地分支与远程分支的关联关系

​	命令形式:git branch -vv

​	功能:如题

#### 3.25	从远程仓库克隆到本地(由上至下)

​	命令形式:git clone [仓库路径] (本地目录)

​	功能: 将已经一个已经存在的远程仓库克隆到本地目录下(连带着远程仓库中的文件一起)。若本地目录留空，则会克隆到当前目录。

#### 3.26	从远程仓库中抓取

​	命令形式:git fetch [远端名称] [远端分支名称]

​	功能:将远程仓库里的更新都抓取到本地，但不会进行合并。如果上述两个参数都留空，则会抓取远程仓库的所有分支。

#### 3.27	从远程仓库中拉取

​	命令形式:git pull [远端名称] [远端分支名称]

​	功能:将远端仓库的修改拉到本地并自动进行合并，等同于fetch+merge。如果上述两个参数都留空，则会抓取所有并更新当前分支。

#### 3.28	关于合并冲突

​	远程分支也是分支，所以合并时冲突的解决方式也和解决本地分支冲突的方式基本相同:

​		0、先拉取远程仓库的提交；

​		1、处理文件中冲突的地方(可以**直接**在文件操作)；

​		2、将解决完冲突的文件加入暂存区(add)；

​		3、提交到仓库(commit)；

#### 3.29	取消本地仓库与远程仓库的绑定

​	命令形式: git remote rm 远程仓库名

## 4.0	Git与实际开发

### 4.10	分支策略

​	在开发中,分支有若干使用原则和相对固定的流程。master生产分支、develop开发分支、feature/xxxx分支、hotfix/xxxx分支、test代码测试分支、pre预上线分支等。

​	同时每个人都有自己的分支。

![git-br-policy](https://www.liaoxuefeng.com/files/attachments/919023260793600/0)

## 5.0	几个补充要点

1. 切换分支前要先提交本地的修改。
2.  代码要及时提交(保存)，提交过了就不会丢。
3. Q: Git因为unrelated history无法merge怎么办?

​		A:[Git因为unrelated history无法merge的原因是两个仓库不同而导致的，需要在后面加上**–allow-unrelated-histories**进行允许合并，即可解决问题。

例如，你可以使用以下命令来合并远程仓库的master分支到本地仓库的master分支：

```
git pull origin master --allow-unrelated-histories
```

​	这个选项可以合并两个独立启动仓库的历史。如果还不能解决问题，就把本地的remote删除，重新git remote add添加远程仓库，再按上面的方法来。

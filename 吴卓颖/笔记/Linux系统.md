# Linux操作系统笔记

​	操作系统是软件的一类，主要作用是协助用户调度硬件工作，充当用户和计算机硬件之间的桥梁。

## 0.0	Linux本身

​	Linux系统由Linux系统内核和系统级应用程序两部分组成。

- 内核提供系统最核心的功能，如:调度CPU、调度内存、调度文件系统、调度网络通讯、调度IO等。
- 系统级应用程序，可以理解为出厂自带程序，可供用户快速上手操作系统，如文件管理器、任务管理器、图片查看等

### 0.10	VMWare虚拟机与FinalShell终端

​	虚拟机用Linux方便；Linux的图形化界面不稳定且不好用；因为直接操作虚拟机会跨越系统，导致复制等诸多操作不便，所以需要FinalShell终端来在本地Windows系统"远程"控制我们的Linux"服务器"。

​	*注意重启可能会改变其IP导致FinalShell断连，此时需要在虚拟机命令行输入`ifconfig`找到新IP*

### 0.20	Linux的目录结构

![image-20221027214128453](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/20221027214128.png)

- 在Windows系统中可以有多个盘符，每个盘符都可以看成一棵树，且路径之间的层级关系用`\`来表示。
- 在Linux只有一个顶级目录：`/`。`/`就是根目录是最顶级的目录了。路径描述的层次关系则也用`/`来表示，例如:
    - /home/itheima/a.txt，表示根目录下的home文件夹内有itheima文件夹，内有a.txt

## 1.0	Linux命令行和命令

​	命令行: 即**Linux终端**(Terminal)，是一种命令提示符页面。以纯“字符”的形式操作系统。

​	命令: 即Linux程序。一个命令就是一个Linux的程序。命令没有图形化页面,可以在命令行(终端中)提供字符化的反馈。

## 1.10	Linux命令基础格式

​	无论是什么命令，用于什么用途，在Linux中，命令有其通用的格式：

| command | [-options] | [parameter] |
| ------- | ---------- | ----------- |

- command： 命令本身
- -options：[可选，非必填]命令的一些选项，可以通过选项控制命令的行为细节
- parameter：[可选，非必填]命令的参数，多数用于命令的指向目标等

​	*注: 语法中的[]，表示可选的意思*

## 1.20	基本命令

### 1.21	ls命令

功能：列出文件夹信息

语法：`ls [-l -h -a] [参数]`

- 参数：被查看的文件夹，不提供参数，表示查看当前工作目录
- -l，以列表形式查看
- -h，配合-l，以更加人性化的方式显示文件大小(即带单位)
- -a，显示隐藏文件

​	*注:此处输入`ls -lh`也是一样的效果*

![ls-hl-h](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/ls-hl-h.png)

​	在Linux中以`.`开头的文件，均是隐藏(**自动隐藏**)。需要`-a`选项才可查看到。

### 1.22	cd命令

​	功能：切换工作目录

​	语法：`cd [目标目录]`

​	参数：目标目录，要切换去的地方，不提供默认切换到`当前登录用户HOME目录`

​	附注: cd命令来自英文: **C**hange **D**irectory

### 1.23	pwd命令

​	功能：展示当前工作目录

​	语法：`pwd`

​	参数: 无参数

​	附注:	pwd命令来自英文:**P**rint **W**ork **D**irectorty



### 1.23	相对路径、绝对路径

- 相对路径，**非**`/`开头的称之为相对路径

    相对路径表示以`当前目录`作为起点，去描述路径，如`test/a.txt`，表示当前工作目录内的test文件夹内的a.txt文件

- 绝对路径，==以==`/`开头的称之为绝对路径

    绝对路径从`根`开始描述路径

- **特殊路径符**

    - `.`，表示当前，比如./a.txt，表示当前文件夹内的`a.txt`文件
    - `..`，表示上级目录，比如`../`表示上级目录，`../../`表示上级的上级目录
    - `~`，表示用户的HOME目录，比如`cd ~`，即可切回用户HOME目录



### 1.24	mkdir命令

​	功能：创建文件夹

​	语法：`mkdir [-p] [-m] 参数`

- 参数：被创建文件夹的路径

- 选项：-p，可选，表示创建前置路径

    - ```linux
        [root@linuxcool ~]# mkdir -p /Dir1/Dir2/Dir3/Dir4/Dir5
        //不加-p会在/Dir1/Dir2/Dir3/Dir4文件夹下创建文件夹Dir5
        ```

- -m:  创建目录的同时设置权限，具体权限码查文档。

- 附注: mkdir命令来自英文词组”**M**a**k**e **dir**ectories“的缩写

![mkdir](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/mkdir.png)



### 1.25	touch命令

​	功能：创建文件

​	语法：`touch 参数 (带文件路径的)文件名`

- 参数：具体看文档



### 1.26	cat命令

​	功能：查看文件内容

​	语法：`cat 参数`

- 参数：被查看的文件路径
- 附注: cat命令来自英文词组”**C**onc**at**enate files and  print“的缩写，其功能是用于在终端设备上显示文件内容。在Linux系统中有很多用于查看文件内容的命令，例如more、tail、head……等等，每个命令都有各自的特点。cat命令适合查看内容较少、纯文本的文件。 对于内容较多的文件，使用cat命令查看后会在屏幕上快速滚屏，用户往往看不清所显示的具体内容，只好按Ctrl+c键中断命令执行。

### 1.27	more命令

​	功能：查看文件，可以支持翻页查看

​	语法：`more 参数`

- 参数：被查看的文件路径
- 在查看过程中：
    - `空格`键翻页
    - `q`退出查看

![more_ect_service](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/more_ect_service.png)



### 1.28	cp命令

​	功能：复制文件、文件夹

​	语法：`cp [-r] 参数1 参数2`

- 参数1，被复制的
- 参数2，要复制去的地方
- 选项：-r，可选，复制文件夹使用

​	附注: 来源英文**C**o**p**y

​	示例：

- cp a.txt b.txt，复制当前目录下a.txt为b.txt
- cp a.txt test/，复制当前目录a.txt到test文件夹内
- cp -r test test2，复制文件夹test到当前文件夹内为test2存在



### 1.29	mv命令

​	功能：移动文件、文件夹

​	语法：`mv 参数1 参数2`

- 参数1：被移动的
- 参数2：要移动去的地方，参数2如果**不存在**，则会**进行改名**

​	附注: 来源英文**M**o**v**e



### 1.210	rm命令

​	功能：删除文件、文件夹

​	语法：`rm [-r -f] 参数...参数`

- 参数：支持多个，每一个表示被删除的，空格进行分隔。同时参数支持通配符[^1]
- 选项：-r，删除文件夹使用
- 选项：-f，强制删除，不会给出确认提示，一般root用户会用到

​	附注: 来源英文**R**e**m**ove

> ​	rm命令很危险，一定要注意，特别是切换到root用户的时候。



### 1.211	which命令

​	功能：查看命令的程序本体文件路径。每一条命令都是有本体文件存储在硬盘中的。

​	语法：`which 参数`

- 参数：被查看的命令



### 1.212	find命令

​	功能：搜索文件

​	语法1按文件名搜索：`find 起始路径 -name 参数`

- 起始路径: 搜索的起始路径
- 参数: 搜索的关键字，支持通配符*， 比如：`*`test表示搜索任意以test结尾的文件。文件名要加""(双引号)

![findname](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/findname.png)

​	语法2按文件大小搜索: `find 起始路径 -size +/-n[kMG] `

- 起始路径: 同上
- n: 表示大小数字，整数或浮点数
- kMG: 表示大小单位，k(小写字母)表示kb，M表示MB，G表示GB(可能还有T)。最后三选一。

![findsize](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/findsize.png)

- 附注: 为了可以在整个系统完成搜索我们可以切换到root用户以获得管理员权限。执行命令:
    - su -root 
        输入密码:123456（和你普通用户的密码一样)
    - su do find ...亦可(待研究)



## 1.3	grep、wc和管道符

### 1.31	grep命令

​	功能：过滤关键字

​	语法：`grep [-n] 关键字 文件路径`

- 选项-n，可选，表示在结果中显示匹配的行的行号。
- 参数，关键字，必填，表示过滤的关键字，如果带有空格或其它特殊符号，建议使用””将关键字包围起来
- 参数，文件路径，必填，表示要过滤内容的文件路径，**可作为内容输入端口**



![greptest](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/greptest.png)



### 1.32	wc命令

​	功能：统计

​	语法：`wc [-c -m -l -w] 文件路径`

- 选项，-c，统计bytes数量
- 选项，-m，统计字符数量
- 选项，-l，统计行数
- 选项，-w，统计单词数量(空格划分)
- 参数，文件路径，被统计的文件，**可作为内容输入端口**



> 参数文件路径，可作为**管道符**的输入



### 1.33	管道符

​	写法：`|`

​	功能：将符号**左边的结果**，作为**符号右边的输入**

​	示例：

​	`cat a.txt | grep itheima`，将cat a.txt的结果，作为grep命令的输入，用来过滤`itheima`关键字

​	可以支持嵌套：

​	`cat a.txt | grep itheima | grep itcast`

![grepwithtube](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/grepwithtube.png)



## 1.4	echo、tail和重定向符

### 1.41	echo命令

​	功能：输出内容，类似编程语言中的print

​	语法：`echo 参数`

- 参数：被输出的内容，最好在**内容上加上""双引号**减少误解。



### 1.42	`反引号

​	功能：被两个反引号包围的内容，会作为命令直接执行

​	示例：

- echo \`pwd\`，会输出当前工作目录

![echols](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/echols.png)



### 1.43	>与>>重定向符

​	功能：将符号左边的结果，输出到右边指定的文件中去

- `>`，将左侧命令的结果，**覆盖**写入到符号右侧指定的文件中

- `>>`,将左侧命令的结果，**追加**写入到符号右侧指定的文件中

    



### 1.44	tail命令

​	功能：查看文件尾部内容

​	语法：`tail [-f -N] 参数`

- 参数：被查看的文件
- 选项：-f，持续跟踪文件修改
- -N,N为自然数表示查看尾部多少行，留空默认为10

![tail](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/tail.png)

### 1.45	head命令

​	功能：查看文件头部内容

​	语法：`head [-n] 参数`

- 参数：被查看的文件
- 选项：-n，查看的行数

## 1.5	关于命令帮助

​	如果想要对命令的其它选项进行查阅，可以通过`--help` 选项查看命令的帮助。`--help`对绝大部分命令都生效。

​	如：ls --help， 会列出ls命令的帮助文档

![ls--help](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/ls--help.png)

​	帮助文档会简单的对命令的使用方式进行说明



## 2.0	vi/vim编辑器

​	`vi\vim`是visual interface的简称, 是Linux中最经典的文本编辑器。同图形化界面中的文本编辑器一样，vi是命令行下对文本文件进行编辑的绝佳选择。

​	`vim` 是 `vi `的加强版本，兼容 `vi `的所有指令，不仅能编辑文本，而且还具有 shell 程序编辑的功能，可以不同颜色的字体来辨别语法的正确性，极大方便了程序的设计和编辑性。

## 2.1	vi/vim编辑器的三种工作模式

### 2.11	命令模式（Command mode）

​	命令模式下，所敲的按键编辑器都理解为命令，以命令驱动执行不同的功能。此模式下，不能自由进行文本编辑。

![image-20221027215841573](https://image-set.oss-cn-zhangjiakou.aliyuncs.com/img-out/2022/10/27/20221027215841.png)

![image-20221027215846581](https://image-set.oss-cn-zhangjiakou.aliyuncs.com/img-out/2022/10/27/20221027215846.png)

![image-20221027215849668](https://image-set.oss-cn-zhangjiakou.aliyuncs.com/img-out/2022/10/27/20221027215849.png)

### 2.12	输入模式（Insert mode）

​	也就是所谓的编辑模式、插入模式。此模式下，可以对文件内容进行自由编辑。

### 2.13	底线命令模式（Last line mode）

​	 以`:`开始，通常用于文件的保存、退出。

![image-20221027215858967](https://image-set.oss-cn-zhangjiakou.aliyuncs.com/img-out/2022/10/27/20221027215858.png)

![输入模式命令模式底线命令模式](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/%E8%BE%93%E5%85%A5%E6%A8%A1%E5%BC%8F%E5%91%BD%E4%BB%A4%E6%A8%A1%E5%BC%8F%E5%BA%95%E7%BA%BF%E5%91%BD%E4%BB%A4%E6%A8%A1%E5%BC%8F.png)

​	*注：三者状态的切换途径**仅有**上图中的的路径*。



## 2.2	vim编辑器的使用

​	通过vi/vim命令编辑文件，会打开一个新的窗口，此时这个窗口就是：命令模式窗口。

### 2.21	操作示例

​	1.使用：vim hello.txt，编辑一个新文件，执行后进入的是命令模式

​	2.在命令模式内，按键盘 i ，进入输入模式

​	3.在输入模式内输入：itheima and itcast

![testtext](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/testtext.png)

​	4.输入完成后，按esc回退会命令模式

​	5.在命令模式内，按键盘 : ，进入底线命令模式

​	6.在底线命令内输入：wq，保存文件并退出vi编辑器

![saveandquit](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/saveandquit.png)

​	7.成功输入并保存

![savethencat](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/savethencat.png)



## 3.0	Linux用户和用户权限

​		无论是Windows、MacOS、Linux均采用多用户的管理模式进行权限管理。在Linux系统中，拥有最大权限的账户名为：root（超级管理员）

​	root用户拥有最大的系统操作权限，而普通用户在许多地方的权限是受限的：

- 普通用户的权限，一般在其HOME目录内是不受限的
- 一旦出了HOME目录，大多数地方，普通用户仅有只读和执行权限，无修改权限

## 3.10	与用户和用户权限有关的一些命令操作

### 3.11	su命令

​	功能：切换用户

​	语法：`su [-] 参数`

- 参数:用户名。也可以省略，省略表示切换到root
- `-`: `- `符号是可选的，表示是否在切换用户后==加载环境变量==，建议带上
- 附注: 切换用户后，可以通过`Exit`命令退回上一个用户，也可以使用快捷键：`Ctrl + D`



### 3.12	sudo命令

​	功能: 为单条命令临时赋予管理员权限

​	语法:  `sudo 其它命令`

​	附注: 并不是所有的用户，都有权利使用sudo，我们需要为==普通用户配置sudo认证==:

  		1. 切换到root用户，执行visudo命令，会自动通过vi编辑器打开：/etc/sudoers
  		2. 在文件的最后添加：`username ALL=(ALL) NOPASSWD: ALL`
  	   		1. - 其中最后的NOPASSWD:ALL 表示使用sudo命令，无需输入密码
  		3. 切换回普通用户，开始sudo



## 3.20	用户、用户组

​	Linux系统中可以：

- 配置多个用户
- 配置多个用户组
- 用户可以加入多个用户组中![用户用户组](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/%E7%94%A8%E6%88%B7%E7%94%A8%E6%88%B7%E7%BB%84.png)

 	Linux中关于权限的管控级别有2个级别，分别是：

- 针对**用户**的权限控制

- 针对**用户组**的权限控制

    比如，针对某文件，可以控制用户的权限，也可以控制用户组的权限。



### 3.21	用户组管理

​	注意，以下命令需要root权限。

​	功能: 创建用户组

​	语法:  `groupadd 用户组名`



​	功能: 删除用户组

​	语法:  `groupdel 用户组名`



### 3.22	用户管理

​	注意，以下命令需要root权限。

#### 3.221	创建用户

​	功能: 创建用户

​	语法: `useradd [-g -d] 用户名`

​	选项：-g指定用户的组，不指定-g，会创建同名组并自动加入，指定-g需要组已经存在，如已存在同名组，必须使用-g

​	选项：-d指定用户HOME路径，不指定，HOME目录默认在：/home/用户名

#### 3.222	删除用户

​	功能: 删除用户

​	语法: `userdel [-r] 用户名`

​	选项：-r，删除用户的HOME目录，不使用-r，删除用户时，HOME目录保留

#### 3.223	查看修改所属用户组

​	功能: 查看用户所属组

​	语法: `id [用户名]`

​	参数：用户名，被查看的用户，如果不提供则查看自身



​	功能: 修改用户所属组，将指定用户加入指定用户组

​	语法: `usermod -aG 用户组 用户名`



#### 3.225	查看当前系统中有哪些用户

​	功能: 使用getent命令，可以查看当前系统中有哪些用户/用户组

​	语法: `getent passwd`

​	附注: 使用此命令后每条数据共有7份信息，分别是：

​	用户名:密码(x):用户ID:组ID:描述信息(无用):HOME目录:执行终端(默认bash)

![getenttail](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/getenttail.png)

​	语法: `getent group`

​	附注: 包含3份信息，组名称:组认证(显示为x):组ID

![getentgroup](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/getentgroup.png)



## 3.30	权限

​	我们知道通过`ls -l `可以以列表形式查看内容。但我们不知道的是这里也显示了权限细节。

![group](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/group.png)

​	从左到右依次为：

1. 表示文件、文件夹的权限控制信息
2. 表示文件、文件夹所属用户
3. 表示文件、文件夹所属用户组

### 3.31	认知权限信息

![权限2](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/%E6%9D%83%E9%99%902.png)

​	举例：drwxr-xr-x，表示：

1. 这是一个文件夹，首字母d表示
2. 所属用户(上上图左2)的权限是：有r有w有x，rwx
3. 所属用户组(上上图右1)的权限是：有r无w有x，r-x ==（ `-` 表示无此权限）==
4. 其它用户的权限是：有r无w有x，r-x

​	对于`r、w、x`有:

- r表示读权限
- w表示写权限
- x表示执行权限

​	针对文件、文件夹的不同，rwx的含义有细微差别

- r，针对文件可以查看文件内容
    - 针对文件夹，可以查看文件夹内容，如ls命令
- w，针对文件表示可以修改此文件
    - 针对文件夹，可以在文件夹内：创建、删除、改名等操作
- x，针对文件表示可以将文件作为程序执行
    - 针对文件夹，表示可以更改工作目录到此文件夹，即cd进入

​	例如:

![sudoroot](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/sudoroot.png)

​	当前用户==非root==，非文件所属用户和用户组，锁定最后三位权限为：==---，无读、写、执行权限==

### 3.32	修改权限chmod

​	注意，只有文件、文件夹的==所属用户或root用户==可以修改其权限。

​	功能: 修改文件、文件夹的权限信息。

​	语法: `chmod [-R] 权限 文件或文件夹`

- 选项：`-R`，对文件夹内的全部内容应用同样的操作
- 附注: `chmod`命令来自英文词组“**Ch**ange **mod**e”的缩写

​	示例:将文件权限修改为：rwxr-x--x

![chmod](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/chmod.png)

### 3.33	权限的数字序号

​	除此之外，上一节中的命令还有快捷写法：`chmod 751 hello.txt`-->将hello.txt的权限修改为751

​	权限可以用3位数字来代表，第一位数字表示**用户权限**，第二位表示**用户组权限**，第三位表示**其它用户权限**。数字的细节如下：r记为4，w记为2，x记为1，可以有：

- 0：无任何权限， 即 ---
- 1：仅有x权限， 即 --x
- 2：仅有w权限 即 -w-
- 3：有w和x权限 即 -wx
- 4：仅有r权限 即 r--
- 5：有r和x权限 即 r-x
- 6：有r和w权限 即 rw-
- 7：有全部权限 即 rwx

​	所以751表示： rwx(7) r-x(5) --x(1)

### 3.33	修改文件所属用户、用户组chown

​	注意，普通用户无法修改所属为其它用户或组，所以此命令==只适用于root用户执行。==

​	功能: 可以修改文件、文件夹的所属用户和用户组

​	语法: `chown [-R] [用户]:[用户组] 文件或文件夹`

- 选项: `-R`，同chmod，对文件夹内全部内容应用相同规则
- 选项: 用户，修改所属用户
- 选项: ` : `，`:`冒号用于分隔用户和用户组  
- 选项: 用户组，修改所属用户组

​	示例:

- chown ==:root== hello.txt，将hello.txt所属用户组修改为root
- chown ==root:itheima== hello.txt，将hello.txt所属用户修改为root，用户组修改为itheima
- chown ==-R root== test，将文件夹test的所属用户修改为root并对文件夹内全部内容应用同样规则



## 4.0	Linux与软件

​	操作系统安装软件有许多种方式，一般分为：下载安装包自行安装和系统的应用商店内安装两种。Linux系统同样支持这两种方式。其中Linux命令行内的”应用商店”为**yum命令**安装软件

### 4.1	yum命令

​	功能：yum是RPM包软件管理器(CentOS系统)，用于自动化安装配置Linux软件，并可以自动解决依赖问题。RPM是Linux的安装包格式。

​	语法：`yum [-y] [install|remove|search] 软件安装`

- 选项：`-y`，自动确认，无需手动确认安装或卸载过程
- `install`：安装
- `remove`：卸载
- `search`：搜索
- 附注: yum命令需要**root权限**且**需要联网**。yum命令来自英文词组“**Y**ellowdog**U**pdater **M**odified”的缩写



### 4.2	systemctl命令

​	Linux系统很多软件（内置或第三方）均支持使用systemctl命令控制：启动、停止、开机自启。能够被systemctl管理的软件，一般也称之为：**服务**

​	语法: `systemctl start | stop | status | enable | disable | restart 服务名`

- 选项:`start` :启动
- `stop`:停止
- `status`:查看状态
- `enable`:关闭开机自启
- `disable`:开启开机自启
- `restart`: 重启
- ​	附注: 缩写来源不言自明。

​	功能: 查看Linux系统服务列表

​	语法:`systemctl list-unit-files`

#### 4.21	第三方软件与systemctl

​	除了内置的服务以外，部分第三方软件安装后也可以以systemctl进行控制。

- yum install -y ntp，安装ntp软件
    - 可以通过ntpd服务名，配合systemctl进行控制
- yum install -y httpd，安装apache服务器软件
    - 可以通过httpd服务名，配合systemctl进行控制



​	*注:部分软件安装后没有自动集成到systemctl中，我们可以手动添加。*



### 4.3	软连接

​	功能: 在系统中创建软链接，可以将文件、文件夹链接到其它位置。类似Windows系统中的==快捷方式==

​	语法：`ln -s 参数1 参数2`	

​	-s选项: 创建软连接

​	参数1：要链接去的目的文件/文件夹(最好使==用绝对路径==否则可能失败)

​	参数2：链接创建的路径位置(连接名)

![softlink1](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/softlink1.png)

![soflink2](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/soflink2.png)

​	注意到被链接去的文件/文件夹上并没有特殊标识。

![softlink3](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/softlink3.png)

​	链接成功。

#### 4.31	软连接的删除

​	语法: `rm -rf 软连接位置`

### 4.4	date命令

​	功能: 通过date命令可以在命令行中查看系统的时间。

​	语法: `date [-d] [+格式化字符串]`

​	`-d`选项: 按照给定的字符串显示日期，一般用于日期计算。其中支持的时间标记为：

- year 年
- month 月
- day 天
- hour 小时
- minute 分钟
- second 秒

![date](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/date.png)

​	`-d`选项可以和 格式化字符串配合一起使用。

​	格式化字符串：通过特定的字符串标记，来控制显示的日期格式，==有空格要加双引号“”==:

- %Y  年
- %y  年份后两位数字 (00..99)
- %m  月份 (01..12)
- %d  日 (01..31)
- %H  小时 (00..23)
- %M  分钟 (00..59)
- %S  秒 (00..60)
- %s  自 1970-01-01 00:00:00 UTC 到现在的秒数

#### 4.41	修改时区

​	使用root权限，执行如下命令，修改时区为东八区时区。

```
rm -f /etc/localtime
sudo ln -s /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
```

​	将系统自带的localtime文件删除，并将/usr/share/zoneinfo/Asia/Shanghai文件链接为localtime文件即可。

​	通过ntp程序手动校准时间:`ntpdate -u ntp.aliyun.com`

## 5.0	IP地址与主机名与域名

​	每一台联网的电脑都会有一个地址，用于和其它计算机进行通讯

​	IP地址主要有2个版本，IPV4版本和IPV6版本（V6很少用）

​	IPv4版本的地址格式是：a.b.c.d，其中abcd表示0~255的数字，如192.168.88.101就是一个标准的IP地址

### 5.1	特殊IP地址

- 127.0.0.1，这个IP地址用于指代本机
- 0.0.0.0，特殊IP地址
    - 可以用于指代本机
    - 可以在端口绑定中用来确定绑定关系
    - 在一些IP地址限制中，表示所有IP的意思，如放行规则设置为0.0.0.0，表示允许任意IP访问

### 5.2	主机名

​	每一台电脑除了对外联络地址（IP地址）以外，也可以有一个名字，称之为主机名。无论是Windows或Linux系统，都可以给系统设置主机名。Windows系统中的主机名就是设备名称。

#### 5.21	在Linux中修改主机名

​	查看:`hostname`

​	修改:`hostnamectl set-hostname 主机名`

### 5.3	域名解析

​	IP地址难以记忆。实际上，我们一直都是通过字符化的地址去访问服务器，很少指定IP地址。比如，我们在浏览器内打开：www.baidu.com，会打开百度的网址。其中，www.baidu.com，是百度的网址，我们称之为：**域名**。

![域名解析](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/%E5%9F%9F%E5%90%8D%E8%A7%A3%E6%9E%90.png)

​	配置主机名映射时:

- Windows看：C:\Windows\System32\drivers\etc\hosts

- Linux看：/etc/hosts

### 5.4	虚拟机配置固定IP

​	当前我们虚拟机的Linux操作系统，其IP地址是通过DHCP[^2]服务获取的。每次重启都有可能变化，此时我们就需要在Shell中重新设置，非常麻烦。

#### 5.41	在VMware Workstation（或Fusion）中配置IP地址网关和网段（IP地址的范围）

​	在==虚拟网络编辑器==中进行修改

#### 5.42	在Linux系统中手动修改配置文件，固定IP

	1. 使用vim编辑/etc/sysconfig/network-scripts/ifcfg-ens33文件

2. 执行：systemctl restart network 重启网卡，执行ifconfig即可看到ip地址固定为192.168.88.130了

## 6.0	网络传输

### 6.10	ping命令

​	功能: 检查指定的网络服务器是否是可联通状态

​	语法：`ping [-c num] ip或主机名`

​	选项：`-c`，检查的次数，不使用-c选项，将无限次数持续检查

### 6.20	wget命令

​	功能: wget是非交互式的文件下载器，可以在命令行内下载网络文件。

​	语法:`wget [-b] url`

​	选项：-b，可选，后台下载，会将日志写入到当前工作目录的wget-log文件

​	参数：url，下载链接

​	附注: wget命令来自英文词组“**W**eb **g**et”的缩写。无论下载是否完成，都会生成要下载的文件，如果下载未完成，请==及时清理未完成的不可用文件==。

### 6.30	curl命令

​	功能: curl可以发送http网络请求，可用于：下载文件、获取信息等

​	语法：`curl [-0] url`

- 选项：`-0`，用于下载文件，当url是下载链接时，可以使用此选项保存文件。 

- 参数：url，要发起请求的网络地址

### 6.40	端口

​	端口，是设备与外界通讯交流的出入口。端口可以分为：物理端口和虚拟端口两类

- 物理端口：又可称之为接口，是可见的端口，如USB接口，RJ45网口，HDMI端口等
- 虚拟端口：是指计算机内部的端口，是不可见的，是用来操作系统和外部进行交互使用的

#### 6.41	虚拟端口

​	计算机程序之间的通讯，通过IP只能锁定计算机，但是无法锁定具体的程序。通过==端口可以锁定计算机上具体的程序==，确保程序之间进行沟通。

​	Linux系统可以支持65535个端口，这6万多个端口分为3类进行使用：

- 公认端口：1~1023，通常用于一些系统内置或知名程序的预留使用，如SSH服务的22端口，HTTPS服务的443端口。非特殊需要，不要占用这个范围的端口。
- 注册端口：1024~49151，通常可以随意使用，用于松散的绑定一些程序/服务。
- 动态端口：49152~65535，通常不会固定绑定程序，而是当程序对外进行网络链接时，用于临时使用。

#### 6.42	查看端口占用nmap

​	①

​	功能:(需要先通过yum安装)nmap命令查看端口的占用情况

​	语法：`nmap 需要查看的IP地址`

![nmap](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/nmap.png)

​	②

​	功能:(需要先通过yum安装)查看指定**端口/进程的占用情况**

​	语法：`netstat -anp | grep 端口号/进程号`

### 6.50	上传和下载

​	我们可以通过FinalShell工具，方便的和虚拟机进行数据交换。

- 虚拟机->本地电脑: 下载
- 本地电脑->虚拟机: 直接拖拽即可

#### 6.51	rz和sz命令

	- 功能: 上传文件,输入后会弹出选项框。

​	语法:`rz`

	- 功能:下载文件(速度比拖拽==慢得多==)

​	语法:`sz 文件名`

### 6.60	压缩和解压

​	Linux中常用的压缩格式有`.zip	.tar	.gizp`。

​	Linux和Mac系统常用有2种压缩格式，后缀名分别是：

- `tar`，称之为tarball，归档文件，即简单的将文件组装到一个.tar的文件内，并没有太多文件体积的减少，仅仅是简单的封装

- `gz`，也常见为.tar.gz，gzip格式压缩文件，即使用gzip压缩算法将文件压缩到一个文件内，可以极大的减少压缩后的体积

#### 6.61	命令

​	针对`tar`和`gz`这两种格式，使用tar命令均可以进行压缩和解压缩的操作。

​	语法: `tar [-c -v -x -f -z -C] 参数1 参数2 ... 参数3`

- `参数1`,压缩包名/路径

- `-c`，创建压缩文件，用于压缩模式
- `-v`，显示压缩、解压过程，用于查看进度
- `-x`，解压模式
- `-f`，要创建的文件，或要解压的文件，-f选项必须在所有选项中位置处于最后一个
- `-z`，gzip模式，不使用-z就是普通的tarball格式
- `-C`，选择解压的目的地，用于解压模式,其后参数为目的地路径。

​	对于`zip`压缩包，可以使用zip命令。

​	语法: `zip [-r] 参数1 参数2 参数N`

- `参数1`,压缩包名/路径

	-  `-r`，被压缩的包含文件夹的时候，需要使用`-r`选项，和rm、cp等命令的-r效果一致。

####  6.62	常用组合

​	tar的常用**压缩组合**为：

- tar -cvf test.tar 1.txt 2.txt 3.txt
    - 将1.txt 2.txt 3.txt 压缩到test.tar文件内

- tar -zcvf test.tar.gz 1.txt 2.txt 3.txt

    - 将1.txt 2.txt 3.txt 压缩到test.tar.gz文件内，使用gzip模式

- 注意：

    - `-z`选项如果使用的话，一般处于选项位==第一个==

    - `-f`选项，**必须**在==选项位最后一个==



​	tar的常用**解压组合**为：

- tar -xvf test.tar

    - 解压test.tar，将文件解压至当前目录

- tar -xvf test.tar -C /home/itheima

    - 解压test.tar，将文件解压至指定目录（/home/itheima）

- tar -zxvf test.tar.gz -C /home/itheima

    - 以Gzip模式解压test.tar.gz，将文件解压至指定目录（/home/itheima）

- 注意：

    - `-f`选项，**必须**在==选项组合体的最后一位==

    - -`z`选项，建议在==开头位置==

    - `-C`选项**单独使用**，和解压所需的==其它参数分开==



​	zip的解压命令为:

​	语法:`unzip [-d] 参数`

- `-d`，指定要解压去的位置，同tar的`-C`选项。默认到同目录下
- `参数`,被解压的zip压缩包文件
- 附注: 解压时==有同名内容会直接替换！==

## 7.0	软件安装

​	我的Linux虚拟机tomcat用户密码:~~12345678~~(实际上我没设密码)

​	`tail -f /export/server/tomcat/logs/catalina.out`可以实时查看Tomcat最新日志信息。

## 8.0	进程

​	程序运行在操作系统中，是被操作系统所管理的。为了管理运行的程序，每一个程序在运行的时候，便被操作系统注册为系统中的一个进程。每一个进程都会被分配一个独有的：进程ID（进程号）

### 8.1	查看进程信息ps

​	功能：查看进程信息

​	语法：`ps -ef`，查看全部进程信息，可以搭配grep做过滤：`ps -ef | grep xxx`

​	选项：`-e`，显示出全部的进程

​	选项：`-f`，以完全格式化的形式展示信息（展示全部信息）

![](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/ps-ef.png)

​	**从左到右分别是：**

- `UID`：进程所属的用户ID
- `PID`：进程的进程号ID
- `PPID`：进程的父ID（启动此进程的其它进程）
- `C`：此进程的CPU占用率（百分比）
- `STIME`：进程的启动时间
- `TTY`：启动此进程的终端序号，如显示?，表示非终端启动
- `TIME`：进程占用CPU的时间
- `CMD`：进程对应的名称或启动路径或启动命令

### 8.2	关闭进程kill

​	功能:关闭进程

​	语法:`kill [-9] 进程ID`

​	选项：-9，表示强制关闭进程。不使用此选项会向进程发送信号要求其关闭，但是否关闭看进程自身的处理机制。

## 9.0	主机状态和环境变量

### 9.1	查看系统资源占用

​	功能:查看==CPU、内存使用情况==，类似Windows的任务管理器

​	语法:`top`

​	附注: 默认每5秒刷新一次，按q或ctrl + c退出

​	**命令内容详解**:

![top](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/top.png)

​	当top以交互式运行（非-b选项启动），可以用以下交互式命令进行控制。

​	![top选项](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/top%E9%80%89%E9%A1%B9.png)

![top交互式](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/top%E4%BA%A4%E4%BA%92%E5%BC%8F.png)

### 9.2	磁盘信息监控

​	功能：查看硬盘的使用情况

​	语法：`df [-h]`

​	选项：`-h`，以更加人性化的单位显示

### 9.3	CPU和磁盘信息监控

​	功能: 可以使用iostat查看CPU、磁盘的相关信息

​	语法：`iostat [-x] [num1] [num2]`

​	选项：`-x`，显示更多信息

​	num1：数字，刷新间隔，num2：数字，刷新几次

​	红框内是CPU的信息。

![iso](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/iso.png)

​	tps：该设备每秒的传输次数（Indicate the number of transfers per second that were issued to the device.）。"一次传输"意思是"一次I/O请求"。多个逻辑请求可能会被合并为"一次I/O请求"。"一次传输"请求的大小是未知的。

​	`-x`可以显示更多信息:

- rrqm/s： 每秒这个设备相关的读取请求有多少被Merge了（当系统调用需要读取数据的时候，VFS将请求发到各个FS，如果FS发现不同的读取请求读取的是相同Block的数据，FS会将这个请求合并Merge, 提高IO利用率, 避免重复调用）；
- wrqm/s： 每秒这个设备相关的写入请求有多少被Merge了。
- rsec/s： 每秒读取的扇区数；sectors
- wsec/： 每秒写入的扇区数。
- rKB/s： 每秒发送到设备的读取请求数
- wKB/s： 每秒发送到设备的写入请求数
- avgrq-sz  平均请求扇区的大小
- avgqu-sz  平均请求队列的长度。毫无疑问，队列长度越短越好。  
- await：  每一个IO请求的处理的平均时间（单位是微秒毫秒）。
- svctm   表示平均每次设备I/O操作的服务时间（以毫秒为单位）
- %util：  磁盘利用率

### 9.4	网络状态监控

​	功能: 可以使用sar命令查看网络的相关统计（sar命令非常复杂，这里仅简单用于统计网络）

​	语法：`sar -n DEV num1 num2`

​	选项：`-n`，查看网络，DEV表示查看网络接口

​	num1：刷新间隔（不填就查看一次结束），num2：查看次数（不填无限次数）

![sar](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/sar.png)

​	**信息解读：**

- IFACE 本地网卡接口的名称
- rxpck/s 每秒钟接受的数据包
- txpck/s 每秒钟发送的数据包
- rxKB/S 每秒钟接受的数据包大小，单位为KB
- txKB/S 每秒钟发送的数据包大小，单位为KB
- rxcmp/s 每秒钟接受的压缩数据包
- txcmp/s 每秒钟发送的压缩包
- rxmcst/s 每秒钟接收的多播数据包

### 9.5 	环境变量

​	环境变量是操作系统（Windows、Linux、Mac）在运行的时候，记录的一些关键性信息，用以辅助系统运行。

​	在Linux系统中执行：`env` 命令即可查看当前系统中记录的环境变量

​	环境变量是一种==KeyValue型结构，即名称和值==，如下图：

![env](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/env.png)

#### 9.51	环境变量:PATH

​	无论当前工作目录是什么，都能执行/usr/bin/cd这个程序，这个就是借助环境变量中：PATH这个项目的值来做到的。

![PATH](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/PATH.png)

​	PATH记录了系统执行**任何命令的搜索路径**。

​	当执行任何命令，都会按照顺序，从上述路径中搜索要执行的程序的本体:

​		比如执行cd命令，就从第二个目录/usr/bin中搜索到了cd命令，并执行。

### 9.6	符号$

​	在Linux系统中，$符号被用于取”变量”的值。环境变量记录的信息，除了给操作系统自己使用外，如果我们想要取用，也可以使用。

​	取得环境变量的值就可以通过语法：$环境变量名 来取得

![$PATH](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/$PATH.png)

​	当和其它内容混合在一起的时候，可以通过{}来标注取的变量是谁

### 9.7	自行设置环境变量

​	Linux环境变量可以用户自行设置，其中分为：

- 临时设置，语法：export 变量名=变量值（某个窗口）

- 永久生效

    - 针对当前用户生效，配置在当前用户的：` ~/.bashrc`文件中

    - 针对所有用户生效，配置在系统的： `/etc/profile`文件中

        并通过语法：source 配置文件，进行立刻生效，或重新登录FinalShell生效

### 9.8	自定义环境变量PATH

​	环境变量PATH这个项目里面记录了==系统执行命令的搜索路径==。这些搜索路径我们也可以自行添加到PATH中去。

​	临时修改PATH：`export PATH=$PATH:/home/用户名/文件夹名 `。

​	或将`export PATH=$PATH:/home/用户名/文件夹名`，填入用户环境变量文件或系统环境变量文件中去

## 10.0	快捷键与一些补充内容

### 10.1	Ctrl+C终止某些正在运行的程序

- Linux某些程序的运行，如果想要强制停止它，可以使用快捷键ctrl + c
- 命令输入错误，也可以通过快捷键ctrl + c，退出当前输入，重新输入

### 10.2	**ctrl + d** 退出或登出

- 可以通过快捷键：ctrl + d，退出账户的登录
- 或者退出某些特定程序的专属页面(例如python)
- 不能用于退出vi/vim

### 10.3	history历史命令搜索

- 可以通过`history`命令，查看历史输入过的命令

- 可以通过：`!`命令前缀+字符，自动执行上一次匹配前缀的命令

    - 例如输入`!py`，从history中从下往上搜索py开头的第一条命令执行

- 可以通过快捷键：`ctrl + R`，输入内容去匹配历史命令

    - 如果搜索到的内容是你需要的，那么：

        - 回车键可以直接执行

            键盘左右键，可以得到此命令（不执行）



### 10.4	光标移动快捷键

- `ctrl + a`，跳到命令开头
- `ctrl + e`，跳到命令结尾
- `ctrl + 键盘左键`，向左跳一个单词
- `ctrl + 键盘右键`，向右跳一个单词

### 10.5	清屏

- 通过快捷键ctrl + l，可以清空终端内容
- 或通过命令clear得到同样效果

​	









[^1]: 在Linux系统中，通配符是一种特殊的字符或字符序列，用于匹配文件名或路径名中的多个文件或目录。例如星号（*）:匹配任意长度的任意字符。问号（?）: 匹配单个字符。方括号（[]）:匹配方括号内的任意一个字符。花括号（{}）:匹配花括号中的任意一个字符串。



[^2]: DHCP：动态获取IP地址，即每次重启设备后都会获取一次，可能导致IP地址频繁变更

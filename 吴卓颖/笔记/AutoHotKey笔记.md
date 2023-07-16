# AutoHotKey笔记

## 1.0	热键和热子串

​	热键是一个可以触发某个动作的键或组合键。 热键是通过一对冒号(::) 创建的. 按键名或组合按键名必须在 `::` 的**左边**. 代码则跟在后面, 括在大括号里面:

```autohotkey
^j::
{
    Send "My First Script"
}；Ctrl+J 执行"My First Script"
```

​	热字串主要用于扩展缩写(自动替换). 当然, 它也可以用来启动任何脚本动作. 例如:

```
::ftw::Free the whales;将你输入的 "ftw" 转换为 "Free the whales"
::btw::
{
    MsgBox "You typed btw."
};启动脚本动作
```

### 1.1	热键及其对应符号

![Snipaste_2023-07-10_15-05-41](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/Snipaste_2023-07-10_15-05-41.png)

​	此外，可以通过在两个按键(除控制器按键) 之间, 使用 ` & ` 来定义一个组合热键. 在下面的例子中, 要按下`Numpad0`, 再按下`Numpad1` 或 `Numpad2`, 才能触发热键:

```
Numpad0 & Numpad1::
{
    MsgBox "You pressed Numpad1 while holding down Numpad0."
}

Numpad0 & Numpad2::
{
    Run "notepad.exe"
}
```

### 1.3	指定热键和热子串的作用范围

​	若要限制脚本中热键和热子串的作用范围，需要使用任意一个 "高级" 命令, 它们前面带有一个 #, 即 `#HotIf`, 结合内置函数 `WinActive`或` WinExist`:

```
#HotIf WinActive(WinTitle)
#HotIf WinExist(WinTitle)
```

​	这些特殊的命令(技术上称为 "指令") 可以创建对上下文敏感的热键和热字串. 只需为 `WinTitle` 指定窗口参数(这里的参数有很多种，详情查阅文档)

```
#HotIf WinActive("Untitled - Notepad")
#Space::
{
    MsgBox "You pressed WIN+SPACE in Notepad."
}
```

​	默认状况下, 所有的热键和热字串对所有窗口生效。且一个`#HotIf`的作用范围会**一直延伸**到下一个`#Hotif`为止。

```
; 对于所有Untitled - Notepad的窗口，以下热键生效
#HotIf WinActive("Untitled - Notepad")
!q::
{
    MsgBox "You pressed ALT+Q in Notepad."
}

; 对于所有不是Untitled - Notepad的窗口,以下热键才会生效
#HotIf
!q::
{
    MsgBox "You pressed ALT+Q in any window."
}
```

## 2.0	Send函数

​	当需要发送(输入)一些按键到一个程序(窗口)中时，我们可以使用Send函数。该函数表示发送按键, 模拟打字或按键操作。Send函数中也有一些特殊的键，以下是常见特殊键表格:

![Send的特殊按键](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/Send%E7%9A%84%E7%89%B9%E6%AE%8A%E6%8C%89%E9%94%AE.png)

### 2.1	Send与热键

​	`Ctrl`、`Enter`、`Alt`和`Win`作为热键时，不能把它们括在`{ }`中。以下是例子:

```
; 创建热键时
; 错误的例子
{LCtrl}::
{
    Send "AutoHotkey"
}

; 正确的例子
LCtrl::
{
    Send "AutoHotkey"
}
```

​	**大括号**在AHK中有特别的作用。例如，它将告诉 AutoHotkey `{!}` 表示 "感叹号", 而不是要 "按下 Alt 键"。 所以要仔细查看文档上的特殊键表格, 确保在合适的地方加上大括号。

​	**但需要注意**的是，若要发送除特殊键之外的键，则**必需**将按键的名称括在大括号中。

```
Send "This text has been typed{!}" ;  注意大括号中的感叹号。如果没有 {}, AHK 将按下 Alt 键
; 跟上面的例子不同。如果 Enter 没有加上 {} 的话, AHK 将会输出 "Enter"(即直接回车)
Send "Multiple Enter lines have Enter been sent." ; 错误的
Send "Multiple{Enter}lines have{Enter}been sent." ; 正确的
```

​	如果要表示的字符对应的按键**不在**特殊按键列表中, 没必要加大括号. 你**不**需要给普通字符, 数字加上括号, 甚至像`.`(句号) 这些符号加上 `{ }`。而且, 当在使用 Send 函数时, 可以一次性发送多个字符, 数字或符号. 所以没有必要为每一个字符写上一条 Send 函数。

​	想要表示按住或松开某个按键, 可以将这个键用花括号围起来, 同时加上单词 UP 或 DOWN. 例如:

```
; 下面这个例子表示按下一个键的时候再按下另一个键(或多个键)..
; 如果其中一个方法不奏效, 试试另一个.
Send "^s"                     ; 表示发送 CTRL+S
Send "{Ctrl down}s{Ctrl up}"  ; 表示发送 CTRL+S
Send "{Ctrl down}c{Ctrl up}";	表示CTRL+C
Send "{b down}{b up}";	表示按下并松开b键
Send "{Tab down}{Tab up}"
Send "{Up down}"  ; 按下向上键.
Sleep 1000        ; 保持 1 秒.
Send "{Up up}"    ; 然后松开向上键.
```

​	*注：`CTRL`和`Shift`等特殊按键在Send时会且仅会对其后一个字符产生影响(其它按键和实际情况待测试)*
# ApiFox笔记

## 0.0	写在前面

​	当我开始学Apifox的时候，我使用最终考核的项目进行测试。我发现无论是在Params里还是在Body的json里设置参数，我都无法正常的发送POST请求。最后通过在IDEA打断点我发现，我项目里前端js代码发过去的请求体里的数据根本不是`json`，而是`x-www-form-urlencoded`:cry:。

```javascript
 $("#usernameOrPassword-error").text("");
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: 'http://localhost:8080/QGFinal_war/user',
                data: {
                    username: username,
                    password: password,
                    method: "login",
                    remember: remember
                },
                dataType: "json",
                ......
```

​	这样子放在请求体里的数据根本不是`JSON`格式。而是一种类似表单的格式

![x-www-form](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/x-www-form.png)

​	可以看到即使前端ajax里设置了`contentType`为`application/json`后台接受请求时结果为`application/x-www-form-urlencoded.`

​	这种格式的字符串如图所示。

![requestbody](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/requestbody.png)

​	最后设置的请求体如下:

![requestbody2](https://mytyporapicute.oss-cn-guangzhou.aliyuncs.com/typoraPics/requestbody2.png)

## 1.0	设置接口和文档

​	帮助文档连接https://apifox.com/help/api-docs/api-design

​	我自己搞的接口文档:https://apifox.com/apidoc/shared-808b82ed-d97e-4818-9c1c-4b20e0de8fd5

​	项目设置里可以导出不同格式的API接口文档。

## 2.0	API MOCK功能

​	可以通过设置==MOCK预设，正则表达式，枚举值==等方式来规定**随机**生成的接口测试数据。

## 3.0	API 调试

​	接口请求参数可以设置==动态值==，也可以支持==常量、环境变量、全局变量。==最后还可以==调用加密、lowercase、子字符串==的函数。

​	后端自测接口时可以把各种情况都保存为一个`用例`。可以方便测试各种情况。

#### 3.05	前置后置操作

​	接口里可以设置后置操作:==断言、提取变量、连接数据库、POSTMAN的自动化脚本、公共脚本(获取token大概也是在这里操作)==等等等。**目录级**和**项目级**的前置后置操作也是支持的。

### 3.1	响应

​	我们设置好响应Response的**数据结构**时，在进行`接口调试`时，系统会自动校验返回的数据==是否符合定义的数据结构==。

​	如果一个接口在不同情况下返回不一样的数据结构，那么我们也可以设置多个`返回响应`。

#### 3.11	公共响应

​	公共响应主要用来实现返回响应的复用。通常不同接口在某些情况下会返回相同的数据结构，如`资源不存在(404)`、`没有访问权限(401)`等，这些可以设置为`公共响应`，避免重复编写，方便统一管理。

### 3.2	成功/失败示例

​	点击`响应示例`模块右上方的`+ 新建`按钮即可添加多个示例。通过示例让API文档更加清晰易懂。

### 3.3	联调环节测试环境

​	Apifox的环境管理支持==每个环境==拥有不同的前置url服务。也可以对==单个接口==设置前置url服务

## 4.0	自动化测试

### 4.1	接口导入

​	接口导入时有==复制==和==绑定==，两种模式，注意区分。

### 4.2	使用数据集

​	自动化测试支持使用已有或新建的==数据集==。

### 4.3	性能测试

​	可以使用==多线程测试==。 

### 4.4	测试套件

​	顾名思义，批量测试。


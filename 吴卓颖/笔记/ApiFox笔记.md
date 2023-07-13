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

## 2.0	API MOCK功能


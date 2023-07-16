---
title: 个人项目 v1.0.0
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.17"

---

# 个人项目

> v1.0.0

Base URLs:

* <a href="http://test-cn.your-api-server.com">测试环境: http://test-cn.your-api-server.com</a>

# Default

## POST QGShop服务器版登陆接口

POST /QGShop/user

这是登录页面的接口

> Body 请求参数

```yaml
username: 123
password: 12345678
method: login
remember: true{% mock 'boolean' %}

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Content-Type|header|string| 否 |none|
|body|body|object| 否 |none|
|» username|body|string| 否 |none|
|» password|body|string| 否 |none|
|» method|body|string| 否 |none|
|» remember|body|string| 否 |none|

> 返回示例

> 成功

```json
{
  "code": 200,
  "data": {},
  "message": "成功,请求正常处理并返回数据",
  "success": true
}
```

> 请求有误

```json
{
  "code": 400,
  "data": {},
  "message": "失败,请求参数错误",
  "success": false
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|成功|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|请求有误|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|代码|none|
|» data|object|true|none|数据|none|
|» message|string|true|none|消息|none|
|» success|boolean|true|none|是否成功|none|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|代码|none|
|» data|object|true|none|数据|none|
|» message|string|true|none|消息|none|
|» success|boolean|true|none|是否成功|none|

# 数据模型

<h2 id="tocS_QGShopPostRequest">QGShopPostRequest</h2>

<a id="schemaqgshoppostrequest"></a>
<a id="schema_QGShopPostRequest"></a>
<a id="tocSqgshoppostrequest"></a>
<a id="tocsqgshoppostrequest"></a>

```json
{
  "username": "string",
  "password": "string",
  "method": "string",
  "remeber": true
}

```

QG最终考核的POST请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none|用户名|none|
|password|string|true|none|密码|none|
|method|string|true|none|方法|none|
|remeber|boolean|true|none|响应|none|


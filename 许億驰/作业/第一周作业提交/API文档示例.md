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

## POST Qgmall服务器版登陆接口

POST /Qgmall/login.jsp

这是登录页面的接口

> Body 请求参数

```yaml
username: admin
password: admin
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
|» code|integer|true|none||none|
|» data|object|true|none||none|
|» message|string|true|none||none|
|» success|boolean|true|none||none|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» data|object|true|none||none|
|» message|string|true|none||none|
|» success|boolean|true|none||none|

# 数据模型

<h2 id="tocS_QGmallPostRequest">QgmallPostRequest</h2>

<a id="schemaqgmallrequest"></a>
<a id="schema_QGmallPostRequest"></a>
<a id="tocSqgmallpostrequest"></a>
<a id="tocsqgmallpostrequest"></a>

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
|username|string|true|none||none|
|password|string|true|none||none|
|method|string|true|none||none|
|remeber|boolean|true|none||none|


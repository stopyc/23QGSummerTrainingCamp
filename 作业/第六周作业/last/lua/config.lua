--WAF config file,enable = "on",disable = "off"

--waf status
config_waf_enable = "on"
--log dir
config_log_dir = "/tmp"
--rule setting
config_rule_dir = "/usr/local/openresty/nginx/conf/waf/rule-config"
--enable/disable white url
config_white_url_check = "on"
--enable/disable white ip
config_white_ip_check = "on"
--enable/disable block ip
config_black_ip_check = "on"
--enable/disable url filtering
config_url_check = "on"
--enalbe/disable url args filtering
config_url_args_check = "on"
--enable/disable user agent filtering
config_user_agent_check = "on"
--enable/disable cookie deny filtering
config_cookie_check = "on"
--enable/disable cc filtering
config_cc_check = "on"
--cc rate the xxx of xxx seconds
config_cc_rate = "10/60"
--enable/disable post filtering
config_post_check = "on"
--config waf output redirect/html
config_waf_output = "html"
--if config_waf_output ,setting url
config_waf_redirect_url = "https://www.unixhot.com"
config_output_html=[[
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Content-Language" content="zh-cn" />
<title>OpsAny｜Web应用防火墙</title>
</head>
<body>
<h1 align="center"> 欢迎白帽子进行授权安全测试，安全漏洞请联系QQ：57459267
</body>
</html>
]]
html_content = [[
<!DOCTYPE html>
<html lang="en">

<head>
    
    <meta charset="UTF-8">
  
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>IP封禁通知</title>
</head>
<style>
    * {
        margin: 0;
        padding: 0;
    }

    body {
        font-family: cursive;
        height: 100vh;
        text-align: center;
    }

    h1 {
        margin: 4% 0;
        font-size: 24px;
    }

    p {
        margin-bottom: 4%;
    }
</style>

<body style="font-family:cursive;
        height: 100vh;
        text-align: center;">
    <h1 style="margin: 4% 0;
        font-size: 24px;text-align:center ">您的IP已经被当前服务器封禁</h1>
     <p style=" margin-bottom: 4%;text-align:center  ">很抱歉，由于某些原因，您的IP地址被当前服务器封禁了。</p >
    <p style=" margin-bottom: 4%;text-align:center  ">如果您认为这是一个错误，请联系管理员以解决此问题。</p >
   <div id="showTime" style="text-align:center  "></div>
   
    <script>
        const showTimeElement = document.getElementById('showTime');

        // 创建定时器
        setInterval(() => {
            const dt = new Date();
            const y = dt.getFullYear();
            const mt = dt.getMonth() + 1;
            const day = dt.getDate();
            const h = dt.getHours();
            const m = dt.getMinutes();
            const s = dt.getSeconds();

            const timeString =
                "当前封禁时间：" +
                y +
                "年" +
                mt +
                "月" +
                day +
                "-" +
                h +
                "时" +
                m +
                "分" +
                s +
                "秒";

            showTimeElement.textContent = timeString;
        }, 1000); 
    </script>
</body>

</html>
]]

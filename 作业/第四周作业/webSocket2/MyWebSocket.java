package com.hekai.webSocket2;

import org.springframework.web.bind.annotation.*;

/**
 * \* User: hekaijie
 * \* Date: 2023/8/14
 * \* Time: 16:47
 * \* Description:
 * \
 */
@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class MyWebSocket {
    @GetMapping("/sendMsg")
    @ResponseBody
    public boolean sendMsg() {
        return MySocketClient.sendMessage("hello");
    }

}

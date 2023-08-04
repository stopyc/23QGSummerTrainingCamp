package com.yinjunbiao.controller;


import com.yinjunbiao.domain.User;
import com.yinjunbiao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yinjunbiao
 * @since 2023-07-19
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result login(@RequestBody User user){
        return userService.login(user);
    }

    @PostMapping
    public Result register(@RequestBody User user){
        return userService.register(user);
    }

}


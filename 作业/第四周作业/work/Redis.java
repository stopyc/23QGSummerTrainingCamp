package com.qgstudio.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.qgstudio.controller.Result;
import com.qgstudio.entity.User;
import com.qgstudio.util.BCryptUtil;
import com.qgstudio.util.EmailUtil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * \* User: hekaijie
 * \* Date: 2023/8/5
 * \* Time: 21:23
 * \* Description:
 * \
 */
public class Redis {
    @Override
    public Result register(Map<String, String> params) {
        User user = new User();
        //获取用户信息和验证码  对密码加密
        user.setEmail(params.get("email"));
        user.setPassword(BCryptUtil.hashPassword(params.get("password")));
        user.setUsername(params.get("username"));
        user.setPhone(params.get("phone"));
        user.setPower(0);
        String registerCode = params.get("emailCode");
        System.out.println(registerCode);
        String email = params.get("email");

        String emailCode = stringRedisTemplate.opsForValue().get("EmailSender:" + email);
        if (emailCode == null || !emailCode.equals(registerCode)) {
            //验证码不存在或者已经过期
            LOGGER.info("验证码: {} 验证失败", emailCode);
            return Result.error(Result.USERFAIL, "验证码错误");
        }
        LOGGER.info("验证码: {} 验证成功", emailCode);
        // 检查该用户是否已经注册
        if (userMapper.login(user.getEmail()) != null) {
            //用户已经被注册
            LOGGER.info("该邮箱已注册");
            return Result.error(Result.USERFAIL, "该邮箱已被注册");
        }
        //未注册且验证成功,添加信息到数据库
        int insert = userMapper.insert(user);
        if (insert == 1) {
            LOGGER.info("注册成功");
            stringRedisTemplate.delete("EmailSender:" + email);
            return Result.success();
        }
        return Result.error(Result.USERFAIL, "注册失败,请重试");
    }

    /**
     * 发送验证码并保存在session里
     *
     * @return 返回操作结果
     */
    @Override
    public Result sendCode(String email) {
        // 1.生成验证码和发送消息
        String code = RandomUtil.randomNumbers(4);
        String text = "qgassist" + "给您发来了验证码, 有效期为5分钟";
        if (!EmailUtil.sendEmail(email, text, code)) {
            //发送失败
            return Result.error(Result.USERFAIL, "验证码发送失败, 请重试");
        }
        // 2.发送成功, 把验证码存在redis
        stringRedisTemplate.opsForValue().set("EmailSender:" + email, code, 5, TimeUnit.MINUTES);
        LOGGER.info("验证码:{}", code);
        return Result.success();

    }
}

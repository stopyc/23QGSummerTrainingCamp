package com.yinjunbiao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.yinjunbiao.controller.Result;
import com.yinjunbiao.domain.User;
import com.yinjunbiao.mapper.UserDao;
import com.yinjunbiao.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yinjunbiao
 * @since 2023-07-19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public Result login(User user) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getPhone,user.getPhone()).eq(User::getPassword,user.getPassword());
        List<User> list = userDao.selectList(lambdaQueryWrapper);
        if (list != null && list.size() == 1) {
            return new Result(1, "success");
        }
        return new Result(0, "error");
    }


    @Override
    public Result register(User user) {
        int i = 0;
        try {
            i = userDao.insert(user);
        } catch (Exception e) {
            return new Result(0, "phone", "该手机号已被注册");
        }
        return new Result(1, "success");
    }
}

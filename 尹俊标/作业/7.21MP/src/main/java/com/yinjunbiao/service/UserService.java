package com.yinjunbiao.service;

import com.yinjunbiao.controller.Result;
import com.yinjunbiao.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yinjunbiao
 * @since 2023-07-19
 */
public interface UserService extends IService<User> {

    Result login(User user);

    @Transactional
    Result register(User user);

}

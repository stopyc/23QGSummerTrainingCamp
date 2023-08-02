package com.yinjunbiao;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yinjunbiao.domain.User;
import com.yinjunbiao.mapper.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MpApplicationTests {

    @Autowired
    private UserDao userDao;

    @Test
    void contextLoads() {
//        List<User> select = userDao.select("1");
//        System.out.println(userDao);

//        List<User> users = userDao.selectList(lambdaQueryWrapper);
//        System.out.println(users);
//        System.out.println(users.size());
//        System.out.println(page.getRecords());
//        System.out.println(page.getPages());
//        com.yinjunbiao.domain.User user = (com.yinjunbiao.domain.User) userDao.selectList(null);
//        System.out.println(user);
    }

}

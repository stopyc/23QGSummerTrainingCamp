package com.yinjunbiao.mapper;

import com.yinjunbiao.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yinjunbiao
 * @since 2023-07-19
 */
@Mapper
public interface UserDao extends BaseMapper<User> {

}

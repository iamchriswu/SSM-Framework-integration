package com.chris.service.impl;

import com.chris.mapper.UserMapper;
import com.chris.mapper.UserMapperCustom;
import com.chris.pojo.User;
import com.chris.pojo.UserCustom;
import com.chris.pojo.UserQueryVo;
import com.chris.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMapperCustom userMapperCustom;

    @Override
    public int insertUser(User user) throws Exception {
        user.setTime(new Date());
        return userMapper.insert(user);
    }

    @Override
    public UserCustom findUserByUserNameAndPassword(UserQueryVo userQueryVo) throws Exception {
        return userMapperCustom.selectUserByUserNameAndPwd(userQueryVo);
    }
}

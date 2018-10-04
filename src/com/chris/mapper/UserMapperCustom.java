package com.chris.mapper;

import com.chris.pojo.UserCustom;
import com.chris.pojo.UserQueryVo;

public interface UserMapperCustom {
    UserCustom selectUserByUserNameAndPwd(UserQueryVo userQueryVo);
}

package com.thunisoft.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.thunisoft.web.model.User;
/**
 * @Author: Diodeme
 * @Date: 2019/5/14 12:53
 * @Version 1.0
 */
@Mapper
public interface UserMapper {
    User getUserById(int id);
}

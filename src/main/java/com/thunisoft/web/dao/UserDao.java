package com.thunisoft.web.dao;

import com.thunisoft.web.mapper.UserMapper;
import com.thunisoft.web.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
//TODO Dao不应该存在 应该是mapper和mapper.xml
public class UserDao{
    @Autowired
    UserMapper userMapper;
    public User findByAccount(String account)
    {
        User user=userMapper.getUserById(1);
        return user;
    }

	
}

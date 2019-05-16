package com.thunisoft.web.controller;

import com.thunisoft.web.mapper.UserMapper;
import com.thunisoft.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Diodeme
 * @Date: 2019/5/14 13:15
 * @Version 1.0
 */
@RestController
@RequestMapping
public class UserController_bak {
    @Autowired
    UserMapper userMapper;
    @GetMapping("/test")
    public String getUser(@RequestParam int id)
    {
        User user=userMapper.getUserById(id);
        return user.toString();
    }

}

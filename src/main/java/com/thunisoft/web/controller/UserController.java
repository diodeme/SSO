package com.thunisoft.web.controller;

import com.thunisoft.web.model.webResult;
import com.thunisoft.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @Author: Diodeme
 * @Date: 2019/5/14
 */

/**
 * 用户控制类
 * 登录，注册，注销
 */
@RestController
@RequestMapping("/user")
public class UserController {
	/**
     * 注入userService
	 */
	@Autowired
	private UserService userService;
	//TODO 1.注册
	//TODO 3.单点登录

    /**
     * 用户登录
     * @param account 用户帐号
     * @param password 用户密码
     * @param request http request
     * @param response http response
     * @return webResult对象 含状态码
     */
	@RequestMapping(value="/login", method= RequestMethod.POST)
	public webResult userLogin(@RequestParam(value = "account",defaultValue = "") String account, @RequestParam(value = "password",defaultValue = "") String password,
							   HttpServletRequest request, HttpServletResponse response) {
		try {
			webResult result = userService.userLogin(account, password, request, response);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return webResult.build(500, "");
		}
	}

    /**
     * 页面预加载时传递cookie
     * @param request
     * @param response
     * @return webResult对象 包含状态码
     */
    @RequestMapping(value="/login", method= RequestMethod.GET)
    public webResult cookie(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(null==cookies)
            return webResult.build(250,"无cookie");
        try {
            webResult result = userService.queryUserByToken(cookies[0].getValue());
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            return webResult.build(500, "");
        }

    }

    /**
     * 注销
	 * @param token 用户token
	 * @return index页面
	 */
	@RequestMapping(value="/logout/{token}")
	public String logout(@PathVariable String token) {
		userService.logout(token); // 思路是从Redis中删除key，实际情况请和业务逻辑结合
		return "index";
	}

    /**
     * 根据token寻找用户
	 * @param token 用户token
	 * @return webResult对象
	 */
	@RequestMapping("/token/{token}")
	public Object getUserByToken(@PathVariable String token) {
		webResult result = null;
		try {
			result = userService.queryUserByToken(token);
		} catch (Exception e) {
			e.printStackTrace();
			result = webResult.build(500, "");
		}
		return result;
	}
}

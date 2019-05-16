package com.thunisoft.web.controller;

import com.thunisoft.web.model.webResult;
import com.thunisoft.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {
	/**
     * 注入userService
	 */
	@Autowired
	private UserService userService;

	/**
	 * 用户登录controller，直接与前端进行交互的函数
	 * @param account 用户名
	 * @param password 密码
	 * @param request 似乎是自动生成
	 * @param response 似乎是自动生成
	 * @return ItdragonResult对象
	 */
	@RequestMapping(value="/login", method= RequestMethod.GET)
	public webResult userLogin(@RequestParam String account, @RequestParam String password,
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
	 * @return ItdragonResult对象
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

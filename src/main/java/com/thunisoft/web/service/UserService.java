package com.thunisoft.web.service;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.thunisoft.web.model.webResult;
import com.thunisoft.web.model.User;
import com.thunisoft.web.component.JedisClient;
import com.thunisoft.web.mapper.UserMapper;
import com.thunisoft.web.utils.CookieUtils;
import com.thunisoft.web.utils.webUtils;
import com.thunisoft.web.utils.JsonUtils;

/**
 *
 * 它负责三件事:
 * 第一件事情：验证用户信息是否正确，并将登录成功的用户信息保存到Redis数据库中。
 * 第二件事情：负责判断用户令牌是否过期，若没有则刷新令牌存活时间。
 * 第三件事情：负责从Redis数据库中删除用户信息。
 * 主要有四个函数：
 * registerUser：用户注册
 * userLogin：用户登录
 * logout：用户注销
 * queryUserByToken：根据用户token在redis查找用户信息
 */
@Service
//@Transactional
@PropertySource(value = "classpath:redis.properties")
public class UserService {
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${REDIS_USER_SESSION_KEY}")
	private String REDIS_USER_SESSION_KEY;
	
	@Value("${SSO_SESSION_EXPIRE}")
	private Integer SSO_SESSION_EXPIRE;

    /**
     * 用户注册
     * @param user 用户对象
     * @return webResult对象
     */
    public webResult registerUser(User user) {
    	// 检查用户名是否注册，一般在前端验证的时候处理，因为注册不存在高并发的情况，这里再加一层查询是不影响性能的
    	if (null != userMapper.findByAccount(user.getAccount())) {
    	    //如果已经注册，返回报错
    		return webResult.build(400, "");
    	}
    	//TODO 更新数据库中的条目 自写
    	//userMapper.save(user);
    	// 注册成功后选择发送邮件激活。现在一般都是短信验证码
    	return webResult.build(200, "");
    }

    /**
     *
     * @param account 账户
     * @param password 密码
     * @param request 客户端请求
     * @param response 服务器响应
     * @return
     */
    public webResult userLogin(String account, String password,
			HttpServletRequest request, HttpServletResponse response) {
    	// 判断账号密码是否正确
		User user = userMapper.findByAccount(account);
		//ItdragonUtils用于加密解密
		if (!webUtils.decryptPassword(user, password)) {
			return webResult.build(400, "账号名或密码错误");
		}
		// 生成token
		String token = UUID.randomUUID().toString();
		// 清空密码和盐避免泄漏
		String userPassword = user.getPassword();
		String userSalt = user.getSalt();
		user.setPassword(null);
		user.setSalt(null);
		// 把用户信息写入 redis
		jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token, JsonUtils.objectToJson(user));
		// user 已经是持久化对象了，被保存在了session缓存当中，
        // 若user又重新修改了属性值，那么在提交事务时，
        // 此时 hibernate对象就会拿当前这个user对象和保存在session缓存中的user对象进行比较，
        // 如果两个对象相同，则不会发送update语句，否则，如果两个对象不同，则会发出update语句。
		user.setPassword(userPassword);
		user.setSalt(userSalt);
		// 设置 session 的过期时间 expire方法用于设置过期时间 SSO_SESSION_EXPIRE在配置文件中进行配置
		jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
		// 添加写 cookie 的逻辑，cookie 的有效期是关闭浏览器就失效。
		CookieUtils.setCookie(request, response, "USER_TOKEN", token);
		// 返回token
		return webResult.ok(token);
	}
    //注销删除token
    public void logout(String token) {
    	jedisClient.del(REDIS_USER_SESSION_KEY + ":" + token);
    }

	public webResult queryUserByToken(String token) {
		// 根据token从redis中查询用户信息
		String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token);
		// 判断是否为空
		if (StringUtils.isEmpty(json)) {
			return webResult.build(400, "此session已经过期，请重新登录");
		}
		// 更新过期时间
		jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
		// 返回用户信息
		return webResult.ok(JsonUtils.jsonToPojo(json, User.class));
	}
    
}

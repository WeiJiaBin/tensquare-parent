package com.tenquare.user.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tenquare.user.pojo.User;
import com.tenquare.user.service.UserService;

import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import util.JwtUtil;

/**
 * 控制器层
 * @author Administrator
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * 更新好友粉丝数和用户关注数
	 */
	@RequestMapping(value = "/{userid}/{friendid}/{x}",method= RequestMethod.PUT)
	public void updareFanscountAndFollowcount(@PathVariable String userid,@PathVariable String friendid,@PathVariable int x){
		userService.updareFanscountAndFollowcount(x,userid,friendid);
	}

	@RequestMapping(value = "/login",method= RequestMethod.POST)
	public Result login(@RequestBody User user) {
		User loginUser = userService.login(user.getMobile(),user.getPassword());
		if (loginUser == null) {
			return new Result(false, StatusCode.LOGINERROR, "登陆失败");
		}
		//使得前后端可以通话的操作，采用JWT来实现
		System.out.println("用户id为" + loginUser.getId());
		System.out.println("手机号为:" +loginUser.getMobile());
		String token = jwtUtil.createJWT(loginUser.getId(), loginUser.getMobile(), "user");
		System.out.println("没有加密前的数据"+token);
		Map<String, Object> map = new HashMap<>();
		map.put("token", token);
		map.put("roles", "user");
		return new Result(true,StatusCode.OK,"登陆成功",map);
	}

	@RequestMapping(value = "/register/{code}", method = RequestMethod.POST)
	public Result regist(@PathVariable String code, @RequestBody User user) {
		//获取缓存中的验证码
		String checkcodeRedis = (String) redisTemplate.opsForValue().get("checkcode_" + user.getMobile());
		if (checkcodeRedis.isEmpty()) {
			return new Result(false, StatusCode.ERROR, "请先获取手机验证码");
		}
		if (!checkcodeRedis.equals(code)) {
			return new Result(false, StatusCode.ERROR, "请输入正确的验证码");
		}
		userService.add(user);
		return new Result(true, StatusCode.OK, "注册成功");
	}
	/**
	 * 发送短信验证码
	 */
	@RequestMapping(value = "/sendsms/{mobile}",method= RequestMethod.POST)
	public Result sendSms(@PathVariable String mobile){

		userService.sendSms(mobile);
		return new Result(true, StatusCode.OK, "发送成功");
	}

	/**
	 * 用户注册
	 */

	/**
	 * 查询全部数据
	 * @return
	 */
	@RequestMapping(method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true,StatusCode.OK,"查询成功",userService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 * @return
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.GET)
	public Result findById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",userService.findById(id));
	}


	/**
	 * 分页+多条件查询
	 * @param searchMap 查询条件封装
	 * @param page 页码
	 * @param size 页大小
	 * @return 分页结果
	 */
	@RequestMapping(value="/search/{page}/{size}",method=RequestMethod.POST)
	public Result findSearch(@RequestBody Map searchMap , @PathVariable int page, @PathVariable int size){
		Page<User> pageList = userService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<User>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMap
     * @return
     */
    @RequestMapping(value="/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",userService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param user
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Result add(@RequestBody User user  ){
		userService.add(user);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param user
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.PUT)
	public Result update(@RequestBody User user, @PathVariable String id ){
		user.setId(id);
		userService.update(user);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 验证角色,必须有admin角色才能删除
	 删除
	 * @param id
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String id ){
		userService.deleteById(id);
		return new Result(true,StatusCode.OK,"删除成功");
	}
	
}

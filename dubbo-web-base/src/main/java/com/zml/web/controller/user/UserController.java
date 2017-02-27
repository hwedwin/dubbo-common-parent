package com.zml.web.controller.user;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zml.common.web.base.BaseController;
import com.zml.common.web.entity.Message;
import com.zml.user.entity.User;
import com.zml.user.exceptions.UserServiceException;
import com.zml.user.service.IUserService;

@RestController
public class UserController extends BaseController {
	
	@Autowired
	private IUserService userService;
	
	/**
	 * 用户详情
	 * @param id
	 * @return
	 */
	@RequiresAuthentication
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Message getDetail(@PathVariable("id") long id) {
		Message message = new Message();
		User user = this.userService.getUserById(id);
		message.setData(user);
		return message;
		
	}
	
	/**
	 * 获取所有用户信息
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET)
    public Message listAllUsers() {
		Message message = new Message();
        List<User> users = userService.getAllUser();
        if(users.isEmpty()){
        	message.setStatusCode(HttpStatus.NO_CONTENT);
        }
        message.setMessage("获取列表成功！");
        message.setData(users);
        return message;
    }
	
	/**
	 * 添加用户
	 * @param user
	 * @param ucBuilder
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public Message createUser(@RequestBody User user) {
		Message message = new Message();
		try {
			if(this.userService.isUserExist(user)) {
				message.setMessage("此用户已经存在！");
				message.setStatusCode(HttpStatus.CONFLICT);
			} else {
				this.userService.addUser(user);
				this.logSave("添加用户成功！");
				message.setMessage("添加用户成功！");
			}
		} catch (UserServiceException e) {
			this.logSaveErr("添加用户失败："+e.getErrMsg(), e.getCode());
			message.setMessage("添加用户失败!");
		}
		return message;
	}
	
	/**
	 * 更新用户信息
	 * @param id
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public Message updateUser(@PathVariable("id") long id, @RequestBody User user) {
		Message message = new Message();
        User currentUser = this.userService.getUserById(id);
         
        if (currentUser == null) {
        	message.setMessage("更新用户失败！");
        	message.setStatusCode(HttpStatus.NO_CONTENT);
        } else {
        	//currentUser.setUserName(user.getUserName());
        	currentUser.setStaffNum(user.getStaffNum());
        	currentUser.setPasswd(user.getPasswd());
        	
        	this.userService.updateUser(currentUser);
        	message.setSuc();
        }
 
        return message;
    }
	
	/**
	 * 根据id删除用户
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public Message deleteUser(@PathVariable("id") long id) {
		Message message = new Message();
		
        System.out.println("Fetching & Deleting User with id " + id);
        User user = this.userService.getUserById(id);
        if (user == null) {
        	message.setMessage("未找到相应用户信息！");
        	message.setStatusCode(HttpStatus.NO_CONTENT);
        } else {
        	this.userService.deleteUser(id);
        	message.setSuc();
        }
        return message;
    }
	
}
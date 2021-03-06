package com.zml.user.service;

import java.util.List;

import com.zml.common.page.Page;
import com.zml.common.page.Parameter;
import com.zml.user.entity.User;
import com.zml.user.exceptions.UserServiceException;

/**
 * 用户接口
 * @author zhao
 *
 */
public interface IUserService {

	public Long addUser(User user) throws UserServiceException;
	
	public List<User> getAllUser() throws UserServiceException;
	
	public Page getUserPage(Parameter<User> param) throws UserServiceException;
	
	public User getUserByName(String userName) throws UserServiceException;
	
	public User getUserByStaffId(String staffId) throws UserServiceException;
	
	public User getUserById(Long id) throws UserServiceException;
	
	/**
	 * 通过用户名查询用户是否存在
	 * @param user
	 * @return
	 * @throws UserServiceException
	 */
	public boolean isUserExist(User user) throws UserServiceException;
	
	public void updateUser(User user) throws UserServiceException;
	
	public void deleteUser(Long id) throws UserServiceException;
	
	public void updateUserStatus(Long id, Integer status) throws UserServiceException;
	
	/**
	 * 通过用户id获取权限列表
	 * @param userId
	 * @return
	 * @throws UserServiceException
	 */
	public List<String> getPermissionByUserId(String userId) throws UserServiceException;
	
	/**
	 * 通过用户编制号获取权限列表
	 * @param staffNum
	 * @return
	 * @throws UserServiceException
	 */
	public List<String> getPermissionByStaffNuym(String staffNum) throws UserServiceException;

}

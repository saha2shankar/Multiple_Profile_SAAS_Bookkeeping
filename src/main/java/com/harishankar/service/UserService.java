package com.harishankar.service;

import java.util.List;

import com.harishankar.model.User;

public interface UserService {

	public User saveUser(User user);
	public void removeSessionMessage();
	List<User> getAllUser();
	void deleteUserByid(int id);
	public User updateUser(User user);
	User getUserByid(int id);
	
	


}

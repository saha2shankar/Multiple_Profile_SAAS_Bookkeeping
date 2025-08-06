package com.harishankar.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.harishankar.model.User;
import com.harishankar.repository.UserRepo;

import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	public User saveUser(User user) {

		String password=passwordEncoder.encode(user.getPassword());
		user.setPassword(password);
		User newuser = userRepo.save(user);
		return newuser;
	}

	@Override
	public void removeSessionMessage() {

		HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest()
				.getSession();

		session.removeAttribute("msg");
	}

	@Override
	public List<User> getAllUser() {
		
		return userRepo.findAll();
	}

	@Override
	public void deleteUserByid(int id) {
		userRepo.deleteById(id);
	}

	@Override
	public User updateUser(User user) {
		return userRepo.save(user);
	}

	@Override
	public User getUserByid(int id) {
		return userRepo.findById(id).get();
		
	}

	

}
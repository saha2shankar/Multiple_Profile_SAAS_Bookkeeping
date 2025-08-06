package com.harishankar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harishankar.model.User;

public interface UserRepo extends JpaRepository<User, Integer> {
	public User findByEmail(String emaill);
    long countByRole(String role);

}

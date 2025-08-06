package com.harishankar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harishankar.model.Company;

public interface CompanyRepo extends JpaRepository<Company, Long> {
	

}

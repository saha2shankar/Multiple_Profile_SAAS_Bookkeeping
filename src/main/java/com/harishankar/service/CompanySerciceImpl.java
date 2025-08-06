package com.harishankar.service;


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.harishankar.model.Company;
import com.harishankar.model.User;
import com.harishankar.repository.CompanyRepo;
import com.harishankar.repository.UserRepo;

import jakarta.servlet.http.HttpSession;

@Service
public class CompanySerciceImpl implements CompanyService {

	@Autowired
	private CompanyRepo companyRepo;

	@Override
	public Company saveCompany(Company company) {
		return companyRepo.save(company);
	}

	@Override
	public Company upgateCompnay(Company company) {
		
		return companyRepo.save(company);
	}

	@Override
	public List<Company> getAllCompanies() {
		
		return companyRepo.findAll();
	}

	@Override
	public void deleteCompanyByid(Long id) {
		companyRepo.deleteById(id);
	}

	@Override
	public Company getCompanyById(Long id) {
		return companyRepo.findById(id).orElse(null);
	}
}

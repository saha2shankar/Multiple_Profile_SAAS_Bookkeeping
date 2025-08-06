package com.harishankar.service;

import java.util.List;

import com.harishankar.model.Company;

public interface CompanyService {

	public Company saveCompany(Company company);
	List<Company> getAllCompanies();
	void deleteCompanyByid(Long id);
	public Company upgateCompnay(Company company);
	public Company getCompanyById(Long id);
	
	


}

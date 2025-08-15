package com.harishankar.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.harishankar.model.Company;
import com.harishankar.model.Transaction;
import com.harishankar.model.User;
import com.harishankar.repository.UserRepo;
import com.harishankar.service.TransactionService;
import com.harishankar.service.UserService;

import jakarta.servlet.http.HttpSession;

import com.harishankar.service.CompanyService;

@Controller
@RequestMapping("admin/companies/{companyId}")
public class TransactionController {
	@Autowired
	private TransactionService transactionService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private UserService userService;
	@Autowired
	private UserRepo userRepo;

	@GetMapping("/transactions/list")
	public String listTransactions(@PathVariable Long companyId, Model model) {
		Company company = companyService.getCompanyById(companyId);
		model.addAttribute("transactions", transactionService.getTransactionsByCompany(company));
		model.addAttribute("company", company);
		return "Admin/companys/transactionlist";
	}

	@GetMapping("/transactions/new")
	public String showCreateForm(@PathVariable Long companyId, Model model) {
		Company company = companyService.getCompanyById(companyId);
		Transaction transaction = new Transaction();
		transaction.setCompany(company);

		model.addAttribute("transaction", transaction);
		model.addAttribute("company", company);
		return "Admin/companys/new";
	}

	@PostMapping("/transactions/new")
	public String createTransaction(@PathVariable Long companyId, @ModelAttribute Transaction transaction,
			BindingResult result) {
		if (result.hasErrors()) {
			return "transactions/create";
		}
		// Set the company from the path variable
		Company company = companyService.getCompanyById(companyId);
		transaction.setCompany(company);

		transactionService.saveTransaction(transaction);
		return "redirect:/admin/companies/" + companyId + "/transactions/new";
	}

	@GetMapping("/transactions/delete")
	public String deleteUser(@RequestParam Long id, @PathVariable Long companyId) {
		transactionService.deleteTransaction(id);
		return "redirect:/admin/companies/" + companyId + "/transactions/list";
	}

	// GET: Load edit form
	@GetMapping("/transactions/edit")
	public String editProfile(@PathVariable Long companyId, @RequestParam Long id, Model model) {
		Transaction transaction = transactionService.getTransactionbyId(id);
		Company company = companyService.getCompanyById(companyId);

		model.addAttribute("transactionsModel", transaction);
		model.addAttribute("company", company); // Optional: to send back to form
		return "Admin/companys/edittransaction";
	}

	// POST: Submit edited transaction
	@PostMapping("/transactions/edit")
	public String editTransaction(@PathVariable Long companyId, @ModelAttribute Transaction transaction,
			BindingResult result) {
		if (result.hasErrors()) {
			return "Admin/companys/edittransaction"; // Better than pointing to create
		}

		Company company = companyService.getCompanyById(companyId);
		transaction.setCompany(company);
		transactionService.saveTransaction(transaction);
		return "redirect:/admin/companies/" + companyId + "/transactions/list";

	}
	
	@GetMapping("/company-user")
	public String companyUserForm(@PathVariable Long companyId,Model model) {
		Company company = companyService.getCompanyById(companyId);
		model.addAttribute("company", company);
		return "Admin/companys/adduser";
	}
	
	
	
	@PostMapping("/company-user")
	public String saveCompanyUser(@ModelAttribute User user,@RequestParam("companyId") Long companyId) {

		// System.out.println(user);
		Company company = companyService.getCompanyById(companyId);
		user.setCompany(company);
		userService.saveUser(user);
		return "redirect:/admin/companies/" + companyId + "/company-user-list";

	}
	
	@GetMapping("/company-user-list")
	public String companyViewUsers(HttpSession session, Model model) {
	    Company company = (Company) session.getAttribute("company");

	    if (company == null) {
	        return "redirect:/login"; // or handle as needed
	    }

	    // Filter users whose company ID matches session company ID
	    List<User> users = userService.getAllUser().stream()
	            .filter(u -> u.getCompany() != null && u.getCompany().getId().equals(company.getId()))
	            .collect(Collectors.toList());

	    model.addAttribute("users", users);
	    model.addAttribute("totalUsers", users.size());

	    long adminCount = users.stream().filter(u -> u.getRole().equals("ROLE_ADMIN")).count();
	    long userCount = users.stream().filter(u -> u.getRole().equals("ROLE_USER")).count();

	    model.addAttribute("adminCount", adminCount);
	    model.addAttribute("userCount", userCount);

	    return "Admin/companys/userlist";
	}

	
	
	@GetMapping("/company-edit/user")
	public String companyEditProfile(@RequestParam int id, @PathVariable Long companyId, Model model) {
		List<Company> company = companyService.getAllCompanies();
		model.addAttribute("companys", company);
		User user = userService.getUserByid(id);
		model.addAttribute("uModel", user);
		model.addAttribute("companyId", companyId); // Required for redirect later
		return "Admin/companys/edituser";
	}

	

	@PostMapping("/company-updateUser")
	public String CompanyEditUser(@ModelAttribute User user, @RequestParam int id, @RequestParam Long companyId) {
		User currentUser = userService.getUserByid(id);
		user.setPassword(currentUser.getPassword());
		userService.updateUser(user);
		return "redirect:/admin/companies/" + companyId + "/company-user-list";
	}

	
	
	@GetMapping("/company-delete-user")
	public String CompanyDeleteUser(@RequestParam int id, @PathVariable Long companyId) {
		userService.deleteUserByid(id);
		return "redirect:/admin/companies/" + companyId + "/company-user-list";
	}

	
	



}
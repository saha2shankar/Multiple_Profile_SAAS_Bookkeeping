package com.harishankar.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harishankar.model.Company;
import com.harishankar.model.Transaction;
import com.harishankar.model.User;
import com.harishankar.repository.UserRepo;
import com.harishankar.service.CompanyService;
import com.harishankar.service.TransactionService;
import com.harishankar.service.UserService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/company")
public class UserController {

	@Autowired
	private UserRepo userRepo;
	

	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	
	

	@ModelAttribute
	public void commonUser(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User user = userRepo.findByEmail(email);
			m.addAttribute("user", user);
		}

	}


    @GetMapping("/dashboard/{id}")
    public String showDashboard(@PathVariable Long id, Model model) {
        Company company = companyService.getCompanyById(id);
        
        // Transaction statistics
        model.addAttribute("company", company);
        model.addAttribute("totalAmount", transactionService.getTotalAmountByCompany(company));
        model.addAttribute("todayAmount", transactionService.getTodayAmountByCompany(company));
        model.addAttribute("monthAmount", transactionService.getThisMonthAmountByCompany(company));
        model.addAttribute("totalCount", transactionService.getTotalCountByCompany(company));
        model.addAttribute("todayCount", transactionService.getTodayCountByCompany(company));
        model.addAttribute("monthCount", transactionService.getThisMonthCountByCompany(company));
        model.addAttribute("recentTransactions", transactionService.getRecentTransactions(company));
        

        // Chart data preparation
        List<Object[]> monthlyData = transactionService.getMonthlyTransactionData(company);
        
        // Convert to JSON strings for JavaScript
        ObjectMapper mapper = new ObjectMapper();
        try {
            model.addAttribute("monthlyLabels", 
                mapper.writeValueAsString(monthlyData.stream()
                    .map(arr -> getMonthName((Integer) arr[0]))
                    .collect(Collectors.toList())));
            
            model.addAttribute("monthlyValues", 
                mapper.writeValueAsString(monthlyData.stream()
                    .map(arr -> arr[1])
                    .collect(Collectors.toList())));
        } catch (JsonProcessingException e) {
            // Handle error if needed
            model.addAttribute("monthlyLabels", "[]");
            model.addAttribute("monthlyValues", "[]");
        }
        
        
        // Line chart data - Last 30 days
        Map<LocalDate, Double> dailyData = transactionService.getDailyTransactionsLast30Days(company);
        
        // Convert to format suitable for Chart.js
        List<String> lineChartLabels = dailyData.keySet().stream()
            .map(date -> date.format(DateTimeFormatter.ofPattern("MMM dd")))
            .collect(Collectors.toList());
        
        List<Double> lineChartValues = new ArrayList<>(dailyData.values());
        
        model.addAttribute("lineChartLabels", lineChartLabels);
        model.addAttribute("lineChartValues", lineChartValues);
     // Add this to your controller temporarily for debugging
        System.out.println("Line Chart Labels: " + lineChartLabels);
        System.out.println("Line Chart Values: " + lineChartValues);
        
        return "User/companys/view";
        
        
         }

    private String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                              "July", "August", "September", "October", "November", "December"};
        return monthNames[month - 1];
    }
    
    
	@GetMapping("/{companyId}/transactions/list")
	public String listTransactions(@PathVariable Long companyId, Model model) {
		Company company = companyService.getCompanyById(companyId);
		model.addAttribute("transactions", transactionService.getTransactionsByCompany(company));
		model.addAttribute("company", company);
		return "User/companys/transactionlist";
	}
	
	@GetMapping("/{companyId}/transactions/new")
	public String showCreateForm(@PathVariable Long companyId, Model model) {
		Company company = companyService.getCompanyById(companyId);
		Transaction transaction = new Transaction();
		transaction.setCompany(company);

		model.addAttribute("transaction", transaction);
		model.addAttribute("company", company);
		return "User/companys/new";
	}
	
	@PostMapping("/{companyId}/transactions/new")
	public String createTransaction(@PathVariable Long companyId, @ModelAttribute Transaction transaction,
			BindingResult result) {
		if (result.hasErrors()) {
			return "transactions/create";
		}
		// Set the company from the path variable
		Company company = companyService.getCompanyById(companyId);
		transaction.setCompany(company);

		transactionService.saveTransaction(transaction);
		return "redirect:/company/" + companyId + "/transactions/new";
	}
	
	

	@GetMapping("/{companyId}/transactions/delete")
	public String deleteUser(@RequestParam Long id, @PathVariable Long companyId) {
		transactionService.deleteTransaction(id);
		return "redirect:/company/" + companyId + "/transactions/list";
	}

	// GET: Load edit form
	@GetMapping("/{companyId}/transactions/edit")
	public String editProfile(@PathVariable Long companyId, @RequestParam Long id, Model model) {
		Transaction transaction = transactionService.getTransactionbyId(id);
		Company company = companyService.getCompanyById(companyId);

		model.addAttribute("transactionsModel", transaction);
		model.addAttribute("company", company); // Optional: to send back to form
		return "User/companys/edittransaction";
	}

	// POST: Submit edited transaction
	@PostMapping("/{companyId}/transactions/edit")
	public String editTransaction(@PathVariable Long companyId, @ModelAttribute Transaction transaction,
			BindingResult result) {
		if (result.hasErrors()) {
			return "User/companys/edittransaction"; // Better than pointing to create
		}

		Company company = companyService.getCompanyById(companyId);
		transaction.setCompany(company);
		transactionService.saveTransaction(transaction);
		return "redirect:/company/" + companyId + "/transactions/list";

	}
	
	
	@GetMapping("/{companyId}/change-password")
	public String changepasswordform(@PathVariable Long companyId, Model model) {
	    Company company = companyService.getCompanyById(companyId);
	    model.addAttribute("company", company);
	    return "User/companys/passwordchange";
	}

	

	@PostMapping("/{companyId}/change-password")
	public String changePassword(@PathVariable Long companyId,
	                             @RequestParam("currentPassword") String currentPassword,
	                             @RequestParam("newPassword") String newPassword,
	                             @RequestParam("confirmPassword") String confirmPassword,
	                             Principal principal,
	                             Model model) {

	    Company company = companyService.getCompanyById(companyId);
	    model.addAttribute("company", company);

	    User user = userRepo.findByEmail(principal.getName());

	    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
	        model.addAttribute("error", "Current password is incorrect.");
	        return "redirect:/company/" + companyId + "/change-password";
	    }

	    if (!newPassword.equals(confirmPassword)) {
	        model.addAttribute("error", "New passwords do not match.");
	        return "redirect:/company/" + companyId + "/change-password";
	    }

	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepo.save(user);

	    model.addAttribute("success", "Password changed successfully.");
	    return "redirect:/logout";
	}


	@GetMapping("/{companyId}/company-user")
	public String saveCompanyUserForm(@PathVariable Long companyId, Model model) {
	    Company company = companyService.getCompanyById(companyId);
	    model.addAttribute("company", company);
	    model.addAttribute("newUser", new User()); // different name
	    return "User/companys/adduser";
	}

	@PostMapping("/{companyId}/company-user")
	public String saveCompanyUser(@ModelAttribute("newUser") User user, @PathVariable Long companyId) {
	    Company company = companyService.getCompanyById(companyId);
	    user.setCompany(company);
	    userService.saveUser(user);
	    return "redirect:/company/" + companyId + "/company-user-list";
	}

	
	@GetMapping("/{companyId}/company-user-list")
	public String companyViewUsers(@PathVariable Long companyId, Model model) {
	    Company company = companyService.getCompanyById(companyId);

	    List<User> users = userService.getAllUser().stream()
	            .filter(u -> u.getCompany() != null && u.getCompany().getId().equals(company.getId()))
	            .collect(Collectors.toList());

	    model.addAttribute("users", users);
	    model.addAttribute("totalUsers", users.size());
	    model.addAttribute("adminCount", users.stream().filter(u -> "ROLE_ADMIN".equals(u.getRole())).count());
	    model.addAttribute("userCount", users.stream().filter(u -> "ROLE_USER".equals(u.getRole())).count());
	    model.addAttribute("company", company);

	    return "User/companys/userlist";
	}
	

@GetMapping("/{companyId}/company-edit/user")
public String companyEditProfile(@RequestParam int id, @PathVariable Long companyId, Model model) {
    List<Company> companies = companyService.getAllCompanies();
    Company companyById = companyService.getCompanyById(companyId);
    model.addAttribute("companys", companies);
    model.addAttribute("uModel", userService.getUserByid(id));
    model.addAttribute("company", companyById);
    return "User/companys/edituser";
}

@PostMapping("/{companyId}/company-updateUser")
public String CompanyEditUser(@ModelAttribute User user, @RequestParam int id, @PathVariable Long companyId) {
    User currentUser = userService.getUserByid(id);
    user.setPassword(currentUser.getPassword()); // Keep old password
    userService.updateUser(user);
    return "redirect:/company/" + companyId + "/company-user-list";
}

@GetMapping("/{companyId}/company-delete-user")
public String CompanyDeleteUser(@RequestParam int id, @PathVariable Long companyId) {
    userService.deleteUserByid(id);
    return "redirect:/company/" + companyId + "/company-user-list";
}
	

	
	
	
	
	
	
	
	

	

    
    
    
    
    
    

}
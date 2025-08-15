package com.harishankar.controller;


import java.security.Principal;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.harishankar.model.Company;
import com.harishankar.model.User;
import com.harishankar.repository.UserRepo;
import com.harishankar.service.CompanyService;
import com.harishankar.service.TransactionService;
import com.harishankar.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;


@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
private UserService userService;
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private TransactionService transactionService;
	
//	@Autowired
//	private UserRepo userRepo;

//	@ModelAttribute
//	public void commonUser(Principal p, Model m) {
//		if (p != null) {
//			String email = p.getName();
//			User user = userRepo.findByEmail(email);
//			m.addAttribute("user", user);
//		}
//	}

	@GetMapping("/profile")
	public String profile(HttpSession session, Model model) {
	    long mainAdminCount = userRepo.countByRole("ROLE_MAIN_ADMIN");
	    long company =companyService.getAllCompanies().size();
	    Double totalIncome = transactionService.getTotalAmount();
	    
	    model.addAttribute("mainAdmin", mainAdminCount);
	    model.addAttribute("companyList", company);
	    model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("recentTransactions", transactionService.getRecentTransaction());
		return "Admin/dashboard";
	}
	@GetMapping("/add-new-user")
	public String addNewUsers(Model model) {
		List<Company> company = companyService.getAllCompanies();
		model.addAttribute("companys", company);
		return "Admin/adduser";
	}
	@PostMapping("/add-new-user")
	public String saveUser(@ModelAttribute User user,@RequestParam("companyId") Long companyId,  RedirectAttributes redirectAttributes) {
		// System.out.println(user);
		Company company = companyService.getCompanyById(companyId);
		user.setCompany(company);
		userService.saveUser(user);
		redirectAttributes.addFlashAttribute("successMessage", "New User Added successfully!");
		return "redirect:/admin/view-user-list";
	}

	
	@GetMapping("/view-user-list")
	public String viewUsers(Model model) {
		
		 List<User> users = userService.getAllUser();
		model.addAttribute("users",users );
		
		long totalUsers = users.size();
	    long adminCount = userRepo.countByRole("ROLE_ADMIN");
	    long userCount = userRepo.countByRole("ROLE_USER");

	    model.addAttribute("totalUsers", totalUsers);
	    model.addAttribute("adminCount", adminCount);
	    model.addAttribute("userCount", userCount);
		return "Admin/userlist";
	}
	
	
	@GetMapping("/edit/user")
	public String editProfile(@RequestParam int id, Model model) {
		List<Company> company = companyService.getAllCompanies();
		model.addAttribute("companys", company);
		User user =userService.getUserByid(id);
		model.addAttribute("uModel", user);
		return "Admin/edituser";
	}
	

	@PostMapping("/updateUser")
	public String editUser(@ModelAttribute User user, @RequestParam int id) {

		System.out.println("this is userid:" + user.getId());
		System.out.println("this is role:" + user.getRole());
		System.out.println("this is email:" + user.getEmail());
		System.out.println("this is mobile:" + user.getMobileNo());
		System.out.println("this is name:" + user.getName());
		System.out.println("this is password:" + user.getPassword());
		
		User currentUser =userService.getUserByid(id);
		user.setPassword(currentUser.getPassword());
		userService.updateUser(user);
		return "redirect:/admin/view-user-list";
	}
	
	
	@GetMapping("/delete")
	public String deleteUser(@RequestParam int id) {
		userService.deleteUserByid(id);	
		return "redirect:/admin/view-user-list";
	}
	
	
	@GetMapping("/change-password")
	public String changepasswordform() {
		return "Admin/passwordchange";
	}
	


    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Authentication authentication,
                                 Model model) {

        String email = authentication.getName(); // Logged in user
        User user = userRepo.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "redirect:/admin/change-password";
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "Current password is incorrect.");
            return "redirect:/admin/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            return "redirect:admin/change-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        model.addAttribute("success", "Password changed successfully.");
		return "Admin/passwordchange";
	}
	
	
	

	
	
}
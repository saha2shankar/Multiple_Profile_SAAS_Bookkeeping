package com.harishankar.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harishankar.model.Company;
import com.harishankar.model.User;
import com.harishankar.repository.UserRepo;
import com.harishankar.service.UserService;
import com.harishankar.service.CompanyService;
import com.harishankar.service.TransactionService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/company")
public class CompanyController {

	@Autowired
	private UserService userService;

	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private TransactionService transactionService;

	@PostMapping("/new")
	public String saveCompany(@ModelAttribute Company company, HttpSession session, Model m) {
		Company savecompany = companyService.saveCompany(company);
		if (savecompany != null) {
			session.setAttribute("msg", "Company Create successfully");
		} else {
			session.setAttribute("msg", "Something wrong server");
		}
		return "redirect:/admin/company/list";
	}

	@GetMapping("/list")
	public String viewCompany(Model model) {
		List<Company> company = companyService.getAllCompanies();
		model.addAttribute("companys", company);
		return "Admin/company";
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
        
        return "Admin/companys/view";
        
        
         }

    private String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                              "July", "August", "September", "October", "November", "December"};
        return monthNames[month - 1];
    }
}
package com.harishankar.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.harishankar.model.Company;
import com.harishankar.model.Transaction;
import com.harishankar.model.User;
import com.harishankar.service.TransactionService;
import com.harishankar.service.CompanyService;

@Controller
@RequestMapping("admin/companies/{companyId}/transactions")
public class TransactionController {
	@Autowired
	private TransactionService transactionService;

	@Autowired
	private CompanyService companyService;

	@GetMapping("/list")
	public String listTransactions(@PathVariable Long companyId, Model model) {
		Company company = companyService.getCompanyById(companyId);
		model.addAttribute("transactions", transactionService.getTransactionsByCompany(company));
		model.addAttribute("company", company);
		return "Admin/companys/transactionlist";
	}

	@GetMapping("/new")
	public String showCreateForm(@PathVariable Long companyId, Model model) {
		Company company = companyService.getCompanyById(companyId);
		Transaction transaction = new Transaction();
		transaction.setCompany(company);

		model.addAttribute("transaction", transaction);
		model.addAttribute("company", company);
		return "Admin/companys/new";
	}

	@PostMapping("/new")
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

	@GetMapping("/delete")
	public String deleteUser(@RequestParam Long id, @PathVariable Long companyId) {
		transactionService.deleteTransaction(id);
		return "redirect:/admin/companies/" + companyId + "/transactions/list";
	}

	// GET: Load edit form
	@GetMapping("/edit")
	public String editProfile(@PathVariable Long companyId, @RequestParam Long id, Model model) {
		Transaction transaction = transactionService.getTransactionbyId(id);
		Company company = companyService.getCompanyById(companyId);

		model.addAttribute("transactionsModel", transaction);
		model.addAttribute("company", company); // Optional: to send back to form
		return "Admin/companys/edittransaction";
	}

	// POST: Submit edited transaction
	@PostMapping("/edit")
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

}
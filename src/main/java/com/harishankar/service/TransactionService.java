package com.harishankar.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.harishankar.model.Company;
import com.harishankar.model.Transaction;

public interface TransactionService {

	public Transaction saveTransaction(Transaction transaction);
	
	public void deleteTransaction(Long id);
	
	public Transaction getTransactionbyId(Long id);
	
	public Transaction updateTransaction(Transaction transaction);


	public List<Transaction> getTransactionsByCompany(Company company);

	public Double getTotalAmountByCompany(Company company);
    public Double getTodayAmountByCompany(Company company);

	public Double getThisMonthAmountByCompany(Company company);

	public Long getTotalCountByCompany(Company company);

	public Long getTodayCountByCompany(Company company);

	public Long getThisMonthCountByCompany(Company company);

	public List<Transaction> getRecentTransactions(Company company);

	public List<Object[]> getMonthlyTransactionData(Company company);

	public Map<LocalDate, Double> getDailyTransactionsLast30Days(Company company);

}

package com.harishankar.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.harishankar.model.Company;
import com.harishankar.model.Transaction;
import com.harishankar.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

	@Override
	public Transaction saveTransaction(Transaction transaction) {
		 return transactionRepository.save(transaction);
	}

	@Override
	public List<Transaction> getTransactionsByCompany(Company company) {
		  return transactionRepository.findByCompany(company);
	}

	@Override
	public Double getTotalAmountByCompany(Company company) {
        Double amount = transactionRepository.getTotalAmountByCompany(company);
        return amount != null ? amount : 0.0;
	}

	@Override
	public Double getThisMonthAmountByCompany(Company company) {
	      Double amount = transactionRepository.getThisMonthAmountByCompany(company);
	        return amount != null ? amount : 0.0;    
	}

	@Override
	public Long getTotalCountByCompany(Company company) { 
        return transactionRepository.countByCompany(company);

	}

	@Override
	public Long getTodayCountByCompany(Company company) {
        return transactionRepository.countTodayByCompany(company);

	}

	@Override
	public Long getThisMonthCountByCompany(Company company) {
        return transactionRepository.countThisMonthByCompany(company);

	}

	@Override
	public List<Transaction> getRecentTransactions(Company company) {
		 Pageable topFive = PageRequest.of(0, 5);
	        return transactionRepository.findRecentTransactions(company, topFive);
	  
	}

	@Override
	public List<Object[]> getMonthlyTransactionData(Company company) {
        return transactionRepository.findMonthlyTransactionData(company);

	}

	@Override
	public Map<LocalDate, Double> getDailyTransactionsLast30Days(Company company) {
		 LocalDate endDate = LocalDate.now();
	        LocalDate startDate = endDate.minusDays(30);
	        
	        List<Object[]> results = transactionRepository.findDailyTransactionDataBetweenDates(
	            company, startDate, endDate);
	        
	        Map<LocalDate, Double> dailyData = new LinkedHashMap<>();
	        
	        // Initialize all 30 days with 0.0
	        for (int i = 0; i <= 30; i++) {
	            dailyData.put(endDate.minusDays(30 - i), 0.0);
	        }
	        
	        // Fill with actual data
	        results.forEach(data -> {
	            LocalDate date = (LocalDate) data[0];
	            Double amount = (Double) data[1];
	            dailyData.put(date, amount);
	        });
	        
	        return dailyData;
	}

	@Override
	public void deleteTransaction(Long id) {
		transactionRepository.deleteById(id);
		
	}

	@Override
	public Transaction updateTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);
	}

	@Override
	public Transaction getTransactionbyId(Long id) {
		return transactionRepository.findById(id).get();
	}

	@Override
	public Double getTodayAmountByCompany(Company company) {
		   Double amount = transactionRepository.getTodayAmountByCompany(company);
	        return amount != null ? amount : 0.0;
	   
	}

}

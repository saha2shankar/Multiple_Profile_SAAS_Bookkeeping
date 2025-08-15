package com.harishankar.repository;

import com.harishankar.model.Company;


import com.harishankar.model.Transaction;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCompany(Company company);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.company = :company")
    Double getTotalAmountByCompany(@Param("company") Company company);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.company = :company AND t.date = CURRENT_DATE")
    Double getTodayAmountByCompany(@Param("company") Company company);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.company = :company AND YEAR(t.date) = YEAR(CURRENT_DATE) AND MONTH(t.date) = MONTH(CURRENT_DATE)")
    Double getThisMonthAmountByCompany(@Param("company") Company company);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.company = :company")
    Long countByCompany(@Param("company") Company company);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.company = :company AND t.date = CURRENT_DATE")
    Long countTodayByCompany(@Param("company") Company company);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.company = :company AND YEAR(t.date) = YEAR(CURRENT_DATE) AND MONTH(t.date) = MONTH(CURRENT_DATE)")
    Long countThisMonthByCompany(@Param("company") Company company);
    @Query("SELECT t FROM Transaction t WHERE t.company = :company ORDER BY t.date DESC, t.id DESC")
    List<Transaction> findRecentTransactions(@Param("company") Company company, Pageable pageable);

    @Query("SELECT FUNCTION('MONTH', t.date) as month, SUM(t.amount) as amount " +
           "FROM Transaction t WHERE t.company = :company AND YEAR(t.date) = YEAR(CURRENT_DATE) " +
           "GROUP BY FUNCTION('MONTH', t.date) " +
           "ORDER BY month")
    List<Object[]> findMonthlyTransactionData(@Param("company") Company company);
    
 // In TransactionRepository.java
    @Query("SELECT t.date, SUM(t.amount) " +
           "FROM Transaction t " +
           "WHERE t.company = :company " +
           "AND t.date BETWEEN :startDate AND :endDate " +
           "GROUP BY t.date " +
           "ORDER BY t.date")
    List<Object[]> findDailyTransactionDataBetweenDates(
        @Param("company") Company company,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Sum of all amounts
    @Query("SELECT SUM(t.amount) FROM Transaction t")
    Double getTotalAmount();
    
    @Query("SELECT t FROM Transaction t ORDER BY t.date DESC, t.id DESC")
    List<Transaction> findRecentTransaction(Pageable pageable);



}
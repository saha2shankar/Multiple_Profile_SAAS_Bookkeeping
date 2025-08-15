package com.harishankar.model;

import jakarta.persistence.*;

import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String description;
    private String paymentType;
    private double amount;
    private LocalDate date;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}
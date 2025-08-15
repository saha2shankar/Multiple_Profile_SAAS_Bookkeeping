package com.harishankar.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    
    @OneToMany(mappedBy = "company", fetch = FetchType.EAGER)
    private List<Transaction> transactions;
    
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<User> users;
}

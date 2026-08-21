package com.example.credit_appsec_demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CreditApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String applicantName;
    private Double requestedAmount;
    private String notes;
}
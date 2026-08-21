package com.example.credit_appsec_demo.repo;

import com.example.credit_appsec_demo.model.CreditApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditApplicationRepository extends JpaRepository<CreditApplication, Long> {
}
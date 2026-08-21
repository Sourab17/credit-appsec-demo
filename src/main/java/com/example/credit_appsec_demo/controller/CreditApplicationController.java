package com.example.credit_appsec_demo.controller;

import com.example.credit_appsec_demo.model.CreditApplication;
import com.example.credit_appsec_demo.repo.CreditApplicationRepository;
import com.example.credit_appsec_demo.repo.CreditSearchDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/applications")
public class CreditApplicationController {

    private static final Logger log = LoggerFactory.getLogger(CreditApplicationController.class);

    @Autowired
    private CreditApplicationRepository repository;

    @Autowired
    private CreditSearchDao searchDao;

    @PostMapping
    public CreditApplication create(@RequestBody CreditApplication application) {
        return repository.save(application);
    }

    // VULNERABILITY #2: reflected XSS. applicantName inserted into HTML unescaped.
    @GetMapping(value = "/{id}", produces = "text/html")
    public ResponseEntity<String> getOne(@PathVariable Long id) {
        Optional<CreditApplication> app = repository.findById(id);
        if (app.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String html = "<html><body><h1>Application for " + app.get().getApplicantName() + "</h1></body></html>";
        return ResponseEntity.ok(html);
    }

    // Uses the SQL-injectable DAO on purpose (VULNERABILITY #1 lives in CreditSearchDao)
    @GetMapping("/search")
    public List<CreditApplication> search(@RequestParam String q) {
        return searchDao.search(q);
    }
}
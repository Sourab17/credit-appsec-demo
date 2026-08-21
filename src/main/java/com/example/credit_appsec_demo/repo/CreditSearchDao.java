package com.example.credit_appsec_demo.repo;

import com.example.credit_appsec_demo.model.CreditApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CreditSearchDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // VULNERABILITY #1: raw string concatenation into SQL = SQL injection
    public List<CreditApplication> search(String name) {
        String sql = "SELECT * FROM credit_application WHERE applicant_name LIKE '%" + name + "%'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CreditApplication c = new CreditApplication();
            c.setId(rs.getLong("id"));
            c.setApplicantName(rs.getString("applicant_name"));
            c.setRequestedAmount(rs.getDouble("requested_amount"));
            c.setNotes(rs.getString("notes"));
            return c;
        });
    }
}
package com.example.claims.orchestration.service;

import com.example.claims.orchestration.dto.ClaimantPolicyResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ClaimantPolicyService {

    private final JdbcTemplate jdbcTemplate;

    public ClaimantPolicyService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ClaimantPolicyResponse> getPolicies(String claimantEmail) {
        return jdbcTemplate.query("""
                SELECT p.id, p.policy_number, p.claim_type, p.product_name, p.market,
                       p.currency, p.insured_item, p.coverage_amount, p.effective_from,
                       p.effective_to, p.is_active, a.first_name, a.last_name, a.email, a.phone
                FROM claimant_policies p
                JOIN claimant_accounts a ON a.id = p.claimant_account_id
                WHERE LOWER(a.email) = LOWER(?) AND a.is_active = TRUE
                ORDER BY p.effective_to DESC, p.policy_number
                """, (rs, rowNum) -> new ClaimantPolicyResponse(
                rs.getObject("id", UUID.class),
                rs.getString("policy_number"),
                rs.getString("claim_type"),
                rs.getString("product_name"),
                rs.getString("market"),
                rs.getString("currency"),
                rs.getString("insured_item"),
                rs.getBigDecimal("coverage_amount"),
                date(rs.getDate("effective_from")),
                date(rs.getDate("effective_to")),
                rs.getBoolean("is_active"),
                rs.getString("first_name") + " " + rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone")), claimantEmail);
    }

    private java.time.LocalDate date(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}

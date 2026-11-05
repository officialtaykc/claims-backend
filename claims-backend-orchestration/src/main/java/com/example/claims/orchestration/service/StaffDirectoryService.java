package com.example.claims.orchestration.service;

import com.example.claims.orchestration.dto.StaffOfficerResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffDirectoryService {

    private final JdbcTemplate jdbcTemplate;

    public StaffDirectoryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StaffOfficerResponse> getActiveOfficers() {
        return jdbcTemplate.query("""
                SELECT username, first_name, last_name, email
                FROM claims_staff_accounts
                WHERE UPPER(role) = 'OFFICER' AND is_active = TRUE
                ORDER BY first_name, last_name, username
                """, (resultSet, rowNumber) -> new StaffOfficerResponse(
                resultSet.getString("username"),
                resultSet.getString("first_name") + " " + resultSet.getString("last_name"),
                resultSet.getString("email")));
    }
}

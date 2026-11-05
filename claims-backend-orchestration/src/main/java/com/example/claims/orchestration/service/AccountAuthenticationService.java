package com.example.claims.orchestration.service;

import com.example.claims.orchestration.dto.LoginRequest;
import com.example.claims.orchestration.dto.LoginResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AccountAuthenticationService {

    private static final String INVALID_CREDENTIALS = "Invalid username/email or password";
    private final JdbcTemplate jdbcTemplate;
    private final PasswordHashService passwordHashService;

    public AccountAuthenticationService(JdbcTemplate jdbcTemplate, PasswordHashService passwordHashService) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordHashService = passwordHashService;
    }

    public LoginResponse login(LoginRequest request) {
        String role = request.getRole().toUpperCase(Locale.ROOT);
        String identifier = request.getIdentifier().trim();

        if ("CLAIMANT".equals(role)) {
            return loginClaimant(identifier, request.getPassword());
        }
        return loginStaff(identifier, request.getPassword(), role);
    }

    private LoginResponse loginClaimant(String email, String password) {
        List<Map<String, Object>> accounts = jdbcTemplate.queryForList("""
                SELECT id, first_name, last_name, email, password_hash
                FROM claimant_accounts
                WHERE LOWER(email) = LOWER(?) AND is_active = TRUE
                """, email);
        if (accounts.isEmpty() || !passwordHashService.matches(password, value(accounts, 0, "password_hash"))) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }
        Map<String, Object> account = accounts.get(0);
        jdbcTemplate.update("UPDATE claimant_accounts SET last_login_at = CURRENT_TIMESTAMP WHERE id = ?",
                account.get("id"));
        return new LoginResponse(fullName(account), value(account, "email"), "claimant");
    }

    private LoginResponse loginStaff(String identifier, String password, String requestedRole) {
        List<Map<String, Object>> accounts = jdbcTemplate.queryForList("""
                SELECT id, first_name, last_name, email, password_hash, role
                FROM claims_staff_accounts
                WHERE (LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?))
                  AND UPPER(role) = ? AND is_active = TRUE
                """, identifier, identifier, requestedRole);
        if (accounts.isEmpty() || !passwordHashService.matches(password, value(accounts, 0, "password_hash"))) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }
        Map<String, Object> account = accounts.get(0);
        jdbcTemplate.update("UPDATE claims_staff_accounts SET last_login_at = CURRENT_TIMESTAMP WHERE id = ?",
                account.get("id"));
        return new LoginResponse(fullName(account), value(account, "email"),
                "MANAGER".equals(requestedRole) ? "manager" : "officer");
    }

    private String fullName(Map<String, Object> account) {
        return value(account, "first_name") + " " + value(account, "last_name");
    }

    private String value(List<Map<String, Object>> accounts, int index, String key) {
        return value(accounts.get(index), key);
    }

    private String value(Map<String, Object> account, String key) {
        return String.valueOf(account.get(key));
    }
}

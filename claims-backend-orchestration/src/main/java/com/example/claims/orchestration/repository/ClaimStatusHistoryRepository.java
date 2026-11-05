package com.example.claims.orchestration.repository;

import com.example.claims.orchestration.entity.ClaimStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClaimStatusHistoryRepository extends JpaRepository<ClaimStatusHistory, UUID> {

    List<ClaimStatusHistory> findByClaimIdOrderByChangedAtAsc(UUID claimId);
}

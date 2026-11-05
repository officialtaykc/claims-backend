package com.example.claims.orchestration.repository;

import com.example.claims.common.enums.ClaimStatus;
import com.example.claims.common.enums.ClaimType;
import com.example.claims.orchestration.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {

    Optional<Claim> findByClaimReference(String claimReference);

    boolean existsByEventId(UUID eventId);

    List<Claim> findByClaimantEmailOrderByCreatedAtDesc(String claimantEmail);

    @Query("SELECT c FROM Claim c WHERE "
            + "(:status IS NULL OR c.status = :status) AND "
            + "(:claimType IS NULL OR c.claimType = :claimType) AND "
            + "(:market IS NULL OR c.market = :market) AND "
            + "(:assignedTo IS NULL OR c.assignedTo = :assignedTo) "
            + "ORDER BY c.createdAt DESC")
    List<Claim> searchQueue(@Param("status") ClaimStatus status,
                            @Param("claimType") ClaimType claimType,
                            @Param("market") String market,
                            @Param("assignedTo") String assignedTo);

    @Query("SELECT c.assignedTo, COUNT(c) FROM Claim c WHERE c.assignedTo IS NOT NULL GROUP BY c.assignedTo")
    List<Object[]> countByAssignedTo();

    @Query("SELECT COALESCE(SUM(c.estimatedLoss), 0) FROM Claim c WHERE c.status NOT IN "
            + "(com.example.claims.common.enums.ClaimStatus.REJECTED, "
            + "com.example.claims.common.enums.ClaimStatus.SETTLED)")
    BigDecimal sumEstimatedLossOutstanding();

    @Query("SELECT COALESCE(SUM(c.decisionAmount), 0) FROM Claim c WHERE c.status IN "
            + "(com.example.claims.common.enums.ClaimStatus.APPROVED, "
            + "com.example.claims.common.enums.ClaimStatus.PARTIALLY_APPROVED, "
            + "com.example.claims.common.enums.ClaimStatus.SETTLEMENT_PENDING, "
            + "com.example.claims.common.enums.ClaimStatus.SETTLED)")
    BigDecimal sumApprovedExposure();
}

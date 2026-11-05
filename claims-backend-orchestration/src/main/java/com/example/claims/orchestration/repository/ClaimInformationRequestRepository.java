package com.example.claims.orchestration.repository;

import com.example.claims.orchestration.entity.ClaimInformationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClaimInformationRequestRepository extends JpaRepository<ClaimInformationRequest, UUID> {

    List<ClaimInformationRequest> findByClaimIdOrderByRequestedAtAsc(UUID claimId);

    Optional<ClaimInformationRequest> findByIdAndClaimId(UUID id, UUID claimId);
}

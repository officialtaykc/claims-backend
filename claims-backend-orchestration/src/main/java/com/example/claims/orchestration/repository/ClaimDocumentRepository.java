package com.example.claims.orchestration.repository;

import com.example.claims.orchestration.entity.ClaimDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, UUID> {

    List<ClaimDocument> findByClaimId(UUID claimId);

    java.util.Optional<ClaimDocument> findByIdAndClaimId(UUID id, UUID claimId);
}

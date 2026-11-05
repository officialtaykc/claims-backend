package com.example.claims.orchestration.repository;

import com.example.claims.orchestration.entity.ClaimAttachmentContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClaimAttachmentContentRepository
        extends JpaRepository<ClaimAttachmentContent, UUID> {
}

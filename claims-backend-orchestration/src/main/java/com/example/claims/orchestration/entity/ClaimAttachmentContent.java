package com.example.claims.orchestration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "claim_attachment_contents")
public class ClaimAttachmentContent {

    @Id
    @Column(name = "attachment_id")
    private UUID attachmentId;

    @Column(name = "content", nullable = false, columnDefinition = "BYTEA")
    private byte[] content;

    public UUID getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(UUID attachmentId) {
        this.attachmentId = attachmentId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}

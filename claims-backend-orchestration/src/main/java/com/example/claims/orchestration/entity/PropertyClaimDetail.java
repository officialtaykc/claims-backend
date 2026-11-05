package com.example.claims.orchestration.entity;

import com.example.claims.common.dto.PropertyClaimDetails;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "property_claim_details")
public class PropertyClaimDetail {

    @Id
    @Column(name = "claim_id")
    private UUID claimId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "claim_id")
    private Claim claim;

    @Column(name = "property_type", length = 50)
    private String propertyType;
    @Column(name = "property_address", columnDefinition = "TEXT")
    private String propertyAddress;
    @Column(name = "affected_areas", columnDefinition = "TEXT")
    private String affectedAreas;
    @Column(name = "damage_to_building")
    private Boolean damageToBuilding;
    @Column(name = "damage_to_contents")
    private Boolean damageToContents;
    @Column(name = "cause_of_loss", length = 100)
    private String causeOfLoss;
    @Column(name = "estimated_repair_cost", precision = 15, scale = 2)
    private BigDecimal estimatedRepairCost;
    @Column(name = "estimated_contents_loss", precision = 15, scale = 2)
    private BigDecimal estimatedContentsLoss;

    public static PropertyClaimDetail from(PropertyClaimDetails details) {
        PropertyClaimDetail entity = new PropertyClaimDetail();
        entity.propertyType = details.getPropertyType();
        entity.propertyAddress = details.getPropertyAddress();
        entity.affectedAreas = details.getAffectedAreas();
        entity.damageToBuilding = details.getDamageToBuilding();
        entity.damageToContents = details.getDamageToContents();
        entity.causeOfLoss = details.getCauseOfLoss();
        entity.estimatedRepairCost = details.getEstimatedRepairCost();
        entity.estimatedContentsLoss = details.getEstimatedContentsLoss();
        return entity;
    }

    public void setClaim(Claim claim) { this.claim = claim; }
    public String getPropertyType() { return propertyType; }
    public String getPropertyAddress() { return propertyAddress; }
    public String getAffectedAreas() { return affectedAreas; }
    public Boolean getDamageToBuilding() { return damageToBuilding; }
    public Boolean getDamageToContents() { return damageToContents; }
    public String getCauseOfLoss() { return causeOfLoss; }
    public BigDecimal getEstimatedRepairCost() { return estimatedRepairCost; }
    public BigDecimal getEstimatedContentsLoss() { return estimatedContentsLoss; }
}

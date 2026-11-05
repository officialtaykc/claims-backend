package com.example.claims.common.dto;

import java.math.BigDecimal;

public class PropertyClaimDetails {

    private String propertyType;
    private String propertyAddress;
    private String affectedAreas;
    private Boolean damageToBuilding;
    private Boolean damageToContents;
    private String causeOfLoss;
    private BigDecimal estimatedRepairCost;
    private BigDecimal estimatedContentsLoss;

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }
    public String getAffectedAreas() { return affectedAreas; }
    public void setAffectedAreas(String affectedAreas) { this.affectedAreas = affectedAreas; }
    public Boolean getDamageToBuilding() { return damageToBuilding; }
    public void setDamageToBuilding(Boolean damageToBuilding) { this.damageToBuilding = damageToBuilding; }
    public Boolean getDamageToContents() { return damageToContents; }
    public void setDamageToContents(Boolean damageToContents) { this.damageToContents = damageToContents; }
    public String getCauseOfLoss() { return causeOfLoss; }
    public void setCauseOfLoss(String causeOfLoss) { this.causeOfLoss = causeOfLoss; }
    public BigDecimal getEstimatedRepairCost() { return estimatedRepairCost; }
    public void setEstimatedRepairCost(BigDecimal estimatedRepairCost) { this.estimatedRepairCost = estimatedRepairCost; }
    public BigDecimal getEstimatedContentsLoss() { return estimatedContentsLoss; }
    public void setEstimatedContentsLoss(BigDecimal estimatedContentsLoss) { this.estimatedContentsLoss = estimatedContentsLoss; }
}

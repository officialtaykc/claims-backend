package com.example.claims.orchestration.entity;

import com.example.claims.common.dto.MotorClaimDetails;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "motor_claim_details")
public class MotorClaimDetail {

    @Id
    @Column(name = "claim_id")
    private UUID claimId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "claim_id")
    private Claim claim;

    @Column(name = "vehicle_registration", length = 50)
    private String vehicleRegistration;
    @Column(name = "vehicle_model")
    private String vehicleModel;
    @Column(name = "driver_name")
    private String driverName;
    @Column(name = "driver_licence_number", length = 100)
    private String driverLicenceNumber;
    @Column(name = "third_party_involved")
    private Boolean thirdPartyInvolved;
    @Column(name = "police_report_number", length = 100)
    private String policeReportNumber;
    @Column(name = "repair_workshop")
    private String repairWorkshop;
    @Column(name = "claim_category", length = 100)
    private String claimCategory;

    public static MotorClaimDetail from(MotorClaimDetails details) {
        MotorClaimDetail entity = new MotorClaimDetail();
        entity.vehicleRegistration = details.getVehicleRegistration();
        entity.vehicleModel = details.getVehicleModel();
        entity.driverName = details.getDriverName();
        entity.driverLicenceNumber = details.getDriverLicenceNumber();
        entity.thirdPartyInvolved = details.getThirdPartyInvolved();
        entity.policeReportNumber = details.getPoliceReportNumber();
        entity.repairWorkshop = details.getRepairWorkshop();
        entity.claimCategory = details.getClaimCategory();
        return entity;
    }

    public void setClaim(Claim claim) { this.claim = claim; }
    public String getVehicleRegistration() { return vehicleRegistration; }
    public String getVehicleModel() { return vehicleModel; }
    public String getDriverName() { return driverName; }
    public String getDriverLicenceNumber() { return driverLicenceNumber; }
    public Boolean getThirdPartyInvolved() { return thirdPartyInvolved; }
    public String getPoliceReportNumber() { return policeReportNumber; }
    public String getRepairWorkshop() { return repairWorkshop; }
    public String getClaimCategory() { return claimCategory; }
}

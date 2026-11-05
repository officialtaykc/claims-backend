package com.example.claims.common.dto;

public class MotorClaimDetails {

    private String vehicleRegistration;
    private String vehicleModel;
    private String driverName;
    private String driverLicenceNumber;
    private Boolean thirdPartyInvolved;
    private String policeReportNumber;
    private String repairWorkshop;
    private String claimCategory;

    public String getVehicleRegistration() { return vehicleRegistration; }
    public void setVehicleRegistration(String vehicleRegistration) { this.vehicleRegistration = vehicleRegistration; }
    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getDriverLicenceNumber() { return driverLicenceNumber; }
    public void setDriverLicenceNumber(String driverLicenceNumber) { this.driverLicenceNumber = driverLicenceNumber; }
    public Boolean getThirdPartyInvolved() { return thirdPartyInvolved; }
    public void setThirdPartyInvolved(Boolean thirdPartyInvolved) { this.thirdPartyInvolved = thirdPartyInvolved; }
    public String getPoliceReportNumber() { return policeReportNumber; }
    public void setPoliceReportNumber(String policeReportNumber) { this.policeReportNumber = policeReportNumber; }
    public String getRepairWorkshop() { return repairWorkshop; }
    public void setRepairWorkshop(String repairWorkshop) { this.repairWorkshop = repairWorkshop; }
    public String getClaimCategory() { return claimCategory; }
    public void setClaimCategory(String claimCategory) { this.claimCategory = claimCategory; }
}

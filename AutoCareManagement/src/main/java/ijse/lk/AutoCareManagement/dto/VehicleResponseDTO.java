package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.FuelType;
import ijse.lk.AutoCareManagement.enumeration.TransmissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponseDTO {

    private long vehicleId;
    private String vehicleCode;
    private String licensePlate;
    private String brand;
    private String model;
    private int manufactureYear;
    private String chassisNumber;
    private String engineNumber;
    private FuelType fuelType;
    private TransmissionType transmissionType;
    private String color;
    private LocalDateTime createdAt;

    // Customer Details
    private String customerUserCode;
    private String customerUsername;
    private String customerPhone;
    private String customerEmail;
}

package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.FuelType;
import ijse.lk.AutoCareManagement.enumeration.TransmissionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRequestDTO {

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    private int manufactureYear;

    private String chassisNumber;

    private String engineNumber;

    @NotNull(message = "Fuel type is required")
    private FuelType fuelType;

    @NotNull(message = "Transmission type is required")
    private TransmissionType transmissionType;

    private String color;

    @NotBlank(message = "Customer user code is required")
    private String customerUserCode;
}

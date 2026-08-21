package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.VehicleRequestDTO;
import ijse.lk.AutoCareManagement.dto.VehicleResponseDTO;

import java.util.List;

public interface VehicleService {

    VehicleResponseDTO registerVehicle(VehicleRequestDTO vehicleRequestDTO);

    List<VehicleResponseDTO> getAllVehicles();

    VehicleResponseDTO getVehicleByVehicleCode(String vehicleCode);

    VehicleResponseDTO getVehicleByLicensePlate(String licensePlate);

    List<VehicleResponseDTO> getVehiclesByCustomerCode(String customerUserCode);

    List<VehicleResponseDTO> filterVehiclesByLicensePlate(String licensePlate);

    VehicleResponseDTO updateVehicle(String vehicleCode, VehicleRequestDTO vehicleRequestDTO);

    void deleteVehicle(String vehicleCode);

    long getTotalVehiclesCount();
}

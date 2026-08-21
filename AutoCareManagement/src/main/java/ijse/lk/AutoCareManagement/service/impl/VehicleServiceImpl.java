package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.VehicleRequestDTO;
import ijse.lk.AutoCareManagement.dto.VehicleResponseDTO;
import ijse.lk.AutoCareManagement.entity.User;
import ijse.lk.AutoCareManagement.entity.Vehicle;
import ijse.lk.AutoCareManagement.enumeration.UserStatus;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.UserRepository;
import ijse.lk.AutoCareManagement.repository.VehicleRepository;
import ijse.lk.AutoCareManagement.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Override
    public VehicleResponseDTO registerVehicle(VehicleRequestDTO vehicleRequestDTO) {
       log.info("Registering new  vehicle: {}", vehicleRequestDTO);

       if(vehicleRepository.existsByLicensePlate(vehicleRequestDTO.getLicensePlate())){
           log.warn("License Plate '{}' is already registered!", vehicleRequestDTO.getLicensePlate());
           throw new CustomException(400, "License Plate '" + vehicleRequestDTO.getLicensePlate() + "' is already registered!");
       }

        if (vehicleRequestDTO.getChassisNumber() != null && !vehicleRequestDTO.getChassisNumber().trim().isEmpty()) {
            if (vehicleRepository.existsByChassisNumber(vehicleRequestDTO.getChassisNumber())) {
                log.warn("Vehicle registration failed: Chassis number '{}' is already registered", vehicleRequestDTO.getChassisNumber());
                throw new CustomException(400, "Chassis Number '" + vehicleRequestDTO.getChassisNumber() + "' is already registered!");
            }
        }


        Optional<User> userOptional = userRepository.findByUserCodeAndStatus(vehicleRequestDTO.getCustomerUserCode(), UserStatus.ACTIVE);
        if (userOptional.isEmpty()) {
            log.warn("Vehicle registration failed: Customer with user code '{}' not found or inactive", vehicleRequestDTO.getCustomerUserCode());
            throw new CustomException(404, "Customer with user code '" + vehicleRequestDTO.getCustomerUserCode() + "' not found or inactive");
        }

        String generatedVehicleCode = generateVehicleCode();

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleCode(generatedVehicleCode);
        vehicle.setLicensePlate(vehicleRequestDTO.getLicensePlate());
        vehicle.setBrand(vehicleRequestDTO.getBrand());
        vehicle.setModel(vehicleRequestDTO.getModel());
        vehicle.setManufactureYear(vehicleRequestDTO.getManufactureYear());
        vehicle.setChassisNumber(vehicleRequestDTO.getChassisNumber());
        vehicle.setEngineNumber(vehicleRequestDTO.getEngineNumber());
        vehicle.setFuelType(vehicleRequestDTO.getFuelType());
        vehicle.setTransmissionType(vehicleRequestDTO.getTransmissionType());
        vehicle.setColor(vehicleRequestDTO.getColor());
        vehicle.setCustomer(userOptional.get());

        vehicle.setQrCodeHash(UUID.randomUUID().toString());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle successfully registered with code: {} for customer: {}", generatedVehicleCode, userOptional.get().getUserCode());
        return mapToResponseDTO(savedVehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getAllVehicles() {
        log.info("Fetching all registered vehicles");
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<VehicleResponseDTO> responseDTOList = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            responseDTOList.add(mapToResponseDTO(vehicle));
        }

        log.info("Successfully retrieved {} vehicles", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDTO getVehicleByVehicleCode(String vehicleCode) {
       log.info("Fetching vehicle with code: {}", vehicleCode);
       Optional<Vehicle> vehicleOptional = vehicleRepository.findByVehicleCode(vehicleCode);
         if (vehicleOptional.isEmpty()) {
            log.warn("Vehicle with code '{}' not found", vehicleCode);
            throw new CustomException(404, "Vehicle with code '" + vehicleCode + "' not found");
         }
         return mapToResponseDTO(vehicleOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDTO getVehicleByLicensePlate(String licensePlate) {
        log.info("Fetching vehicle with license plate: {}", licensePlate);
        Optional<Vehicle> vehicleOptional = vehicleRepository.findByLicensePlate(licensePlate);
        if (vehicleOptional.isEmpty()) {
            log.warn("Vehicle with license plate '{}' not found", licensePlate);
            throw new CustomException(404, "Vehicle with license plate '" + licensePlate + "' not found");
        }
        return mapToResponseDTO(vehicleOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getVehiclesByCustomerCode(String customerUserCode) {
        log.info("Fetching vehicles for customerUserCode: {}", customerUserCode);
        if (!userRepository.existsByUserCode(customerUserCode)) {
            log.error("Customer vehicles fetch failed: Customer not found with code: {}", customerUserCode);
            throw new CustomException(404, "Customer not found with code: " + customerUserCode);
        }

        List<Vehicle> vehicles = vehicleRepository.findByCustomer_userCode(customerUserCode);
        List<VehicleResponseDTO> responseDTOList = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            responseDTOList.add(mapToResponseDTO(vehicle));
        }

        log.info("Found {} vehicles for customer: {}", responseDTOList.size(), customerUserCode);
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> filterVehiclesByLicensePlate(String licensePlate) {
        log.info("Filtering vehicles with license plate query: '{}'", licensePlate);
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            log.info("License plate query is empty, returning all vehicles");
            return getAllVehicles();
        }

        List<Vehicle> filteredVehicles = vehicleRepository.findByLicensePlateContainingIgnoreCase(licensePlate);
        List<VehicleResponseDTO> responseDTOList = new ArrayList<>();

        for (Vehicle vehicle : filteredVehicles) {
            responseDTOList.add(mapToResponseDTO(vehicle));
        }

        log.info("Filter matched {} vehicles for query: '{}'", responseDTOList.size(), licensePlate);
        return responseDTOList;
    }

    @Override
    public VehicleResponseDTO updateVehicle(String vehicleCode, VehicleRequestDTO vehicleRequestDTO) {
        log.info("update vehicle with code: {}", vehicleCode);
        Optional<Vehicle> vehicleOptional = vehicleRepository.findByVehicleCode(vehicleCode);
        if (vehicleOptional.isEmpty()) {
            log.warn("Vehicle with code '{}' not found for update", vehicleCode);
            throw new CustomException(404, "Vehicle with code '" + vehicleCode + "' not found");
        }

        Vehicle vehicle = vehicleOptional.get();

        if (!vehicle.getLicensePlate().equalsIgnoreCase(vehicleRequestDTO.getLicensePlate())) {
            if (vehicleRepository.existsByLicensePlate(vehicleRequestDTO.getLicensePlate())) {
                log.warn("Vehicle update failed: License plate '{}' is already in use", vehicleRequestDTO.getLicensePlate());
                throw new CustomException(400, "License Plate '" + vehicleRequestDTO.getLicensePlate() + "' is already in use!");
            }
        }

        if (vehicleRequestDTO.getChassisNumber() != null && !vehicleRequestDTO.getChassisNumber().trim().isEmpty()) {
            if (vehicle.getChassisNumber() == null || !vehicle.getChassisNumber().equals(vehicleRequestDTO.getChassisNumber())) {
                if (vehicleRepository.existsByChassisNumber(vehicleRequestDTO.getChassisNumber())) {
                    log.warn("Vehicle update failed: Chassis number '{}' is already registered", vehicleRequestDTO.getChassisNumber());
                    throw new CustomException(400, "Chassis Number '" + vehicleRequestDTO.getChassisNumber() + "' is already registered!");
                }
            }
        }

        if (!vehicle.getCustomer().getUserCode().equals(vehicleRequestDTO.getCustomerUserCode())) {
            log.info("Changing owner for vehicle {} to new customerCode: {}", vehicleCode, vehicleRequestDTO.getCustomerUserCode());

            Optional<User> newCustomerOptional = userRepository.findByUserCodeAndStatus(vehicleRequestDTO.getCustomerUserCode(), UserStatus.ACTIVE);
            if (newCustomerOptional.isEmpty()) {
                log.error("Vehicle owner change failed: Customer not found with code: {}", vehicleRequestDTO.getCustomerUserCode());
                throw new CustomException(404, "New Customer not found with code: " + vehicleRequestDTO.getCustomerUserCode());
            }

            vehicle.setCustomer(newCustomerOptional.get());
        }

        vehicle.setLicensePlate(vehicleRequestDTO.getLicensePlate());
        vehicle.setBrand(vehicleRequestDTO.getBrand());
        vehicle.setModel(vehicleRequestDTO.getModel());
        vehicle.setManufactureYear(vehicleRequestDTO.getManufactureYear());
        vehicle.setChassisNumber(vehicleRequestDTO.getChassisNumber());
        vehicle.setEngineNumber(vehicleRequestDTO.getEngineNumber());
        vehicle.setFuelType(vehicleRequestDTO.getFuelType());
        vehicle.setTransmissionType(vehicleRequestDTO.getTransmissionType());
        vehicle.setColor(vehicleRequestDTO.getColor());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle successfully updated with code: {}", vehicleCode);

        return mapToResponseDTO(updatedVehicle);
    }

    @Override
    public void deleteVehicle(String vehicleCode) {
        log.info("Deleting vehicle with code: {}", vehicleCode);
        Optional<Vehicle> vehicleOptional = vehicleRepository.findByVehicleCode(vehicleCode);
        if (vehicleOptional.isEmpty()) {
            log.warn("Vehicle with code '{}' not found for deletion", vehicleCode);
            throw new CustomException(404, "Vehicle with code '" + vehicleCode + "' not found");
        }

        vehicleRepository.delete(vehicleOptional.get());
        log.info("Vehicle successfully deleted with code: {}", vehicleCode);
    }

    @Override
    public long getTotalVehiclesCount() {
       log.info("Fetching total count of registered vehicles");
       long count = vehicleRepository.getAllVehiclesCount();
       log.info("Total registered vehicles count: {}", count);
       return count;
    }

    private String generateVehicleCode() {
        long currentCount = vehicleRepository.getAllVehiclesCount();
        long nextNumber = currentCount + 1;
        int currentYear = Year.now().getValue();

        String generatedCode = String.format("VEH-%d-%04d", currentYear, nextNumber);

        while (vehicleRepository.existsByVehicleCode(generatedCode)) {
            nextNumber++;
            generatedCode = String.format("VEH-%d-%04d", currentYear, nextNumber);
        }

        log.debug("Generated unique vehicle code: {}", generatedCode);
        return generatedCode;
    }

    private VehicleResponseDTO mapToResponseDTO(Vehicle vehicle) {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        dto.setVehicleId(vehicle.getVehicleId());
        dto.setVehicleCode(vehicle.getVehicleCode());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setManufactureYear(vehicle.getManufactureYear());
        dto.setChassisNumber(vehicle.getChassisNumber());
        dto.setEngineNumber(vehicle.getEngineNumber());
        dto.setFuelType(vehicle.getFuelType());
        dto.setTransmissionType(vehicle.getTransmissionType());
        dto.setColor(vehicle.getColor());
        dto.setCreatedAt(vehicle.getCreatedAt());

        if (vehicle.getCustomer() != null) {
            dto.setCustomerUserCode(vehicle.getCustomer().getUserCode());
            dto.setCustomerUsername(vehicle.getCustomer().getUsername());
            dto.setCustomerPhone(vehicle.getCustomer().getPhone());
            dto.setCustomerEmail(vehicle.getCustomer().getEmail());
        }

        return dto;
    }
}

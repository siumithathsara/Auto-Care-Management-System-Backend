package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.VehicleRequestDTO;
import ijse.lk.AutoCareManagement.dto.VehicleResponseDTO;
import ijse.lk.AutoCareManagement.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/vehicle")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor

public class VehicleController {

    private final VehicleService vehicleService;

// vehicle register
    @PostMapping(value = "/register-vehicle", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR', 'CUSTOMER')")
    public CommonResponse registerVehicle(@Valid @RequestBody VehicleRequestDTO vehicleRequestDTO) {
        VehicleResponseDTO responseDTO = vehicleService.registerVehicle(vehicleRequestDTO);
        return new CommonResponse(201, responseDTO, "Vehicle registered successfully!");
    }

//    get all vehicles
    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse getAllVehicles() {
        List<VehicleResponseDTO> vehicles = vehicleService.getAllVehicles();
        return new CommonResponse(200, vehicles, "All vehicles fetched successfully!");
    }

//    get all vehicle by using vehicle code
    @GetMapping(value = "/get-by-code/{vehicleCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR', 'CUSTOMER')")
    public CommonResponse getVehicleByCode(@PathVariable String vehicleCode) {
        VehicleResponseDTO vehicle = vehicleService.getVehicleByVehicleCode(vehicleCode);
        return new CommonResponse(200, vehicle, "Vehicle fetched successfully!");
    }

//   get all vehicle by using license plate
    @GetMapping(value = "/get-by-license/{licensePlate}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR', 'CUSTOMER')")
    public CommonResponse getVehicleByLicensePlate(@PathVariable String licensePlate) {
        VehicleResponseDTO vehicle = vehicleService.getVehicleByLicensePlate(licensePlate);
        return new CommonResponse(200, vehicle, "Vehicle fetched successfully!");
    }

//   get all vehicle by using customer code
    @GetMapping(value = "/get-by-customer/{customerUserCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR', 'CUSTOMER')")
    public CommonResponse getVehiclesByCustomerCode(@PathVariable String customerUserCode) {
        List<VehicleResponseDTO> vehicles = vehicleService.getVehiclesByCustomerCode(customerUserCode);
        return new CommonResponse(200, vehicles, "Customer vehicles fetched successfully!");
    }

//   filter vehicle by using license plate
    @GetMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse filterVehiclesByLicensePlate(@RequestParam(value = "licensePlate", required = false) String licensePlate) {
        List<VehicleResponseDTO> vehicles = vehicleService.filterVehiclesByLicensePlate(licensePlate);
        return new CommonResponse(200, vehicles, "Vehicles filtered successfully!");
    }

//  update vehicle by using vehicle code
    @PutMapping(value = "/update/{vehicleCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR', 'CUSTOMER')")
    public CommonResponse updateVehicle(@PathVariable String vehicleCode,
                                        @Valid @RequestBody VehicleRequestDTO vehicleRequestDTO) {

        VehicleResponseDTO updatedVehicle = vehicleService.updateVehicle(vehicleCode, vehicleRequestDTO);
        return new CommonResponse(200, updatedVehicle, "Vehicle updated successfully!");
    }

// delete vehicle by using vehicle code
    @DeleteMapping(value = "/delete/{vehicleCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse deleteVehicle(@PathVariable String vehicleCode) {
        vehicleService.deleteVehicle(vehicleCode);
        return new CommonResponse(200, null, "Vehicle deleted successfully!");
    }

// get total vehicles count
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse getTotalVehiclesCount() {
        long count = vehicleService.getTotalVehiclesCount();
        return new CommonResponse(200, count, "Total vehicles count fetched successfully!");
    }
}

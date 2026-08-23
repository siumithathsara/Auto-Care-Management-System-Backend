package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.AppointmentRequestDTO;
import ijse.lk.AutoCareManagement.dto.AppointmentResponseDTO;
import ijse.lk.AutoCareManagement.enumeration.AppointmentStatus;
import ijse.lk.AutoCareManagement.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

//    create new appointment
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public CommonResponse createAppointment(@Valid @RequestBody AppointmentRequestDTO dto) {
        AppointmentResponseDTO responseDTO = appointmentService.createAppointment(dto);
        return new CommonResponse(201, responseDTO, "Appointment created successfully!");
    }

//     Update appointment (PENDING status only)
    @PutMapping(value = "/update/{appointmentCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public CommonResponse updateAppointment(@PathVariable String appointmentCode, @Valid @RequestBody AppointmentRequestDTO dto) {
        AppointmentResponseDTO responseDTO = appointmentService.updateAppointment(appointmentCode, dto);
        return new CommonResponse(200, responseDTO, "Appointment updated successfully!");
    }

//     Get appointment details by appointment code
    @GetMapping(value = "/get-by-code/{appointmentCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAppointmentByCode(@PathVariable String appointmentCode) {
        AppointmentResponseDTO responseDTO = appointmentService.getAppointmentByCode(appointmentCode);
        return new CommonResponse(200, responseDTO, "Appointment fetched successfully!");
    }

//     Get all appointments
    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse getAllAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
        return new CommonResponse(200, appointments, "All appointments fetched successfully!");
    }

//     Get appointments by customer code
    @GetMapping(value = "/get-by-customer/{userCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAppointmentsByCustomer(@PathVariable String userCode) {
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByCustomer(userCode);
        return new CommonResponse(200, appointments, "Customer appointments fetched successfully!");
    }

//     Get appointments by status (PENDING, CONFIRMED, COMPLETED, CANCELLED )
    @GetMapping(value = "/get-by-status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByStatus(status);
        return new CommonResponse(200, appointments, "Appointments fetched by status successfully!");
    }

//     Update appointment status
    @PatchMapping(value = "/change-status/{appointmentCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse updateAppointmentStatus(
            @PathVariable String appointmentCode,
            @RequestParam(value = "status") AppointmentStatus status) {
        AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointmentStatus(appointmentCode, status);
        return new CommonResponse(200, updatedAppointment, "Appointment status updated successfully!");
    }

//     Get total count of appointments
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse getTotalAppointmentsCount() {
        long count = appointmentService.getTotalAppointmentsCount();
        return new CommonResponse(200, count, "Total appointments count fetched successfully!");
    }
}

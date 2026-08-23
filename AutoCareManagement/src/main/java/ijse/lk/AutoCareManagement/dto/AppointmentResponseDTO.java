package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {

    private String appointmentCode;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;
    private double estimatedTotalFee;
    private String specialNotes;
    private LocalDateTime createdAt;

    // customer details
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // vehicle details
    private String vehicleCode;
    private String licensePlate;
    private String vehicleModel;

    // service list
    private List<ServiceResponseDTO> selectedServices;
}

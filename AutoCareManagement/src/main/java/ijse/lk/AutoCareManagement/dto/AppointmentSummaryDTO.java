package ijse.lk.AutoCareManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentSummaryDTO {

    private String appointmentCode;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String specialNotes;
    private String status;
    private String licensePlate;
}

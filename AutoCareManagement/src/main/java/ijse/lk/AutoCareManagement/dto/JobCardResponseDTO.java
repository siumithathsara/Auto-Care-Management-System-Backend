package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCardResponseDTO {

    private String jobCardCode;
    private String vehicleCode;
    private String licensePlate;
    private String customerName;
    private String advisorName;
    private String appointmentCode;

    private int mileageIn;
    private String fuelLevel;
    private String customerNotes;
    private String advisorNotes;
    private JobStatus status;

    private LocalDateTime checkInTime;
    private LocalDateTime estimatedCompletionTime;
    private LocalDateTime checkOutTime;

    private List<JobCardServiceResponseDTO> services;
    private List<JobCardPartResponseDTO> parts;

    private double totalServicesFee;
    private double totalPartsFee;
    private double estimatedTotalFee;

}

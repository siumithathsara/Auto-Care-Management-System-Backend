package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.AppointmentRequestDTO;
import ijse.lk.AutoCareManagement.dto.AppointmentResponseDTO;
import ijse.lk.AutoCareManagement.enumeration.AppointmentStatus;

import java.util.List;

public interface AppointmentService {

    AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto);

    AppointmentResponseDTO getAppointmentByCode(String appointmentCode);

    List<AppointmentResponseDTO> getAllAppointments();

    List<AppointmentResponseDTO> getAppointmentsByCustomer(String userCode);

    List<AppointmentResponseDTO> getAppointmentsByStatus(AppointmentStatus status);

    AppointmentResponseDTO updateAppointmentStatus(String appointmentCode, AppointmentStatus newStatus);

    long getTotalAppointmentsCount();

    AppointmentResponseDTO updateAppointment(String appointmentCode, AppointmentRequestDTO dto);
}

package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.AppointmentRequestDTO;
import ijse.lk.AutoCareManagement.dto.AppointmentResponseDTO;
import ijse.lk.AutoCareManagement.dto.ServiceResponseDTO;
import ijse.lk.AutoCareManagement.entity.Appointment;
import ijse.lk.AutoCareManagement.entity.User;
import ijse.lk.AutoCareManagement.entity.Vehicle;
import ijse.lk.AutoCareManagement.entity.VehicleService;
import ijse.lk.AutoCareManagement.enumeration.AppointmentStatus;
import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import ijse.lk.AutoCareManagement.enumeration.UserStatus;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.AppointmentRepository;
import ijse.lk.AutoCareManagement.repository.ServiceRepository;
import ijse.lk.AutoCareManagement.repository.UserRepository;
import ijse.lk.AutoCareManagement.repository.VehicleRepository;
import ijse.lk.AutoCareManagement.service.AppointmentService;
import ijse.lk.AutoCareManagement.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final EmailService emailService;

    @Override
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto) {
        log.info("Creating new appointment for userCode: {}, vehicleCode: {}", dto.getUserCode(), dto.getVehicleCode());

        Optional<User> userOptional = userRepository.findByUserCodeAndStatus(dto.getUserCode(), UserStatus.ACTIVE);
        if (userOptional.isEmpty()) {
            log.warn("Appointment creation failed: Active customer not found with code: {}", dto.getUserCode());
            throw new CustomException(404, "Active customer not found with code: " + dto.getUserCode());
        }
        User customer = userOptional.get();

        Optional<Vehicle> vehicleOptional = vehicleRepository.findByVehicleCode(dto.getVehicleCode());
        if (vehicleOptional.isEmpty()) {
            log.warn("Appointment creation failed: Vehicle not found with code: {}", dto.getVehicleCode());
            throw new CustomException(404, "Vehicle not found with code: " + dto.getVehicleCode());
        }
        Vehicle vehicle = vehicleOptional.get();

        if (!vehicle.getCustomer().getUserCode().equals(customer.getUserCode())) {
            log.warn("Appointment creation failed: Vehicle does not belong to customer: {}", dto.getUserCode());
            throw new CustomException(400, "Vehicle does not belong to the given customer.");
        }

        List<VehicleService> selectedServices = new ArrayList<>();
        double totalFee = 0.0;

        for (String sCode : dto.getServiceCodes()) {
            Optional<VehicleService> serviceOptional =
                    serviceRepository.findByServiceCodeAndStatus(sCode, DataStatus.ACTIVE);

            if (serviceOptional.isEmpty()) {
                log.warn("Appointment creation failed: Active service not found with code: {}", sCode);
                throw new CustomException(404, "Active service not found with code: " + sCode);
            }

            VehicleService serviceEntity = serviceOptional.get();
            selectedServices.add(serviceEntity);

            totalFee += serviceEntity.getStandardFee();
        }

        String appointmentCode = generateAppointmentCode();

        Appointment appointment = new Appointment();
        appointment.setAppointmentCode(appointmentCode);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointmentTime(dto.getAppointmentTime());
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setEstimatedTotalFee(totalFee);
        appointment.setSpecialNotes(dto.getSpecialNotes());
        appointment.setCustomer(customer);
        appointment.setVehicle(vehicle);
        appointment.setServices(selectedServices);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created successfully with code: {}", appointmentCode);

        sendAdminAppointmentAlertEmail(savedAppointment);

        return mapToResponseDTO(savedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointmentByCode(String appointmentCode) {
        log.info("Fetching appointment with code: {}", appointmentCode);
        Optional<Appointment> appointmentOptional = appointmentRepository.findByAppointmentCode(appointmentCode);
        if (appointmentOptional.isEmpty()) {
            log.warn("Appointment with code '{}' not found", appointmentCode);
            throw new CustomException(404, "Appointment not found with code: " + appointmentCode);
        }
        return mapToResponseDTO(appointmentOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAllAppointments() {
        log.info("Fetching all appointments");
        List<Appointment> list = appointmentRepository.findAll();
        List<AppointmentResponseDTO> dtoList = new ArrayList<>();
        for (Appointment app : list) {
            dtoList.add(mapToResponseDTO(app));
        }
        log.info("Successfully retrieved {} appointments", dtoList.size());
        return dtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsByCustomer(String userCode) {
        log.info("Fetching appointments for userCode: {}", userCode);
        List<Appointment> list = appointmentRepository.findByCustomerUserCode(userCode);
        List<AppointmentResponseDTO> dtoList = new ArrayList<>();
        for (Appointment app : list) {
            dtoList.add(mapToResponseDTO(app));
        }
        log.info("Found {} appointments for userCode: {}", dtoList.size(), userCode);
        return dtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsByStatus(AppointmentStatus status) {
        log.info("Fetching appointments with status: {}", status);
        List<Appointment> list = appointmentRepository.findByStatus(status);
        List<AppointmentResponseDTO> dtoList = new ArrayList<>();
        for (Appointment app : list) {
            dtoList.add(mapToResponseDTO(app));
        }
        log.info("Found {} appointments with status: {}", dtoList.size(), status);
        return dtoList;
    }

    @Override
    public AppointmentResponseDTO updateAppointmentStatus(String appointmentCode, AppointmentStatus newStatus) {
        log.info("Updating status for appointmentCode: {} to {}", appointmentCode, newStatus);
        Optional<Appointment> appointmentOptional = appointmentRepository.findByAppointmentCode(appointmentCode);
        if (appointmentOptional.isEmpty()) {
            log.warn("Appointment with code '{}' not found for status update", appointmentCode);
            throw new CustomException(404, "Appointment not found with code: " + appointmentCode);
        }

        Appointment appointment = appointmentOptional.get();
        AppointmentStatus oldStatus = appointment.getStatus();

        appointment.setStatus(newStatus);
        Appointment updated = appointmentRepository.save(appointment);
        log.info("Appointment status updated successfully to {} for code: {}", newStatus, appointmentCode);

        if ((newStatus == AppointmentStatus.CONFIRMED || newStatus.name().equals("APPROVED")) && oldStatus != newStatus) {
            sendCustomerAppointmentApprovedEmail(updated);
        }

        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalAppointmentsCount() {
        log.info("Fetching total count of appointments");
        long count = appointmentRepository.getAppointmentCount();
        log.info("Total appointments count: {}", count);
        return count;
    }

    @Override
    public AppointmentResponseDTO updateAppointment(String appointmentCode, AppointmentRequestDTO dto) {
        log.info("Updating appointment with code: {}", appointmentCode);

        Optional<Appointment> appointmentOptional = appointmentRepository.findByAppointmentCode(appointmentCode);
        if (appointmentOptional.isEmpty()) {
            log.warn("Appointment update failed: Appointment with code '{}' not found", appointmentCode);
            throw new CustomException(404, "Appointment not found with code: " + appointmentCode);
        }

        Appointment appointment = appointmentOptional.get();

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            log.warn("Appointment update failed: Cannot update appointment with status '{}'", appointment.getStatus());
            throw new CustomException(400, "Only appointments with 'PENDING' status can be updated!");
        }

        Optional<User> userOptional = userRepository.findByUserCodeAndStatus(dto.getUserCode(), UserStatus.ACTIVE);
        if (userOptional.isEmpty()) {
            log.warn("Appointment update failed: Active customer not found with code: {}", dto.getUserCode());
            throw new CustomException(404, "Active customer not found with code: " + dto.getUserCode());
        }
        User customer = userOptional.get();

        Optional<Vehicle> vehicleOptional = vehicleRepository.findByVehicleCode(dto.getVehicleCode());
        if (vehicleOptional.isEmpty()) {
            log.warn("Appointment update failed: Vehicle not found with code: {}", dto.getVehicleCode());
            throw new CustomException(404, "Vehicle not found with code: " + dto.getVehicleCode());
        }
        Vehicle vehicle = vehicleOptional.get();

        if (!vehicle.getCustomer().getUserCode().equals(customer.getUserCode())) {
            log.warn("Appointment update failed: Vehicle does not belong to customer: {}", dto.getUserCode());
            throw new CustomException(400, "Vehicle does not belong to the given customer.");
        }

        List<VehicleService> selectedServices = new ArrayList<>();
        double totalFee = 0.0;

        for (String sCode : dto.getServiceCodes()) {
            Optional<VehicleService> serviceOptional =
                    serviceRepository.findByServiceCodeAndStatus(sCode, DataStatus.ACTIVE);

            if (serviceOptional.isEmpty()) {
                log.warn("Appointment update failed: Active service not found with code: {}", sCode);
                throw new CustomException(404, "Active service not found with code: " + sCode);
            }

            VehicleService serviceEntity = serviceOptional.get();
            selectedServices.add(serviceEntity);
            totalFee += serviceEntity.getStandardFee();
        }

        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointmentTime(dto.getAppointmentTime());
        appointment.setSpecialNotes(dto.getSpecialNotes());
        appointment.setCustomer(customer);
        appointment.setVehicle(vehicle);
        appointment.setServices(selectedServices);
        appointment.setEstimatedTotalFee(totalFee);

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment with code '{}' successfully updated", appointmentCode);

        return mapToResponseDTO(updatedAppointment);
    }

    private String generateAppointmentCode() {
        long currentCount = appointmentRepository.getAppointmentCount();
        long nextNumber = currentCount + 1;
        int currentYear = Year.now().getValue();

        String generatedCode = String.format("APT-%d-%04d", currentYear, nextNumber);

        while (appointmentRepository.existsByAppointmentCode(generatedCode)) {
            nextNumber++;
            generatedCode = String.format("APT-%d-%04d", currentYear, nextNumber);
        }

        log.debug("Generated unique appointment code: {}", generatedCode);
        return generatedCode;
    }

    private AppointmentResponseDTO mapToResponseDTO(Appointment appointment) {
        List<ServiceResponseDTO> serviceDTOs = new ArrayList<>();
        if (appointment.getServices() != null) {
            for (VehicleService s : appointment.getServices()) {
                ServiceResponseDTO serviceDTO = new ServiceResponseDTO();
                serviceDTO.setServiceCode(s.getServiceCode());
                serviceDTO.setServiceName(s.getServiceName());
                serviceDTO.setDescription(s.getDescription());
                serviceDTO.setStandardFee(s.getStandardFee());
                serviceDTO.setEstimatedTimeMin(s.getEstimatedTimeMins());
                serviceDTO.setDataStatus(s.getStatus());

                if (s.getCategory() != null) {
                    serviceDTO.setCategoryCode(s.getCategory().getCategoryCode());
                    serviceDTO.setCategoryName(s.getCategory().getCategoryName());
                }
                serviceDTOs.add(serviceDTO);
            }
        }

        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setAppointmentCode(appointment.getAppointmentCode());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setStatus(appointment.getStatus());
        dto.setEstimatedTotalFee(appointment.getEstimatedTotalFee());
        dto.setSpecialNotes(appointment.getSpecialNotes());
        dto.setCreatedAt(appointment.getCreatedAt());

        if (appointment.getCustomer() != null) {
            dto.setCustomerName(appointment.getCustomer().getUsername());
            dto.setCustomerPhone(appointment.getCustomer().getPhone());
            dto.setCustomerEmail(appointment.getCustomer().getEmail());
        }

        if (appointment.getVehicle() != null) {
            dto.setVehicleCode(appointment.getVehicle().getVehicleCode());
            dto.setLicensePlate(appointment.getVehicle().getLicensePlate());
            dto.setVehicleModel(appointment.getVehicle().getModel());
        }

        dto.setSelectedServices(serviceDTOs);
        return dto;
    }

    private void sendAdminAppointmentAlertEmail(Appointment appointment) {
        try {
            String subject = "New Appointment Alert - " + appointment.getAppointmentCode();

            Map<String, String> variables = new HashMap<>();
            variables.put("appointmentCode", appointment.getAppointmentCode());
            variables.put("customerName", appointment.getCustomer().getUsername());
            variables.put("customerPhone", appointment.getCustomer().getPhone());
            variables.put("licensePlate", appointment.getVehicle().getLicensePlate());
            variables.put("appointmentDate", appointment.getAppointmentDate().toString());
            variables.put("appointmentTime", appointment.getAppointmentTime().toString());
            variables.put("estimatedTotalFee", String.valueOf(appointment.getEstimatedTotalFee()));
            variables.put("specialNotes", appointment.getSpecialNotes() != null ? appointment.getSpecialNotes() : "None");

            emailService.sendTemplateEmail("autocare.service.official@gmail.com", subject, "new-appointment-admin-alert", variables);
        } catch (Exception e) {
            log.error("Failed to send admin appointment alert email: {}", e.getMessage());
        }
    }

    private void sendCustomerAppointmentApprovedEmail(Appointment appointment) {
        try {
            String subject = "Appointment Approved - " + appointment.getAppointmentCode();

            Map<String, String> variables = new HashMap<>();
            variables.put("customerName", appointment.getCustomer().getUsername());
            variables.put("appointmentCode", appointment.getAppointmentCode());
            variables.put("licensePlate", appointment.getVehicle().getLicensePlate());
            variables.put("appointmentDate", appointment.getAppointmentDate().toString());
            variables.put("appointmentTime", appointment.getAppointmentTime().toString());
            variables.put("estimatedTotalFee", String.valueOf(appointment.getEstimatedTotalFee()));

            emailService.sendTemplateEmail(appointment.getCustomer().getEmail(), subject, "appointment-approved-customer", variables);
        } catch (Exception e) {
            log.error("Failed to send customer approval email: {}", e.getMessage());
        }
    }
}

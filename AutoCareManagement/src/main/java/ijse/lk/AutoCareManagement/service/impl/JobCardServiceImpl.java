package ijse.lk.AutoCareManagement.service.impl;
import ijse.lk.AutoCareManagement.dto.JobCardPartRequestDTO;
import ijse.lk.AutoCareManagement.dto.JobCardRequestDTO;
import ijse.lk.AutoCareManagement.dto.JobCardPartResponseDTO;
import ijse.lk.AutoCareManagement.dto.JobCardResponseDTO;
import ijse.lk.AutoCareManagement.dto.JobCardServiceResponseDTO;
import ijse.lk.AutoCareManagement.entity.*;
import ijse.lk.AutoCareManagement.enumeration.AppointmentStatus;
import ijse.lk.AutoCareManagement.enumeration.ApprovalStatus;
import ijse.lk.AutoCareManagement.enumeration.IssueStatus;
import ijse.lk.AutoCareManagement.enumeration.JobStatus;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.*;
import ijse.lk.AutoCareManagement.service.JobCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobCardServiceImpl implements JobCardService {

    private final JobCardRepository jobCardRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository vehicleServiceRepository;
    private final SparePartRepository sparePartRepository;


    @Override
    public JobCardResponseDTO createJobCard(JobCardRequestDTO dto) {
        log.info("Creating Job Card for vehicle: {}", dto.getVehicleCode());

        Optional<Vehicle> vehicleOptional = vehicleRepository.findByVehicleCode(dto.getVehicleCode());
        if (vehicleOptional.isEmpty()) {
            log.warn("Job Card creation failed: Vehicle with code '{}' not found", dto.getVehicleCode());
            throw new CustomException(404, "Vehicle not found!");
        }

        List<JobStatus> activeStatuses = List.of(JobStatus.PENDING, JobStatus.IN_PROGRESS);
        if (jobCardRepository.existsByVehicleVehicleCodeAndStatusIn(dto.getVehicleCode(), activeStatuses)) {
            log.warn("Job Card creation failed: Active Job Card already exists for vehicle '{}'", dto.getVehicleCode());
            throw new CustomException(400, "An active Job Card already exists for this vehicle!");
        }

        Optional<User> advisorOptional = userRepository.findByUserCode(dto.getAdvisorUserCode());
        if (advisorOptional.isEmpty()) {
            log.warn("Job Card creation failed: Advisor with code '{}' not found", dto.getAdvisorUserCode());
            throw new CustomException(404, "Advisor not found!");
        }

        Appointment appointment = null;
        if (dto.getAppointmentCode() != null && !dto.getAppointmentCode().trim().isEmpty()) {
            Optional<Appointment> appointmentOptional = appointmentRepository.findByAppointmentCode(dto.getAppointmentCode());
            if (appointmentOptional.isEmpty()) {
                log.warn("Job Card creation failed: Appointment with code '{}' not found", dto.getAppointmentCode());
                throw new CustomException(404, "Appointment not found!");
            }

            appointment = appointmentOptional.get();

            if (appointment.getStatus() == AppointmentStatus.PENDING) {
                log.warn("Job Card creation failed: Appointment '{}' is still PENDING", dto.getAppointmentCode());
                throw new CustomException(400, "Cannot create Job Card. Appointment is still PENDING. Please confirm it first!");
            }
            if (appointment.getStatus() == AppointmentStatus.COMPLETED || appointment.getStatus() == AppointmentStatus.CANCELLED) {
                log.warn("Job Card creation failed: Appointment '{}' is already {}", dto.getAppointmentCode(), appointment.getStatus());
                throw new CustomException(400, "Cannot create Job Card. Appointment is already " + appointment.getStatus() + "!");
            }

            appointment.setStatus(AppointmentStatus.IN_PROGRESS);
            appointmentRepository.save(appointment);
            log.info("Updated appointment status to IN_PROGRESS for code: {}", dto.getAppointmentCode());
        }

        String generatedCode = generateJobCardCode();

        JobCard jobCard = new JobCard();
        jobCard.setJobCardCode(generatedCode);
        jobCard.setVehicle(vehicleOptional.get());
        jobCard.setAdvisor(advisorOptional.get());
        jobCard.setAppointment(appointment);
        jobCard.setMileageIn(dto.getMileageIn());
        jobCard.setFuelLevel(dto.getFuelLevel());
        jobCard.setCustomerNotes(dto.getCustomerNotes());
        jobCard.setAdvisorNotes(dto.getAdvisorNotes());
        jobCard.setEstimatedCompletionTime(dto.getEstimatedCompletionTime());
        jobCard.setStatus(JobStatus.IN_PROGRESS);

        List<JobCardServiceCategory> jobCardServices = new ArrayList<>();
        if (dto.getServiceCodes() != null) {
            for (String serviceCode : dto.getServiceCodes()) {
                Optional<VehicleService> serviceOptional = vehicleServiceRepository.findByServiceCode(serviceCode);
                if (serviceOptional.isEmpty()) {
                    log.warn("Job Card creation failed: Vehicle service '{}' not found", serviceCode);
                    throw new CustomException(404, "Vehicle service not found: " + serviceCode);
                }

                VehicleService vehicleService = serviceOptional.get();

                JobCardServiceCategory jcs = new JobCardServiceCategory();
                jcs.setJobCard(jobCard);
                jcs.setVehicleService(vehicleService);
                jcs.setPrice(vehicleService.getStandardFee());
                jcs.setApprovalStatus(ApprovalStatus.APPROVED);
                jobCardServices.add(jcs);
            }
        }
        jobCard.setServices(jobCardServices);

        JobCard savedJobCard = jobCardRepository.save(jobCard);
        log.info("Job Card successfully created with code: {} for vehicle: {}", generatedCode, dto.getVehicleCode());

        return mapToResponse(savedJobCard);
    }

    @Override
    public JobCardResponseDTO addPartsToJobCard(String jobCardCode, List<JobCardPartRequestDTO> partDTOs) {
        log.info("Adding spare parts to Job Card: {}", jobCardCode);

        Optional<JobCard> jobCardOptional = jobCardRepository.findByJobCardCode(jobCardCode);
        if (jobCardOptional.isEmpty()) {
            log.warn("Add parts failed: Job Card with code '{}' not found", jobCardCode);
            throw new CustomException(404, "Job Card not found!");
        }

        JobCard jobCard = jobCardOptional.get();

        if (jobCard.getStatus() == JobStatus.COMPLETED || jobCard.getStatus() == JobStatus.CANCELLED) {
            log.warn("Add parts failed: Job Card '{}' is already in {} state", jobCardCode, jobCard.getStatus());
            throw new CustomException(400, "Cannot add parts to a COMPLETED or CANCELLED Job Card!");
        }

        if (jobCard.getItems() == null) {
            jobCard.setItems(new ArrayList<>());
        }

        for (JobCardPartRequestDTO partDTO : partDTOs) {
            Optional<SparePart> sparePartOptional = sparePartRepository.findByPartCode(partDTO.getPartCode());
            if (sparePartOptional.isEmpty()) {
                log.warn("Add parts failed: Spare Part with code '{}' not found", partDTO.getPartCode());
                throw new CustomException(404, "Spare Part not found with code: " + partDTO.getPartCode());
            }

            Optional<User> requestedByOptional = userRepository.findByUserCode(partDTO.getRequestedByUserCode());
            if (requestedByOptional.isEmpty()) {
                log.warn("Add parts failed: User (Requested By) with code '{}' not found", partDTO.getRequestedByUserCode());
                throw new CustomException(404, "User (Requested By) not found!");
            }

            SparePart sparePart = sparePartOptional.get();

            if (sparePart.getQuantityInStock() < partDTO.getQuantity()) {
                log.warn("Add parts failed: Insufficient stock for part '{}'. Available: {}, Requested: {}",
                        sparePart.getPartName(), sparePart.getQuantityInStock(), partDTO.getQuantity());
                throw new CustomException(400, "Insufficient stock for part: " + sparePart.getPartName());
            }

            JobCardPart jobCardPart = new JobCardPart();
            jobCardPart.setJobCard(jobCard);
            jobCardPart.setSparePart(sparePart);
            jobCardPart.setRequestedBy(requestedByOptional.get());
            jobCardPart.setQuantity(partDTO.getQuantity());
            jobCardPart.setUnitPrice(sparePart.getUnitPrice());
            jobCardPart.setSubTotal(sparePart.getUnitPrice() * partDTO.getQuantity());
            jobCardPart.setIssueStatus(IssueStatus.REQUESTED);

            jobCard.getItems().add(jobCardPart);
        }

        JobCard updatedJobCard = jobCardRepository.save(jobCard);
        log.info("Successfully added {} parts to Job Card code: {}", partDTOs.size(), jobCardCode);

        return mapToResponse(updatedJobCard);
    }

    @Override
    @Transactional(readOnly = true)
    public JobCardResponseDTO getJobCardByCode(String jobCardCode) {
        log.info("Fetching Job Card with code: {}", jobCardCode);
        Optional<JobCard> jobCardOptional = jobCardRepository.findByJobCardCode(jobCardCode);
        if (jobCardOptional.isEmpty()) {
            log.warn("Job Card with code '{}' not found", jobCardCode);
            throw new CustomException(404, "Job Card not found!");
        }

        log.info("Successfully fetched Job Card with code: {}", jobCardCode);
        return mapToResponse(jobCardOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobCardResponseDTO> getAllJobCards() {
        log.info("Fetching all Job Cards");
        List<JobCard> list = jobCardRepository.findAll();
        List<JobCardResponseDTO> dtoList = new ArrayList<>();

        for (JobCard jc : list) {
            dtoList.add(mapToResponse(jc));
        }

        log.info("Successfully retrieved {} Job Cards", dtoList.size());
        return dtoList;
    }

    @Override
    public JobCardResponseDTO updateJobCardStatus(String jobCardCode, JobStatus status) {
        log.info("Updating status for Job Card code: {} to new status: {}", jobCardCode, status);

        Optional<JobCard> jobCardOptional = jobCardRepository.findByJobCardCode(jobCardCode);
        if (jobCardOptional.isEmpty()) {
            log.warn("Job Card status update failed: Job Card with code '{}' not found", jobCardCode);
            throw new CustomException(404, "Job Card not found!");
        }

        JobCard jobCard = jobCardOptional.get();
        jobCard.setStatus(status);

        if (status == JobStatus.COMPLETED) {
            jobCard.setCheckOutTime(LocalDateTime.now());
            if (jobCard.getAppointment() != null) {
                jobCard.getAppointment().setStatus(AppointmentStatus.COMPLETED);
                log.info("Appointment '{}' marked as COMPLETED for Job Card: {}", jobCard.getAppointment().getAppointmentCode(), jobCardCode);
            }
        }

        JobCard updatedJobCard = jobCardRepository.save(jobCard);
        log.info("Job Card code: {} successfully updated to status: {}", jobCardCode, status);

        return mapToResponse(updatedJobCard);
    }

    private String generateJobCardCode() {
        long count = jobCardRepository.getJobCardCount() + 1;
        int year = Year.now().getValue();
        String code = String.format("JOB-%d-%04d", year, count);

        while (jobCardRepository.findByJobCardCode(code).isPresent()) {
            count++;
            code = String.format("JOB-%d-%04d", year, count);
        }

        log.debug("Generated unique Job Card code: {}", code);
        return code;
    }

    private JobCardResponseDTO mapToResponse(JobCard jobCard) {
        JobCardResponseDTO dto = new JobCardResponseDTO();
        dto.setJobCardCode(jobCard.getJobCardCode());

        if (jobCard.getVehicle() != null) {
            dto.setVehicleCode(jobCard.getVehicle().getVehicleCode());
            dto.setLicensePlate(jobCard.getVehicle().getLicensePlate());

            if (jobCard.getVehicle().getCustomer() != null) {
                dto.setCustomerName(jobCard.getVehicle().getCustomer().getUsername());
            }
        }

        if (jobCard.getAdvisor() != null) {
            dto.setAdvisorName(jobCard.getAdvisor().getUsername());
        }

        if (jobCard.getAppointment() != null) {
            dto.setAppointmentCode(jobCard.getAppointment().getAppointmentCode());
        }

        dto.setMileageIn(jobCard.getMileageIn());
        dto.setFuelLevel(jobCard.getFuelLevel());
        dto.setCustomerNotes(jobCard.getCustomerNotes());
        dto.setAdvisorNotes(jobCard.getAdvisorNotes());
        dto.setStatus(jobCard.getStatus());
        dto.setCheckInTime(jobCard.getCheckInTime());
        dto.setEstimatedCompletionTime(jobCard.getEstimatedCompletionTime());
        dto.setCheckOutTime(jobCard.getCheckOutTime());

//          services total calculation
        List<JobCardServiceResponseDTO> serviceDtos = new ArrayList<>();
        double totalServicesFee = 0.0;

        if (jobCard.getServices() != null) {
            for (JobCardServiceCategory jcs : jobCard.getServices()) {
                JobCardServiceResponseDTO sDto = new JobCardServiceResponseDTO();
                if (jcs.getVehicleService() != null) {
                    sDto.setServiceCode(jcs.getVehicleService().getServiceCode());
                    sDto.setServiceName(jcs.getVehicleService().getServiceName());
                }
                sDto.setPrice(jcs.getPrice());
                sDto.setApprovalStatus(jcs.getApprovalStatus());

                serviceDtos.add(sDto);
                totalServicesFee += jcs.getPrice();
            }
        }
        dto.setServices(serviceDtos);
        dto.setTotalServicesFee(totalServicesFee);

//         parts total calculation
        List<JobCardPartResponseDTO> partDtos = new ArrayList<>();
        double totalPartsFee = 0.0;

        if (jobCard.getItems() != null) {
            for (JobCardPart jcp : jobCard.getItems()) {
                JobCardPartResponseDTO pDto = new JobCardPartResponseDTO();
                if (jcp.getSparePart() != null) {
                    pDto.setPartCode(jcp.getSparePart().getPartCode());
                    pDto.setPartName(jcp.getSparePart().getPartName());
                }
                if (jcp.getRequestedBy() != null) {
                    pDto.setRequestedByName(jcp.getRequestedBy().getUsername());
                }
                pDto.setQuantity(jcp.getQuantity());
                pDto.setUnitPrice(jcp.getUnitPrice());
                pDto.setSubTotal(jcp.getSubTotal());
                pDto.setIssueStatus(jcp.getIssueStatus());

                partDtos.add(pDto);
                totalPartsFee += jcp.getSubTotal();
            }
        }
        dto.setParts(partDtos);
        dto.setTotalPartsFee(totalPartsFee);

        dto.setEstimatedTotalFee(totalServicesFee + totalPartsFee);

        return dto;
    }
}

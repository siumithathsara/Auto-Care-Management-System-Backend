package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.AppointmentSummaryDTO;
import ijse.lk.AutoCareManagement.dto.CustomerDashboardDTO;
import ijse.lk.AutoCareManagement.dto.JobCardSummaryDTO;
import ijse.lk.AutoCareManagement.entity.User;
import ijse.lk.AutoCareManagement.enumeration.AppointmentStatus;
import ijse.lk.AutoCareManagement.enumeration.JobStatus;
import ijse.lk.AutoCareManagement.enumeration.PaymentStatus;
import ijse.lk.AutoCareManagement.enumeration.UserStatus;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.*;
import ijse.lk.AutoCareManagement.service.CustomerDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerDashboardServiceImpl implements CustomerDashboardService {
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final JobCardRepository jobCardRepository;
    private final InvoiceRepository invoiceRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomerDashboardDTO getCustomerDashboardData(String customerCode) {
        log.info("Fetching dashboard summary data for customerCode/username: {}", customerCode);

        Optional<User> userOptional = userRepository.findByUserCodeOrUsernameAndStatus(customerCode);
        if (userOptional.isEmpty()) {
            log.warn("Dashboard data fetch failed: Active customer not found with code/username: {}", customerCode);
            throw new CustomException(404, "Active customer not found with code: " + customerCode);
        }

        User user = userOptional.get();
        String realUserCode = user.getUserCode();

        long totalVehicles = vehicleRepository.countByCustomerUserCode(realUserCode);

        List<JobStatus> activeStatuses = List.of(JobStatus.IN_PROGRESS);

        long activeJobs = jobCardRepository.countByVehicleCustomerUserCodeAndStatusIn(realUserCode, activeStatuses);
        long completedJobs = jobCardRepository.countByVehicleCustomerUserCodeAndStatus(realUserCode, JobStatus.COMPLETED);

        Double pendingAmount = invoiceRepository.findTotalPendingPaymentByCustomerCode(realUserCode, PaymentStatus.UNPAID);
        double pendingPaymentAmount = (pendingAmount != null) ? pendingAmount : 0.0;

        List<AppointmentSummaryDTO> approvedAppointments = appointmentRepository
                .findApprovedUpcomingAppointmentsByCustomerCode(realUserCode, LocalDate.now(), AppointmentStatus.CONFIRMED);

        List<JobCardSummaryDTO> activeJobsList = jobCardRepository.findActiveJobsByCustomerCode(realUserCode, activeStatuses);
        List<JobCardSummaryDTO> recentJobHistoryList = jobCardRepository.findTop5ByVehicleCustomerUserCodeOrderByCheckInTimeDesc(realUserCode);

        log.info("Successfully fetched dashboard data for real userCode: {}", realUserCode);

        return new CustomerDashboardDTO(
                totalVehicles,
                activeJobs,
                completedJobs,
                pendingPaymentAmount,
                approvedAppointments,
                activeJobsList,
                recentJobHistoryList
        );
    }
}

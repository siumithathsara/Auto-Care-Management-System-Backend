package ijse.lk.AutoCareManagement.entity;

import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VehicleService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private long serviceId;

    @Column(name = "service_code", unique = true, nullable = false, length = 50)
    private String serviceCode;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory category;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "standard_fee", nullable = false)
    private double standardFee;

    @Column(name = "estimated_time_mins")
    private int estimatedTimeMins;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DataStatus status = DataStatus.ACTIVE;
}

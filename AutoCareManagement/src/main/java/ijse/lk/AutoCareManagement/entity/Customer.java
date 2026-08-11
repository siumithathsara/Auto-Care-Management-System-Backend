package ijse.lk.AutoCareManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private long customerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Users user;

    @Column(name = "nic_passport", length = 50)
    private String nicPassport;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String contact;

    @Column(unique = true, nullable = false, length = 150)
    private String email;
}

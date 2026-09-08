package ijse.lk.AutoCareManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private long supplierId;

    @Column(name = "supplier_code", unique = true, nullable = false, length = 20)
    private String supplierCode;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(length = 150, unique = true)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "brn_no", length = 50)
    private String brnNo;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}

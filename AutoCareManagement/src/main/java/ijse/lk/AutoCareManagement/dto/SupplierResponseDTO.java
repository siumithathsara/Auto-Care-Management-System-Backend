package ijse.lk.AutoCareManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierResponseDTO {
    private long supplierId;
    private String supplierCode;
    private String companyName;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String brnNo;
    private boolean isActive;
}

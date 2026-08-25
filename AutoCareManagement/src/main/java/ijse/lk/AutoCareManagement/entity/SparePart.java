package ijse.lk.AutoCareManagement.entity;

import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import ijse.lk.AutoCareManagement.enumeration.PartType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id")
    private long partId;

    @Column(name = "part_code", unique = true, nullable = false, length = 50)
    private String partCode;

    @Column(name = "part_name", nullable = false, length = 100)
    private String partName;

    @Column(name = "brand", length = 50)
    private String brand;

    @Column(name = "cost_price", nullable = false)
    private double costPrice;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "quantity_in_stock", nullable = false)
    private int quantityInStock;

    @Column(name = "reorder_level", nullable = false)
    private int reorderLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_type", nullable = false, length = 30)
    private PartType partType = PartType.SPARE_PART;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_status", nullable = false, length = 30)
    private DataStatus dataStatus;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}

package ijse.lk.AutoCareManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "po_item_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private SparePart sparePart;

    @Column(name = "ordered_qty", nullable = false)
    private int orderedQty;

    @Column(name = "received_qty", nullable = false)
    private int receivedQty = 0;

    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
    private double unitCost;

    @Column(name = "sub_total", nullable = false, precision = 10, scale = 2)
    private double subTotal;
}

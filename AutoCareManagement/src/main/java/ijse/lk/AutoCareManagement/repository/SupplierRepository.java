package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findBySupplierCode(String supplierCode);

    List<Supplier> findByIsActiveTrue();

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<Supplier> findTopByOrderBySupplierIdDesc();

    boolean existsByPhoneAndSupplierCodeNot(String phone, String supplierCode);

    boolean existsByEmailAndSupplierCodeNot(String email, String supplierCode);
}

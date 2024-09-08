package pl.empik.complaintmanager.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Optional<Complaint> findByProductIdAndComplainant(Long productId, String complainant);

    Page<Complaint> findByProductId(Long productId, Pageable pageable);

    Page<Complaint> findByComplainantContainingIgnoreCase(String complainant, Pageable pageable);

    Page<Complaint> findByProductIdAndComplainantContainingIgnoreCase(Long productId, String complainant, Pageable pageable);

}

package pl.empik.complaintmanager.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private String complainant;

    @Column(nullable = false, length = 2)
    private String country;

    @Column(name = "claim_counter", nullable = false)
    private Integer claimCounter;

    public void incrementClaimCounter() {
        this.claimCounter++;
    }

}

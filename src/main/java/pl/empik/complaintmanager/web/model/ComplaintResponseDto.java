package pl.empik.complaintmanager.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ComplaintResponseDto {

    private Long id;
    private Long productId;
    private String content;
    private LocalDateTime creationDate;
    private String complainant;
    private String country;
    private Integer claimCounter;

}

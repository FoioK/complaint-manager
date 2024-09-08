package pl.empik.complaintmanager.web.model;

import lombok.Data;

@Data
public class ComplaintRequestDto {

    private Long productId;
    private String content;
    private String complainant;

}

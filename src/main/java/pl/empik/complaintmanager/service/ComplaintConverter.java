package pl.empik.complaintmanager.service;

import pl.empik.complaintmanager.domain.Complaint;
import pl.empik.complaintmanager.web.model.ComplaintResponseDto;

class ComplaintConverter {

    ComplaintResponseDto toResponseDto(Complaint complaint) {
        return new ComplaintResponseDto(
                complaint.getId(),
                complaint.getProductId(),
                complaint.getContent(),
                complaint.getCreationDate(),
                complaint.getComplainant(),
                complaint.getCountry(),
                complaint.getClaimCounter()
        );
    }

}

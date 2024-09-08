package pl.empik.complaintmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.empik.complaintmanager.domain.Complaint;
import pl.empik.complaintmanager.domain.ComplaintRepository;
import pl.empik.complaintmanager.web.model.ComplaintRequestDto;
import pl.empik.complaintmanager.web.model.ComplaintResponseDto;
import pl.empik.complaintmanager.web.model.UpdateComplaintRequestDto;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final GeoLocationService geoLocationService;

    @Transactional
    public ComplaintResponseDto addComplaint(ComplaintRequestDto complaintRequestDto, String ipAddress) {
        String country = geoLocationService.getCountryFromIp(ipAddress);
        Optional<Complaint> existingComplaint = complaintRepository.findByProductIdAndComplainant(
                complaintRequestDto.getProductId(), complaintRequestDto.getComplainant());

        var mapper = new ComplaintConverter();
        if (existingComplaint.isPresent()) {
            Complaint updatedComplaint = existingComplaint.get();
            updatedComplaint.incrementClaimCounter();
            return mapper.toResponseDto(complaintRepository.save(updatedComplaint));
        } else {
            Complaint newComplaint = new Complaint();
            newComplaint.setProductId(complaintRequestDto.getProductId());
            newComplaint.setContent(complaintRequestDto.getContent());
            newComplaint.setComplainant(complaintRequestDto.getComplainant());
            newComplaint.setCountry(country);
            newComplaint.setCreationDate(LocalDateTime.now());
            newComplaint.setClaimCounter(1);
            return mapper.toResponseDto(complaintRepository.save(newComplaint));
        }
    }

    @Transactional
    public ComplaintResponseDto updateComplaintContent(Long id, UpdateComplaintRequestDto updateComplaintRequestDto) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setContent(updateComplaintRequestDto.getContent());
        Complaint saved = complaintRepository.save(complaint);

        return new ComplaintConverter().toResponseDto(saved);
    }

    public Page<ComplaintResponseDto> getAllComplaints(Long productId, String complainant, Pageable pageable) {
        var mapper = new ComplaintConverter();
        Page<Complaint> complaints;
        if (productId != null && complainant != null) {
            complaints = complaintRepository.findByProductIdAndComplainantContainingIgnoreCase(productId, complainant, pageable);
        } else if (productId != null) {
            complaints = complaintRepository.findByProductId(productId, pageable);
        } else if (complainant != null) {
            complaints = complaintRepository.findByComplainantContainingIgnoreCase(complainant, pageable);
        } else {
            complaints = complaintRepository.findAll(pageable);
        }

        return complaints.map(mapper::toResponseDto);
    }

    public ComplaintResponseDto getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .map(complaint -> new ComplaintConverter().toResponseDto(complaint))
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }

}

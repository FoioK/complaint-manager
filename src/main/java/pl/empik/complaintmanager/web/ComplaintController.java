package pl.empik.complaintmanager.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.empik.complaintmanager.service.ComplaintService;
import pl.empik.complaintmanager.web.model.ComplaintRequestDto;
import pl.empik.complaintmanager.web.model.ComplaintResponseDto;
import pl.empik.complaintmanager.web.model.UpdateComplaintRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/complaints")
class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ComplaintResponseDto addComplaint(@RequestBody ComplaintRequestDto createComplaintDTO,
                                      @RequestHeader("ip") String ip) {
        return complaintService.addComplaint(createComplaintDTO, ip);
    }

    @PutMapping("/{id}")
    ComplaintResponseDto updateComplaintContent(@PathVariable Long id, @RequestBody UpdateComplaintRequestDto updateComplaintDTO) {
        return complaintService.updateComplaintContent(id, updateComplaintDTO);
    }

    @GetMapping
    Page<ComplaintResponseDto> getAllComplaints(@RequestParam(required = false) Long productId,
                                                @RequestParam(required = false) String complainant,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return complaintService.getAllComplaints(productId, complainant, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    ComplaintResponseDto getComplaintById(@PathVariable Long id) {
        return complaintService.getComplaintById(id);
    }

}
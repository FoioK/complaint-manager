package pl.empik.complaintmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.empik.complaintmanager.domain.Complaint;
import pl.empik.complaintmanager.domain.ComplaintRepository;
import pl.empik.complaintmanager.web.model.ComplaintRequestDto;
import pl.empik.complaintmanager.web.model.ComplaintResponseDto;
import pl.empik.complaintmanager.web.model.UpdateComplaintRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private GeoLocationService geoLocationService;

    @InjectMocks
    private ComplaintService complaintService;

    private ComplaintRequestDto complaintRequestDto;
    private Complaint existingComplaint;

    @BeforeEach
    void setUp() {
        complaintRequestDto = new ComplaintRequestDto();
        complaintRequestDto.setProductId(1L);
        complaintRequestDto.setContent("Test content");
        complaintRequestDto.setComplainant("John Doe");

        existingComplaint = new Complaint();
        existingComplaint.setId(1L);
        existingComplaint.setProductId(1L);
        existingComplaint.setContent("Existing content");
        existingComplaint.setComplainant("John Doe");
        existingComplaint.setCountry("US");
        existingComplaint.setCreationDate(LocalDateTime.now());
        existingComplaint.setClaimCounter(1);
    }

    @Test
    void addComplaint_NewComplaint_ShouldCreateNewComplaint() {
        when(geoLocationService.getCountryFromIp(anyString())).thenReturn("US");
        when(complaintRepository.findByProductIdAndComplainant(anyLong(), anyString())).thenReturn(Optional.empty());
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(invocation -> {
            Complaint savedComplaint = invocation.getArgument(0);
            savedComplaint.setId(1L);
            return savedComplaint;
        });

        ComplaintResponseDto result = complaintService.addComplaint(complaintRequestDto, "127.0.0.1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("Test content");
        assertThat(result.getComplainant()).isEqualTo("John Doe");
        assertThat(result.getCountry()).isEqualTo("US");
        assertThat(result.getClaimCounter()).isEqualTo(1);

        verify(complaintRepository).save(any(Complaint.class));
    }

    @Test
    void addComplaint_ExistingComplaint_ShouldIncrementClaimCounter() {
        when(geoLocationService.getCountryFromIp(anyString())).thenReturn("US");
        when(complaintRepository.findByProductIdAndComplainant(anyLong(), anyString())).thenReturn(Optional.of(existingComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(existingComplaint);

        ComplaintResponseDto result = complaintService.addComplaint(complaintRequestDto, "127.0.0.1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getClaimCounter()).isEqualTo(2);

        verify(complaintRepository).save(existingComplaint);
    }

    @Test
    void updateComplaintContent_ExistingComplaint_ShouldUpdateContent() {
        UpdateComplaintRequestDto updateDto = new UpdateComplaintRequestDto();
        updateDto.setContent("Updated content");

        when(complaintRepository.findById(1L)).thenReturn(Optional.of(existingComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(existingComplaint);

        ComplaintResponseDto result = complaintService.updateComplaintContent(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("Updated content");

        verify(complaintRepository).save(existingComplaint);
    }

    @Test
    void updateComplaintContent_NonExistingComplaint_ShouldThrowException() {
        UpdateComplaintRequestDto updateDto = new UpdateComplaintRequestDto();
        updateDto.setContent("Updated content");

        when(complaintRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> complaintService.updateComplaintContent(1L, updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Complaint not found");

        verify(complaintRepository, never()).save(any(Complaint.class));
    }

    @Test
    void getAllComplaints_NoFilters_ShouldReturnAllComplaints() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Complaint> complaintsPage = new PageImpl<>(List.of(existingComplaint));

        when(complaintRepository.findAll(pageable)).thenReturn(complaintsPage);

        Page<ComplaintResponseDto> result = complaintService.getAllComplaints(null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(1L);

        verify(complaintRepository).findAll(pageable);
    }

    @Test
    void getAllComplaints_WithProductIdFilter_ShouldReturnFilteredComplaints() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Complaint> complaintsPage = new PageImpl<>(List.of(existingComplaint));

        when(complaintRepository.findByProductId(1L, pageable)).thenReturn(complaintsPage);

        Page<ComplaintResponseDto> result = complaintService.getAllComplaints(1L, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getProductId()).isEqualTo(1L);

        verify(complaintRepository).findByProductId(1L, pageable);
    }

    @Test
    void getAllComplaints_WithComplainantFilter_ShouldReturnFilteredComplaints() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Complaint> complaintsPage = new PageImpl<>(List.of(existingComplaint));

        when(complaintRepository.findByComplainantContainingIgnoreCase("John", pageable)).thenReturn(complaintsPage);

        Page<ComplaintResponseDto> result = complaintService.getAllComplaints(null, "John", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getComplainant()).isEqualTo("John Doe");

        verify(complaintRepository).findByComplainantContainingIgnoreCase("John", pageable);
    }

    @Test
    void getAllComplaints_WithBothFilters_ShouldReturnFilteredComplaints() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Complaint> complaintsPage = new PageImpl<>(List.of(existingComplaint));

        when(complaintRepository.findByProductIdAndComplainantContainingIgnoreCase(1L, "John", pageable)).thenReturn(complaintsPage);

        Page<ComplaintResponseDto> result = complaintService.getAllComplaints(1L, "John", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getProductId()).isEqualTo(1L);
        assertThat(result.getContent().getFirst().getComplainant()).isEqualTo("John Doe");

        verify(complaintRepository).findByProductIdAndComplainantContainingIgnoreCase(1L, "John", pageable);
    }

    @Test
    void getComplaintById_ExistingComplaint_ShouldReturnComplaint() {
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(existingComplaint));

        ComplaintResponseDto result = complaintService.getComplaintById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(complaintRepository).findById(1L);
    }

    @Test
    void getComplaintById_NonExistingComplaint_ShouldThrowException() {
        when(complaintRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> complaintService.getComplaintById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Complaint not found");

        verify(complaintRepository).findById(1L);
    }

}

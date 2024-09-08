package pl.empik.complaintmanager.service;

import org.junit.jupiter.api.Test;
import pl.empik.complaintmanager.domain.Complaint;
import pl.empik.complaintmanager.web.model.ComplaintResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ComplaintConverterTest {

    private final ComplaintConverter complaintConverter = new ComplaintConverter();

    @Test
    void toResponseDto_ShouldConvertCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        Complaint complaint = new Complaint();
        complaint.setId(1L);
        complaint.setProductId(123L);
        complaint.setContent("Test content");
        complaint.setCreationDate(now);
        complaint.setComplainant("John Doe");
        complaint.setCountry("US");
        complaint.setClaimCounter(2);

        ComplaintResponseDto result = complaintConverter.toResponseDto(complaint);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductId()).isEqualTo(123L);
        assertThat(result.getContent()).isEqualTo("Test content");
        assertThat(result.getCreationDate()).isEqualTo(now);
        assertThat(result.getComplainant()).isEqualTo("John Doe");
        assertThat(result.getCountry()).isEqualTo("US");
        assertThat(result.getClaimCounter()).isEqualTo(2);
    }

}

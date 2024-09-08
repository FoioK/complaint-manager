package pl.empik.complaintmanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.empik.complaintmanager.domain.Complaint;
import pl.empik.complaintmanager.domain.ComplaintRepository;
import pl.empik.complaintmanager.web.model.ComplaintRequestDto;
import pl.empik.complaintmanager.web.model.UpdateComplaintRequestDto;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Import(WireMockConfig.class)
class ComplaintIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        complaintRepository.deleteAll();
        wireMockServer.resetAll();
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching("/[^/]+/country"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("US")));
    }

    @Test
    void shouldCreateComplaint() throws Exception {
        ComplaintRequestDto requestDto = new ComplaintRequestDto();
        requestDto.setProductId(1L);
        requestDto.setContent("Test complaint");
        requestDto.setComplainant("John Doe");

        mockMvc.perform(post("/api/complaints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("ip", "127.0.0.1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.content", is("Test complaint")))
                .andExpect(jsonPath("$.complainant", is("John Doe")))
                .andExpect(jsonPath("$.country", is("US")))
                .andExpect(jsonPath("$.claimCounter", is(1)));
    }

    @Test
    void shouldIncrementClaimCounterForDuplicateComplaint() throws Exception {
        Complaint complaint = new Complaint();
        complaint.setProductId(1L);
        complaint.setContent("Initial complaint");
        complaint.setComplainant("John Doe");
        complaint.setCountry("US");
        complaint.setCreationDate(LocalDateTime.now());
        complaint.setClaimCounter(1);
        complaintRepository.save(complaint);

        ComplaintRequestDto requestDto = new ComplaintRequestDto();
        requestDto.setProductId(1L);
        requestDto.setContent("Duplicate complaint");
        requestDto.setComplainant("John Doe");

        mockMvc.perform(post("/api/complaints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("ip", "127.0.0.1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(complaint.getId().intValue())))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.content", is("Initial complaint")))
                .andExpect(jsonPath("$.complainant", is("John Doe")))
                .andExpect(jsonPath("$.country", is("US")))
                .andExpect(jsonPath("$.claimCounter", is(2)));
    }

    @Test
    void shouldUpdateComplaintContent() throws Exception {
        Complaint complaint = new Complaint();
        complaint.setProductId(1L);
        complaint.setContent("Initial content");
        complaint.setComplainant("John Doe");
        complaint.setCountry("US");
        complaint.setCreationDate(LocalDateTime.now());
        complaint.setClaimCounter(1);
        complaint = complaintRepository.save(complaint);

        UpdateComplaintRequestDto updateDto = new UpdateComplaintRequestDto();
        updateDto.setContent("Updated content");

        mockMvc.perform(put("/api/complaints/" + complaint.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(complaint.getId().intValue())))
                .andExpect(jsonPath("$.content", is("Updated content")));
    }

    @Test
    void shouldRetrieveComplaintById() throws Exception {
        Complaint complaint = new Complaint();
        complaint.setProductId(1L);
        complaint.setContent("Test content");
        complaint.setComplainant("John Doe");
        complaint.setCountry("US");
        complaint.setCreationDate(LocalDateTime.now());
        complaint.setClaimCounter(1);
        complaint = complaintRepository.save(complaint);

        mockMvc.perform(get("/api/complaints/" + complaint.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(complaint.getId().intValue())))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.content", is("Test content")))
                .andExpect(jsonPath("$.complainant", is("John Doe")))
                .andExpect(jsonPath("$.country", is("US")))
                .andExpect(jsonPath("$.claimCounter", is(1)));
    }

    @Test
    void shouldRetrieveAllComplaintsWithPagination() throws Exception {
        for (int i = 0; i < 15; i++) {
            Complaint complaint = new Complaint();
            complaint.setProductId(1L);
            complaint.setContent("Complaint " + i);
            complaint.setComplainant("John Doe");
            complaint.setCountry("US");
            complaint.setCreationDate(LocalDateTime.now());
            complaint.setClaimCounter(1);
            complaintRepository.save(complaint);
        }

        mockMvc.perform(get("/api/complaints?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements", is(15)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    void shouldFilterComplaintsByProductIdAndComplainant() throws Exception {
        Complaint complaint1 = createComplaint(1L, "John Doe");
        createComplaint(2L, "Jane Doe");

        mockMvc.perform(get("/api/complaints?productId=1&complainant=John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(complaint1.getId().intValue())));
    }

    private Complaint createComplaint(Long productId, String complainant) {
        Complaint complaint = new Complaint();
        complaint.setProductId(productId);
        complaint.setContent("Test content");
        complaint.setComplainant(complainant);
        complaint.setCountry("US");
        complaint.setCreationDate(LocalDateTime.now());
        complaint.setClaimCounter(1);
        return complaintRepository.save(complaint);
    }

}
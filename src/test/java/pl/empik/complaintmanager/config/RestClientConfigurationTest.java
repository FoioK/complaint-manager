package pl.empik.complaintmanager.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class RestClientConfigurationTest {

    private final RestClientConfiguration configuration = new RestClientConfiguration();

    @Test
    void restClient_ShouldCreateRestClientWithCorrectBaseUrl() {
        RestClient restClient = configuration.geoLocationClient(new GeoLocationConfig());

        assertThat(restClient).isNotNull();
    }

}

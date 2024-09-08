package pl.empik.complaintmanager.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
class RestClientConfiguration {

    @Bean
    RestClient geoLocationClient(GeoLocationConfig config) {
        var requestFactorySettings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(config.getConnectionTimeout()))
                .withReadTimeout(Duration.ofSeconds(config.getReadTimeout()));

        return RestClient.builder()
                .requestFactory(ClientHttpRequestFactories.get(requestFactorySettings))
                .baseUrl(config.getUrl())
                .build();
    }

}

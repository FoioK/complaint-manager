package pl.empik.complaintmanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "geolocation")
public class GeoLocationConfig {

    private String url;
    private int connectionTimeout;
    private int readTimeout;

}

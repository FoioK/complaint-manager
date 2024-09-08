package pl.empik.complaintmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class GeoLocationService {

    private final RestClient restClient;

    public String getCountryFromIp(String ipAddress) {
        return restClient.get()
                .uri("/{ipAddress}/country", ipAddress)
                .retrieve()
                .toEntity(String.class)
                .getBody();
    }

}

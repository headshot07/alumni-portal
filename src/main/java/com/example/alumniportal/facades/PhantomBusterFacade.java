package com.example.alumniportal.facades;

import com.example.alumniportal.configurations.RestTemplateConfig;
import com.example.alumniportal.dto.AlumniDetailsRequestDTO;
import com.example.alumniportal.dto.AlumniDetailsResponseDTO;
import com.example.alumniportal.exceptions.PhantomBusterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static com.example.alumniportal.constants.ApplicationConstants.AUTHORIZATION;

@Slf4j
@Component
public class PhantomBusterFacade {

    @Value("${phantomBuster.baseUrl}")
    private String phantomBusterBaseUrl;

    @Value("${phantomBuster.appToken}")
    private String phantomBusterAppToken;

    @Value("${phantomBuster.connect.timeout}")
    private int phantomBusterConnectTimeout;

    @Value("${phantomBuster.read.timeout}")
    private int phantomBusterReadTimeout;

    private RestTemplate restTemplate;

    public PhantomBusterFacade(RestTemplateConfig restTemplateConfig) {
        this.restTemplate = restTemplateConfig.createCustomRestTemplate(phantomBusterConnectTimeout, phantomBusterReadTimeout);
    }

    @Retryable(
            retryFor = {RestClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public AlumniDetailsResponseDTO fetchAlumniDetailsFromPhantomBuster(AlumniDetailsRequestDTO alumniDetailsRequestDTO) {
        String apiUrl = phantomBusterBaseUrl + "/api/alumni/search";
        log.info("Attempting to fetch data from {}", apiUrl);

        HttpEntity<String> requestEntity = buildRequestEntity(alumniDetailsRequestDTO);

        ResponseEntity<AlumniDetailsResponseDTO> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                AlumniDetailsResponseDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {
            log.info("Successfully fetched {} alumni records from PhantomBuster.", response.getBody().getData());
            return response.getBody();
        } else {
            log.error("Failed to fetch data from PhantomBuster. Status: {}", response.getStatusCode());
            throw new PhantomBusterException("No alumni records found for the given criteria.");
        }
    }

    @Recover
    public AlumniDetailsResponseDTO handleRetryFailure(RestClientException e, AlumniDetailsRequestDTO requestDTO) {
        log.error("All retry attempts exhausted. Unable to fetch data from PhantomBuster. Error: {}", e.getMessage());
        throw new PhantomBusterException("Max retries reached while trying to fetch alumni data from PhantomBuster." + e.getMessage());
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, "Bearer " + phantomBusterAppToken);
        return headers;
    }

    private HttpEntity<String> buildRequestEntity(AlumniDetailsRequestDTO requestDTO) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestDTO);
            HttpHeaders headers = buildHeaders();
            return new HttpEntity<>(jsonBody, headers);
        } catch (JsonProcessingException e) {
            throw new PhantomBusterException("Error serializing request body." + e.getMessage());
        }
    }

}

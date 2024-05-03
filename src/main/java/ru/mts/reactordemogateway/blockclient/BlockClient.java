package ru.mts.reactordemogateway.blockclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlockClient {
    private final RestTemplate template;

    public String blockRequest(String requestName) {
        log.info("send block request: {}", requestName);
        var response = template.exchange(new RequestEntity<>(HttpMethod.GET, getUrl()),
                                         String.class);
        return response.getBody();
    }

    private URI getUrl() {
        try {
            return new URI("http://localhost:8081/block");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

package ru.mts.reactordemogateway.reactiveClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReactiveClient {
    private final WebClient webClient;

    public Mono<String> sendReactiveRequest(String requestName) {
        log.info("send request: '{}'", requestName );
        return webClient.method(HttpMethod.GET)
            .uri("/reactive")
            .retrieve()
            .bodyToMono(String.class)
            .map(Instant::parse)
            .map(Instant::getNano)
            .map(Object::toString);
    }
}

package ru.mts.reactordemogateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.mts.reactordemogateway.blockclient.BlockClient;
import ru.mts.reactordemogateway.reactiveClient.ReactiveClient;
import ru.mts.reactordemogateway.service.StringService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ControllerTest {

    private WebTestClient webTestClient;

    @MockBean
    private ReactiveClient reactiveClient;

    @MockBean
    private BlockClient blockClient;
    @MockBean
    private StringService stringService;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(new Controller(reactiveClient, blockClient, stringService)).build();
    }



    @Test
    void should_two_block_request_and_two_reactive() {
        when(reactiveClient.sendReactiveRequest("reactive request if oneVariable == 1")).thenReturn(Mono.just("reactive request 1"));
        when(reactiveClient.sendReactiveRequest("reactive request if oneVariable == 1 (2)")).thenReturn(Mono.just("reactive request 2"));

        when(blockClient.blockRequest("block request if twoVariable == 1")).thenReturn("block request");
        when(blockClient.blockRequest("block request if oneVariable == 1 (2)")).thenReturn("block request 2");

        webTestClient.get()
            .uri("/gateway/1/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);

        verify(reactiveClient).sendReactiveRequest("reactive request if oneVariable == 1");
        verify(reactiveClient).sendReactiveRequest("reactive request if oneVariable == 1 (2)");
        verify(reactiveClient, never()).sendReactiveRequest("reactive request if empty");
        verify(blockClient).blockRequest("block request if twoVariable == 1");
        verify(blockClient).blockRequest("block request if oneVariable == 1 (2)");

    }

    @Test
    void testGetInstantOneVariableIsOneTwoVariableIsNotOne() {
        when(reactiveClient.sendReactiveRequest("reactive request if oneVariable == 1")).thenReturn(Mono.just("reactive request 1"));
        when(reactiveClient.sendReactiveRequest("reactive request if oneVariable == 1 (2)")).thenReturn(Mono.just("reactive request 2"));
        when(blockClient.blockRequest("block request if oneVariable == 1 (2)")).thenReturn("block request 2");

        webTestClient.get()
            .uri("/gateway/1/2")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);

        verify(reactiveClient).sendReactiveRequest("reactive request if oneVariable == 1");
        verify(reactiveClient).sendReactiveRequest("reactive request if oneVariable == 1 (2)");
        verify(blockClient).blockRequest("block request if oneVariable == 1 (2)");
        verify(reactiveClient, never()).sendReactiveRequest("reactive request if empty");
    }

    @Test
    void should_work_switch_if_empty() {
        when(reactiveClient.sendReactiveRequest("reactive request if empty"))
            .thenReturn(Mono.just("reactive request"));

        webTestClient.get()
            .uri("/gateway/2/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);

        verify(reactiveClient).sendReactiveRequest("reactive request if empty");
    }

    @Test
    void should_return_error_string_if_switch_if_empty() {
        when(reactiveClient.sendReactiveRequest("reactive request if oneVariable == 1")).thenReturn(Mono.error(new RuntimeException()));
        when(stringService.getErrorString()).thenReturn("error_string");


        webTestClient.get()
            .uri("/gateway/1/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .isEqualTo("error_string");

        verify(reactiveClient).sendReactiveRequest("reactive request if oneVariable == 1");
        verify(reactiveClient, never()).sendReactiveRequest("reactive request if oneVariable == 1 (2)");
        verify(blockClient, never()).blockRequest("block request if oneVariable == 1 (2)");
        verify(reactiveClient, never()).sendReactiveRequest("reactive request if empty");
        verify(stringService).getErrorString();
    }
}
package ru.mts.reactordemogateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.mts.reactordemogateway.blockclient.BlockClient;
import ru.mts.reactordemogateway.reactiveClient.ReactiveClient;
import ru.mts.reactordemogateway.service.StringService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Controller {

    private final ReactiveClient reactiveClient;
    private final BlockClient blockClient;
    private final StringService stringService;

    @GetMapping("/gateway/{oneVariable}/{twoVariable}")
    public Mono<String> getInstant(@PathVariable("oneVariable") Integer oneVariable,
                                   @PathVariable("twoVariable") Integer twoVariable) {
        return Mono.just(oneVariable)
            .flatMap(integer -> {
                if (integer.equals(1)) {
                    return reactiveClient.sendReactiveRequest("reactive request if oneVariable == 1")
                        .flatMap(s -> {
                            if (twoVariable.equals(1)) {
                                return Mono.just(blockClient.blockRequest("block request if twoVariable == 1"));
                            } else {
                                return Mono.just(stringService.getJustString(s));
                            }
                        });
                } else {
                    return Mono.empty();
                }
            })
            .flatMap(s -> reactiveClient.sendReactiveRequest("reactive request if oneVariable == 1 (2)"))
            .flatMap(s -> Mono.just(blockClient.blockRequest("block request if oneVariable == 1 (2)")))
            .switchIfEmpty(Mono.defer(() -> reactiveClient.sendReactiveRequest("reactive request if empty")))
//            .switchIfEmpty(reactiveClient.sendReactiveRequest("reactive request if empty"))
            .onErrorResume(Exception.class , exception -> Mono.just(stringService.getErrorString()));
    }

}

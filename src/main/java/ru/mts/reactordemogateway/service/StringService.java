package ru.mts.reactordemogateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StringService {

    public String getErrorString() {
        log.info("get string error");
        return "error";
    }

    public String getJustString(String s) {
        log.info("getJustString methode if twoVariable != 1 : " + s);
        return "just string";
    }
}

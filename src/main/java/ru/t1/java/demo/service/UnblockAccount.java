package ru.t1.java.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UnblockAccount {
    private static final Logger log = LoggerFactory.getLogger(UnblockAccount.class);
    private final WebClient webClient;

    @Autowired
    public UnblockAccount(WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.build();
    }


    public Mono<String> requestAccountUnlock(Long transactionId) {
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("api/v1/account/unlock")
                        .queryParam("transactionId", transactionId)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Account unlock response: {}", response))
                .doOnError(error -> log.error("Error during account unlock request: {}", error.getMessage()));
    }

    public String sendUnlockRequest(Long transactionId) {
        return requestAccountUnlock(transactionId)
                .doOnError(error -> log.error("Error during account unlock request: {}", error.getMessage()))
                .block();

    }
}


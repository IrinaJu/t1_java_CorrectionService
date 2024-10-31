package ru.t1.java.demo.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface ErrorTransactionHandler {
    void processFailedTransactionSendAttempts();

    @Scheduled(fixedDelayString = "${t1.kafka.resend-interval}")
    void resendFailedTransactions();
}

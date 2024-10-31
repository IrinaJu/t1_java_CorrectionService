package ru.t1.java.demo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.service.AccountUnlockService;

@Component
public class KafkaTransactionErrorConsumer {


    private static final Logger log = LoggerFactory.getLogger(KafkaTransactionErrorConsumer.class);
    private final AccountUnlockService accountUnlockService;

    @Autowired
    public KafkaTransactionErrorConsumer(AccountUnlockService accountUnlockService) {
        this.accountUnlockService = accountUnlockService;
    }

    @KafkaListener(id = "${t1.kafka.consumer.group-id}",
            topics = "${t1_demo_client_transaction_errors}",
            containerFactory = "transactionDtoKafkaListener"
    )
    public void handleTransactionError(@Payload Long transactionId,
                                       Acknowledgment acknowledgment) {
        try {
            log.info("Начало обработки ошибки транзакции с id {}", transactionId);
            processTransactionError(transactionId);
            acknowledgment.acknowledge();
            log.info("Транзакция с id {} успешно обработана", transactionId);
        } catch (Exception e) {
            log.error("Ошибка при обработке транзакции с id {}", transactionId, e);
            }
    }

    private void processTransactionError(Long transactionId) {
        accountUnlockService.processTransactionForUnlock(transactionId);
    }
}

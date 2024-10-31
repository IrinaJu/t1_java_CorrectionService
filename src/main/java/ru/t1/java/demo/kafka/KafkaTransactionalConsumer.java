package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDTO;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component

public class KafkaTransactionalConsumer {
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @KafkaListener(id = "${t1.kafka.consumer.group-id}",
            topics = "${t1.kafka.topic.transactions}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<TransactionDTO> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений из топика {}", topic);

        try {
            for (TransactionDTO transactionDto : messageList) {
                Transaction transaction = transactionMapper.toEntity(transactionDto);
                Account account = accountMapper.toEntity(transactionDto.getAccountDto());

                accountService.registerAccount(account);

                transactionService.registerTransaction(transaction);
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения: {}", e.getMessage());

        } finally {
            ack.acknowledge();
        }

        log.debug("Transaction consumer: записи обработаны");
    }
}



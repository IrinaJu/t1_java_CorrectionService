package ru.t1.java.demo.service.impl;

import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDTO;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.ErrorTransactionHandler;

import java.util.List;

@Service
public class ErrorTransactionHandlerImpl implements ErrorTransactionHandler {


    private static final Logger log = LoggerFactory.getLogger(ErrorTransactionHandler.class);
    private static final String TRANSACTION_DOES_NOT_EXIST_MESSAGE = "Error transaction does not exist in the database";
    private static final String ERROR_TRANSACTION_SENT_MESSAGE = "Error transactions sent for processing";

    @Value("${t1_demo_client_transactions}")
    private String transactionTopic;

    private final KafkaTemplate<String, TransactionDTO> kafkaTemplate;
    private final TransactionRepository transactionRepository;

    @Autowired
    public ErrorTransactionHandlerImpl(KafkaTemplate<String, TransactionDTO> kafkaTemplate, TransactionRepository transactionRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void processFailedTransactionSendAttempts() {
        List<Transaction> failedTransactions = transactionRepository.findAllByIsError(true);

        if (failedTransactions.isEmpty()) {
            log.info(TRANSACTION_DOES_NOT_EXIST_MESSAGE);
            return;
        }

        for (Transaction transaction : failedTransactions) {
            try {
                sendTransactionToKafka(transaction);
                log.info("Successfully sent transaction with ID: {}", transaction.getId());
            } catch (Exception e) {
                log.error("Failed to send transaction with ID: {}. Error: {}", transaction.getId(), e.getMessage());

            }
        }
    }

    @Override
    @Scheduled(fixedDelayString = "${t1.kafka.resend-interval}")
    public void resendFailedTransactions() {
        List<Transaction> failedTransactions = transactionRepository.findAllByIsError(true);
        if (failedTransactions.isEmpty()) {
            log.info(TRANSACTION_DOES_NOT_EXIST_MESSAGE);
            return;
        }

        failedTransactions.forEach(this::sendTransactionToKafka);
        log.info(ERROR_TRANSACTION_SENT_MESSAGE);
    }

    private void sendTransactionToKafka(Transaction transaction) {
        TransactionDTO transactionDto = convertToDto(transaction);
        kafkaTemplate.send(transactionTopic, transactionDto);
    }

    private TransactionDTO convertToDto(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getAccount().getId(),
                transaction.getType()
        );
    }
}



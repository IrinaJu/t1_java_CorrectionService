package ru.t1.java.demo.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountUnlockService;
import ru.t1.java.demo.service.UnblockAccount;

@Service
public class AccountUnlockServiceImpl implements AccountUnlockService {



    private static final Logger log = LoggerFactory.getLogger(AccountUnlockService.class);
    private static final String UNLOCK_SUCCESS_RESPONSE = "Account successfully unlocked";

    private final UnblockAccount  unblockAccount ;
    private final TransactionRepository transactionRepository;

    @Autowired
    public AccountUnlockServiceImpl(UnblockAccount accountUnlockClient, TransactionRepository transactionRepository) {
        this.unblockAccount = accountUnlockClient;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void processTransactionForUnlock(Long transactionId) {
        try {
            String unlockResponse = sendUnlockRequest(transactionId);
            handleUnlockResponse(transactionId, unlockResponse);
        } catch (Exception e) {
            log.error("Error processing transaction for account unlock: {}", transactionId, e);

        }
    }

    private String sendUnlockRequest(Long transactionId) {
        return unblockAccount.sendUnlockRequest(transactionId);
    }

    private void handleUnlockResponse(Long transactionId, String unlockResponse) {
        if (UNLOCK_SUCCESS_RESPONSE.equals(unlockResponse)) {
            deleteTransactionIfUnlocked(transactionId);
        } else {
            log.warn("Account unlock failed for transaction ID: {}", transactionId);
        }
    }

    private void deleteTransactionIfUnlocked(Long transactionId) {
        transactionRepository.deleteById(transactionId);
        log.info("Transaction {} deleted after successful account unlock.", transactionId);
    }
}


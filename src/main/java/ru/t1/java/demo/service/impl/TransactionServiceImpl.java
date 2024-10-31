package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Override
    public List<Transaction> registerTransactions(List<Transaction> transactions) {
        List<Transaction> savedTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {

            Transaction savedTransaction = transactionRepository.save(transaction);
            savedTransactions.add(savedTransaction);
        }
        return savedTransactions;
    }

    @Override
    public Transaction registerTransaction(Transaction transaction) {

        return transactionRepository.save(transaction);
    }


}

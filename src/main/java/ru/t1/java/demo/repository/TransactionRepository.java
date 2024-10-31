package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Transaction;

import java.util.List;
import java.util.Set;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Set<Transaction> findAllByHasError(Boolean hasError);

    List<Transaction> findAllByIsError(boolean b);
}
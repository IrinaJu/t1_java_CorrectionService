package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.web.CheckWebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor

public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    private final CheckWebClient checkWebClient;

    @Override
    public List<Account> registerAccounts(List<Account> accounts) {
        List<Account> savedAccounts = new ArrayList<>();
        for (Account account : accounts) {
            Optional<CheckResponse> check = checkWebClient.check(account.getClient().getId());
            check.ifPresent(checkResponse -> {
                if (!checkResponse.getBlocked()) {
                    Account saved = repository.save(account);

                    savedAccounts.add(saved);
                }
            });
        }
        return savedAccounts;
    }

    @Override
    public Account registerAccount(Account account) {
        Optional<CheckResponse> check = checkWebClient.check(account.getClient().getId());
        if (check.isPresent() && !check.get().getBlocked()) {
            return repository.save(account);
        } else {

            throw new IllegalStateException("Client is blocked and cannot be registered");
        }
    }
}






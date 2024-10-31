package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.Account;


import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class TransactionDTO {

    private AccountDto accountDto;
    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("account_id")
    private Account account;
    public AccountDto getAccountDto() {
        return accountDto;
    }

    public void setAccountDto(AccountDto accountDto) {
        this.accountDto = accountDto;
    }

}


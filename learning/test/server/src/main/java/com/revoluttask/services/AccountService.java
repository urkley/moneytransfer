package com.revoluttask.services;

import com.revoluttask.exceptions.TransferException;
import com.revoluttask.models.Account;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public interface AccountService {

    Account createNewAccount(BigDecimal balance);

    Account getAccount(UUID accountId);

    void deleteAccount(UUID accountId);

    void transferMoneyFromAccount(BigDecimal transferAmount, UUID fromAccountId, UUID toAccountId)  throws TransferException;

    Set<Account> getAllAccounts();
}

package com.revoluttask.services;

import com.google.inject.Inject;
import com.revoluttask.exceptions.TransferException;
import com.revoluttask.models.Account;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class AccountService {

    private final ConcurrentMap<UUID, Account> idToAccount;
    @Inject
    private DataLockService<UUID> dataLockService;

    private AccountService() {
        this.idToAccount = new ConcurrentHashMap<>();
    }

    public Account createNewAccount(BigDecimal balance) {
        UUID accountId = UUID.randomUUID();
        final Account newAccount = new Account(accountId, balance);
        idToAccount.put(accountId, newAccount);
        return newAccount;
    }

    public Account getAccount(UUID accountId) {
        return idToAccount.get(accountId);
    }

    public void deleteAccount(UUID accountId) {
        idToAccount.remove(accountId);
    }

    public void transferMoneyFromAccount(BigDecimal transferAmount, UUID fromAccountId, UUID toAccountId) throws TransferException {
        Account fromAccount = idToAccount.get(fromAccountId);
        Account toAccount = idToAccount.get(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new TransferException("Not enough data to transfer money");
        }

        final BigDecimal fromAccountResultBalance = fromAccount.getBalance().subtract(transferAmount);
        if (BigDecimal.ZERO.compareTo(fromAccountResultBalance) > 0) {
            throw new TransferException("Not enough money to transfer");
        }

        try {
            final boolean locked = Stream.of(fromAccountId, toAccountId)
                    .sorted()
                    .map(dataLockService::tryLock)
                    .reduce(Boolean::logicalAnd)
                    .orElseThrow(IllegalStateException::new);
            if (locked) {
                idToAccount.put(fromAccountId, new Account(fromAccountId, fromAccountResultBalance));
                idToAccount.put(toAccountId, new Account(toAccountId, toAccount.getBalance().add(transferAmount)));
            }
        } finally {
            Stream.of(fromAccountId, toAccountId)
                    .sorted()
                    .forEach(dataLockService::unlock);
        }
    }

    public Set<Account> getAllAccounts() {
        return new HashSet<>(idToAccount.values());
    }
}

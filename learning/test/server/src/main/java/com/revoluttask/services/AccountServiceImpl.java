package com.revoluttask.services;

import com.revoluttask.exceptions.TransferException;
import com.revoluttask.models.Account;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class AccountServiceImpl implements AccountService {

    private static final ConcurrentMap<UUID, Account> idToAccount = new ConcurrentHashMap<>();
    private static final DataLockService<UUID> dataLockService = new DataLockServiceImpl<>();

    @Override
    public Account createNewAccount(BigDecimal balance) {
        UUID accountId = UUID.randomUUID();
        final Account newAccount = new Account(accountId, balance);
        idToAccount.put(accountId, newAccount);
        return newAccount;
    }

    @Override
    public Account getAccount(UUID accountId) {
        return idToAccount.get(accountId);
    }

    @Override
    public void deleteAccount(UUID accountId) {
        idToAccount.remove(accountId);
    }

    @Override
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

    @Override
    public Set<Account> getAllAccounts() {
        return new HashSet<>(idToAccount.values());
    }
}

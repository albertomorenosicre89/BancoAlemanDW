package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    private Account updateAccount(Account accountWithNewBalance) {
        return accounts.compute(accountWithNewBalance.getAccountId(), (k,account) -> account = accountWithNewBalance);
    }

    @Override
    public boolean updateAccounts(Account accountFrom,Account accountTo) {
        Iterator<String> itr = accounts.keySet().iterator();

        while (itr.hasNext()) {
            String key = itr.next();
            if(key.equals(accountFrom.getAccountId())){
                updateAccount(accountFrom);
            }else if(key.equals(accountTo.getAccountId())){
                updateAccount(accountTo);
            }
        }
        return true;
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

}

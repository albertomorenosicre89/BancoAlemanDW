package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    //https://www.baeldung.com/concurrenthashmap-reading-and-writing
    private Account updateAccount(Account accountWithNewBalance) {
        return accounts.compute(accountWithNewBalance.getAccountId(), (k,account) -> account = accountWithNewBalance);
    }

    @Override
    public String changeBalances(String accountFromId , String accountToId , Double amount) {
        Iterator<String> itr = accounts.keySet().iterator();
        BigDecimal amountBD = BigDecimal.valueOf(amount);
        Account accountFrom = null;
        Account accountTo = null;
        while (itr.hasNext()) {
            String key = itr.next();
            if(key.equals(accountFromId)){
                accountFrom = this.getAccount(accountFromId);
                if(accountFrom.getBalance().compareTo(amountBD)<0){
                    return "The amount "+amount+" is lower than the current balance in the from account which has "+accountFrom.getBalance();
                }
                accountFrom.setBalance(accountFrom.getBalance().subtract(amountBD));
            }else if(key.equals(accountToId)){
                accountTo = this.getAccount(accountToId);
                accountTo.setBalance(accountTo.getBalance().add(amountBD));
            }
            if(accountFrom!=null && accountFrom.getBalance().compareTo(amountBD)>=0 && accountTo!=null){
                updateAccount(accountFrom);
                updateAccount(accountTo);
                return null;
            }
        }
        if(accountFrom==null){
            return "Account from with id " + accountFromId + " not found!";
        }
        if(accountTo==null){
            return "Account to with id " + accountToId + " not found!";
        }
        return null;
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

}

package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Getter
  private final NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public String transferMoney(String accountFromId , String accountToId , Double amount) {
    Account accountFrom = this.accountsRepository.getAccount(accountFromId);
    if(accountFrom==null){
      return "Account from with id " + accountFromId + " not found!";
    }
    Account accountTo = this.accountsRepository.getAccount(accountToId);
    if(accountTo==null){
      return "Account to with id " + accountToId + " not found!";
    }
    BigDecimal amountBD = BigDecimal.valueOf(amount);
    if(accountFrom.getBalance().compareTo(amountBD)>=0){
      accountFrom.setBalance(accountFrom.getBalance().subtract(amountBD));
      accountTo.setBalance(accountTo.getBalance().add(amountBD));
      this.accountsRepository.updateAccounts(accountFrom,accountTo);
      notificationService.notifyAboutTransfer(accountFrom, "You have done a transfer from this account to the account ID "+accountToId+ " with an amount of "+amount);
      notificationService.notifyAboutTransfer(accountTo, "You have received a transfer in this account from the account ID "+accountFromId+ " with an amount of "+amount);
      return null;
    }else{
      return "The amount "+amount+" is lower than the current balance in the from account which has "+accountFrom.getBalance();
    }
  }


}

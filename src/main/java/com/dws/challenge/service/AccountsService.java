package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    String result = this.accountsRepository.changeBalances(accountFromId , accountToId , amount);
    if(result==null){
      Account accountFrom = this.getAccount(accountFromId);
      Account accountTo = this.getAccount(accountToId);
      notificationService.notifyAboutTransfer(accountFrom, "You have done a transfer from this account to the account ID "+accountToId+ " with an amount of "+amount);
      notificationService.notifyAboutTransfer(accountTo, "You have received a transfer in this account from the account ID "+accountFromId+ " with an amount of "+amount);
      return null;
    }else{
      return result;
    }
  }


}

package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.AccountsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  void addAccount() {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }

  @Test
  void transferMoney_success() {
    String uniqueIdFrom = "IdFrom-1";
    Account accountFrom = new Account(uniqueIdFrom);
    accountFrom.setBalance(BigDecimal.valueOf(3.4));
    String uniqueIdTo = "IdTo-2";
    Account accountTo = new Account(uniqueIdTo);
    this.accountsService.createAccount(accountFrom);
    this.accountsService.createAccount(accountTo);
    String result = this.accountsService.transferMoney(accountFrom.getAccountId(), accountTo.getAccountId(), 3.4);
    assertNull(result);
  }

  @Test
  void transferMoney_accountFromNotExists() {
    String uniqueIdTo = "Id-3" ;
    Account accountTo = new Account(uniqueIdTo);
    this.accountsService.createAccount(accountTo);
    String result = this.accountsService.transferMoney("1", accountTo.getAccountId(), 3.4);
    assertEquals("Account from with id 1 not found!" , result);
  }

  @Test
  void transferMoney_accountToNotExists() {
    String uniqueIdFrom = "Id-4";
    Account accountFrom = new Account(uniqueIdFrom,new BigDecimal(1000));
    this.accountsService.createAccount(accountFrom);
    String result = this.accountsService.transferMoney(accountFrom.getAccountId(), "2", 3.4);
    assertEquals("Account to with id 2 not found!" , result);
  }

  @Test
  void transferMoney_notEnoughMoney() {
    String uniqueIdFrom = "IdFrom-5" ;
    Account accountFrom = new Account(uniqueIdFrom);
    String uniqueIdTo = "IdTo-6" ;
    accountFrom.setBalance(BigDecimal.valueOf(3.4));
    Account accountTo = new Account(uniqueIdTo);
    this.accountsService.createAccount(accountFrom);
    this.accountsService.createAccount(accountTo);
    String result = this.accountsService.transferMoney(accountFrom.getAccountId(), accountTo.getAccountId(), 3.41);
    assertEquals("The amount "+3.41+" is lower than the current balance in the from account which has "+accountFrom.getBalance() , result);
  }
}

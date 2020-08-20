package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AmountTransferException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.transaction.AccountTransactionManager;

import lombok.Getter;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  
  private AccountTransactionManager transactionManager;
  
  @Autowired
  private NotificationService emailNotifyService;
  
  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
    this.transactionManager = new AccountTransactionManager(accountsRepository);
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  
 // @Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=AmountTransferException.class)
  public void amountTransfer(final String fromAccount,	
		  final String toAccount, final BigDecimal transferAmount) throws AmountTransferException {
	  
	  transactionManager.doInTransaction(() ->{
		  
		  this.debit(fromAccount, transferAmount);
		  this.credit(toAccount, transferAmount);
	  });
	  
	  transactionManager.commit();
	  notifyEmail(fromAccount, toAccount, transferAmount);
  }
  
	private Account debit(String accountId, BigDecimal amount) throws AmountTransferException{
  		// take repository from transaction manager in order to manage transactions and rollBack.
  		//But, This method will only be transactional only if this is called within "transactionManager.doInTransaction()
  		// OR method annotated with @AccountTransaction.
		final Account account = transactionManager.getRepoProxy().getAccount(accountId);
		if(account == null) {
			throw new AmountTransferException("Account does not exist");
		}
		if(account.getBalance().compareTo(amount) == -1) {
			throw new AmountTransferException("Insufficient balance in account");
		}
		BigDecimal bal = account.getBalance().subtract(amount);
		account.setBalance(bal);
		return account;
	}
	
	private Account credit(String accountId, BigDecimal amount) throws AmountTransferException{
		// take repository from transaction manager in order to manage transactions and rollBack.
  		//But, This method will only be transactional only if this is called within "transactionManager.doInTransaction()
  		// OR method annotated with @AccountTransaction.
		final Account account = transactionManager.getRepoProxy().getAccount(accountId);
		if(account == null) {
			throw new AmountTransferException("Account does not exist");
		}
		BigDecimal bal = account.getBalance().add(amount);
		account.setBalance(bal);
		return account;
	}
	
	public boolean notifyEmail(String fromAccount, String toAccount, BigDecimal transferAmount) {
		emailNotifyService.notifyAboutTransfer(this.accountsRepository.getAccount(fromAccount), transferAmount +" is transfered to " + toAccount);
		emailNotifyService.notifyAboutTransfer(this.accountsRepository.getAccount(toAccount), transferAmount +" is debited from " + fromAccount);
		return true;
	}
}

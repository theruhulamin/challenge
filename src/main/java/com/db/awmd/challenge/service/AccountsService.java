package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AmountTransferException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	private NotificationService emailNotifyService;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountNo) {
		return this.accountsRepository.getAccount(accountNo);
	}

	public void amountTransfer(final String fromAccount, final String toAccount, final BigDecimal transferAmount)
			throws AmountTransferException {

		synchronized (this) {
			this.accountsRepository.amountTransfer(fromAccount, toAccount, transferAmount);
			notifyEmail(fromAccount, toAccount, transferAmount);
		}
	}
/**
 * notifyEmail method to send email notification
 * @param fromAccount
 * @param toAccount
 * @param transferAmount
 */
	public void notifyEmail(String fromAccountNo, String toAccountNo, BigDecimal transferAmount) {
		emailNotifyService.notifyAboutTransfer(this.accountsRepository.getAccount(fromAccountNo),
				transferAmount + " is transfered to " + toAccountNo);
		emailNotifyService.notifyAboutTransfer(this.accountsRepository.getAccount(toAccountNo),
				transferAmount + " is debited from " + fromAccountNo);
	}
}

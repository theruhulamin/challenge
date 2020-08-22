package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AmountTransferException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();
	private final Object lock = new Object();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	@Override
	public void amountTransfer(String fromAccountNo, String toAccountNo, BigDecimal transferAmount)
			throws AmountTransferException {
		if (Objects.equals(fromAccountNo, toAccountNo)) {
			throw new AmountTransferException("Accounts should be different");
		}

		if (!accounts.containsKey(fromAccountNo)) {
			throw new AmountTransferException("Sender account no. is not found");
		}

		if (!accounts.containsKey(toAccountNo)) {
			throw new AmountTransferException("Receiver account no. is not found");
		}

		synchronized (lock) {
			Account fromAccount = accounts.get(fromAccountNo);
			Account toAccount = accounts.get(toAccountNo);

			if (fromAccount.getBalance().compareTo(transferAmount) < 0) {
				throw new AmountTransferException("Insufficient funds");
			}

			accounts.replace(fromAccountNo, fromAccount.withdraw(transferAmount));
			accounts.replace(toAccountNo, toAccount.deposit(transferAmount));
		}

	}

}

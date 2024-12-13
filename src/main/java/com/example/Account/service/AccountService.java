package com.example.Account.service;

import com.example.Account.domain.Account;
import com.example.Account.domain.AccountStatus;
import com.example.Account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;


	@Transactional
	public void createAccount() {
		Account account = Account.builder()
						.accountNumber("40000")
						.accountStatus(AccountStatus.IN_USER)
						.build();
		accountRepository.save(account);
	}

	@Transactional
	public Account getAccount(Long id) {
		if (id < 0) {
			throw new RuntimeException("Minus");
		}
		return accountRepository.findById(id).get();
	}


}

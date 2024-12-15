package com.example.Account.service;

import com.example.Account.Exception.AccountException;
import com.example.Account.domain.Account;
import com.example.Account.domain.AccountUser;
import com.example.Account.dto.AccountDto;
import com.example.Account.repository.AccountRepository;
import com.example.Account.repository.AccountUserRepository;
import com.example.Account.type.AccountStatus;
import com.example.Account.type.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.Account.type.AccountStatus.IN_USER;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final AccountUserRepository accountUserRepository;

	/**
		- 사용자가 있는지 조회
		- 계좌의 번호를 생성하고
		- 계좌를 저장하고, 그 정보를 넘긴다.
	 */
	@Transactional
	public AccountDto createAccount(Long userId, Long initialBalance) {
		AccountUser accountUser = accountUserRepository.findById(userId)
				.orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
			// 조회된 값이 없으면 AccountException 에러 발생 시킴

		validateCrateAccount(accountUser);

		String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
				.map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "") // 가장 최근 계좌 번호 가져와서 1을 더함
				.orElse("1000000000"); // 등록된 계좌 번호가 없으면 1000000000 로 반환

 		return AccountDto.fromEntity(
				 accountRepository.save(Account.builder()
						.accountUser(accountUser)
						.accountStatus(IN_USER)
						.accountNumber(newAccountNumber)
						.balance(initialBalance)
						.registeredAt(LocalDateTime.now())
						.build())
				);
	}

	private void validateCrateAccount(AccountUser accountUser) {
		if (accountRepository.countByAccountUser(accountUser) >= 10) {
			throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
		}
	}

	@Transactional
	public Account getAccount(Long id) {
		if (id < 0) {
			throw new RuntimeException("Minus");
		}
		return accountRepository.findById(id).get();
	}


	@Transactional
	public AccountDto deleteAccount(Long userId, String accountNumber) {
		AccountUser accountUser = accountUserRepository.findById(userId)
				.orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

		validateDeleteAccount(accountUser, account);

		account.setAccountStatus(AccountStatus.UNREGISTERED);
		account.setUnregisteredAt(LocalDateTime.now());

		accountRepository.save(account);

		return AccountDto.fromEntity(account);
	}

	private void validateDeleteAccount(AccountUser accountUser, Account account) {
		if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
			throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
		}

		if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
			throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
		}

		if (account.getBalance() > 0) {
			throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
		}
	}
}

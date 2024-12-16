package com.example.Account.service;

import com.example.Account.Exception.AccountException;
import com.example.Account.domain.Account;
import com.example.Account.domain.AccountUser;
import com.example.Account.domain.Transaction;
import com.example.Account.dto.TransactionDto;
import com.example.Account.repository.AccountRepository;
import com.example.Account.repository.AccountUserRepository;
import com.example.Account.repository.TransactionRepository;
import com.example.Account.type.AccountStatus;
import com.example.Account.type.TransactionRequestType;
import com.example.Account.type.TransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.Account.type.ErrorCode.*;
import static com.example.Account.type.TransactionRequestType.F;
import static com.example.Account.type.TransactionRequestType.S;
import static com.example.Account.type.TransactionType.USE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
	private final TransactionRepository transactionRepository;
	private final AccountUserRepository accountUserRepository;
	private final AccountRepository accountRepository;

	/**
	 * 실패 응답 정책
	 * - 사용자가 없는 경우
	 * - 사용자 아이디와 게좌 소유자가 다른 경우
	 * - 계좌가 이미 해지 상태인 경우
	 * - 거래 금액이 잔액보다 큰 경우
	 * - 거래 금액이 너무 작거나 큰 경우
	 */
	@Transactional
	public TransactionDto useBalance(Long userId, String accountNumber,
									 Long amount) {
		AccountUser user = accountUserRepository.findById(userId)
				.orElseThrow(() -> new AccountException(USER_NOT_FOUND));

		Account account = accountRepository.findByAccountNumber(accountNumber)
						.orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

		validateUseBalance(user, account, amount);

		account.useBalance(amount);

		return TransactionDto.fromEntity(saveAndGetTransaction(S, amount, account));
	}

	private void validateUseBalance(AccountUser user, Account account, Long amount) {
		if (!Objects.equals(user.getId(), account.getAccountUser().getId())) {
			throw new AccountException(USER_ACCOUNT_UN_MATCH);
		}

		if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
			throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
		}

		if (account.getBalance() < amount) {
			throw new AccountException(AMOUNT_EXCEED_BALANCE);
		}
	}

	@Transactional
	public void saveFailedUseTransaction(String accountNumber, Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

		saveAndGetTransaction(F, amount, account);

	}

	private Transaction saveAndGetTransaction(
			TransactionRequestType transactionRequestType, Long amount, Account account) {
		return transactionRepository.save(
				Transaction.builder()
						.transactionType(USE)
						.transactionRequestType(transactionRequestType)
						.account(account)
						.amount(amount)
						.balanceSnapshot(account.getBalance())
						.transactionId(UUID.randomUUID().toString().replace("-", ""))
						.transactedAt(LocalDateTime.now())
						.build()
		);
	}
}

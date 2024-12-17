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
import com.example.Account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.Account.type.AccountStatus.*;
import static com.example.Account.type.TransactionRequestType.F;
import static com.example.Account.type.TransactionRequestType.S;
import static com.example.Account.type.TransactionType.CANCEL;
import static com.example.Account.type.TransactionType.USE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
	public static final long USE_AMOUNT = 200L;
	public static final long CANCEL_AMOUNT = 200L;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private AccountUserRepository accountUserRepository;

	@InjectMocks
	private TransactionService transactionService;


	@Test
	void successUseBalance() {
		// given
		AccountUser user = AccountUser.builder().id(12L).name("Pobi").build();

		Account account = Account.builder()
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));

		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));

		given(transactionRepository.save(any()))
				.willReturn(Transaction.builder()
						.account(account)
						.transactionType(USE)
						.transactionRequestType(S)
						.transactionId("transactionId")
						.transactedAt(LocalDateTime.now())
						.amount(1000L)
						.balanceSnapshot(9000L)
						.build());
		ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

		// when
		TransactionDto transactionDto = transactionService.useBalance(1L, "1000000000", USE_AMOUNT);

		// then
		verify(transactionRepository, times(1)).save(captor.capture());
		assertEquals(200L, captor.getValue().getAmount());
		assertEquals(9800L, captor.getValue().getBalanceSnapshot());
		assertEquals(S, transactionDto.getTransactionRequestType());
		assertEquals(USE, transactionDto.getTransactionType());
		assertEquals(9000L, transactionDto.getBalanceSnapshot());
		assertEquals(1000L, transactionDto.getAmount());
	}


	@Test
	@DisplayName("해당 유저 없음 - 잔액 사용 실패")
	void useBalance_UserNotFound() { // 유저가 없는 경우
		// given
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.empty());

		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.useBalance(1L, "10000000000", 1000L));

		// then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("해당 계좌 없음 - 잔액 사용 실패")
	void useBalance_AccountNotFound() {
		// given
		AccountUser user = AccountUser.builder().id(12L).name("Pobi").build();

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.empty());


		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.useBalance(1L, "10000000000", 1000L));

		// then
		assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("계좌 소유주 다름 - 잔액 사용 실패")
	void useBalance_userUnMatch() {
		// given
		AccountUser pobi = AccountUser.builder().id(12L).name("Pobi").build();
		AccountUser harry = AccountUser.builder().id(13L).name("Harry").build();

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(pobi));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(Account.builder()
								.accountUser(harry)
								.balance(0L)
								.accountNumber("1000000012").build()));

		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.useBalance(1L, "10000000000", 1000L));

		// then
		assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, exception.getErrorCode());
	}

	@Test
	@DisplayName("해지 계좌는 해지할 수 없다")
	void useBalance_alreadyUnRegistered() {
		// given
		AccountUser pobi = AccountUser.builder().id(12L).name("Pobi").build();

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(pobi));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(Account.builder()
								.accountUser(pobi)
								.balance(0L)
								.accountStatus(AccountStatus.UNREGISTERED)
								.accountNumber("1000000012").build()));

		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.useBalance(1L, "10000000000", 1000L));

		// then
		assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
	}


	@Test
	@DisplayName("거래 금액이 잔액보다 큰 경우")
	void exceedAmount_UseBalance() {
		// given
		AccountUser user = AccountUser.builder().id(12L).name("Pobi").build();

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));

		Account account = Account.builder()
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(100L)
				.accountNumber("1000000012").build();

		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));

		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.useBalance(1L, "10000000000", 1000L));

		// then
		assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE, exception.getErrorCode());
		verify(transactionRepository, times(0)).save(any()); // transaction 레코드가 저장되지 않음을 확인

	}

	@Test
	@DisplayName("실패 트랜잭션 저장 성공")
	void saveFailedUseTransaction() {
		// given
		AccountUser user = AccountUser.builder().id(12L).name("Pobi").build();

		Account account = Account.builder()
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();

		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));

		given(transactionRepository.save(any()))
				.willReturn(Transaction.builder()
						.account(account)
						.transactionType(USE)
						.transactionRequestType(S)
						.transactionId("transactionId")
						.transactedAt(LocalDateTime.now())
						.amount(1000L)
						.balanceSnapshot(9000L)
						.build());
		ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

		// when
		transactionService.saveFailedUseTransaction( "1000000000", USE_AMOUNT);

		// then
		verify(transactionRepository, times(1)).save(captor.capture());
		assertEquals(USE_AMOUNT, captor.getValue().getAmount());
		assertEquals(10000L, captor.getValue().getBalanceSnapshot());
		assertEquals(F, captor.getValue().getTransactionRequestType());
	}

	@Test
	void successCancelBalance() {
		// given
		AccountUser user = AccountUser.builder().id(12L).name("Pobi").build();

		Account account = Account.builder()
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();

		Transaction transaction = Transaction.builder()
						.account(account)
						.transactionType(USE)
						.transactionRequestType(S)
						.transactionId("transactionId")
						.transactedAt(LocalDateTime.now())
						.amount(CANCEL_AMOUNT)
						.balanceSnapshot(9000L)
						.build();

		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(transaction));

		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));

		given(transactionRepository.save(any()))
				.willReturn(Transaction.builder()
						.account(account)
						.transactionType(CANCEL)
						.transactionRequestType(S)
						.transactionId("transactionIdForCancel")
						.transactedAt(LocalDateTime.now())
						.amount(CANCEL_AMOUNT)
						.balanceSnapshot(10000L)
						.build());
		ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

		// when
		TransactionDto transactionDto = transactionService.cancelBalance("transactionId", "1000000000", CANCEL_AMOUNT);

		// then
		verify(transactionRepository, times(1)).save(captor.capture());
		assertEquals(CANCEL_AMOUNT, captor.getValue().getAmount());
		assertEquals(10000L + CANCEL_AMOUNT, captor.getValue().getBalanceSnapshot());
		assertEquals(S, transactionDto.getTransactionRequestType());
		assertEquals(CANCEL, transactionDto.getTransactionType());
		assertEquals(10000L, transactionDto.getBalanceSnapshot());
		assertEquals(CANCEL_AMOUNT, transactionDto.getAmount());
	}


	@Test
	@DisplayName("해당 계좌 없음 - 잔액 사용 취소 실패")
	void cancelTransaction_AccountNotFound() {
		// given
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(Transaction.builder().build()));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.empty());


		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.cancelBalance("transactionId", "10000000000", 1000L));

		// then
		assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
	}

		@Test
	@DisplayName("원 사용 거래 없음 - 잔액 사용 취소 실패")
	void cancelTransaction_TransactionNotFound() {
		// given
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.empty());


		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.cancelBalance("transactionId", "10000000000", 1000L));

		// then
		assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
	}


	@Test
	@DisplayName("거래와 계좌가 매칭 실패 - 잔액 사용 취소 실패")
	void cancelTransaction_TransactionAccountUnMatch() {
		AccountUser user = AccountUser.builder().id(12L).name("Pobi").build();

		Account account = Account.builder()
				.id(1L)
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();

		Account accountNotUse = Account.builder()
				.id(2L)
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000013").build();

		Transaction transaction = Transaction.builder()
						.account(account)
						.transactionType(USE)
						.transactionRequestType(S)
						.transactionId("transactionId")
						.transactedAt(LocalDateTime.now())
						.amount(CANCEL_AMOUNT)
						.balanceSnapshot(9000L)
						.build();

		// given
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(transaction));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(accountNotUse));


		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.cancelBalance(
						"transactionId",
						"10000000000",
						CANCEL_AMOUNT));

		// then
		assertEquals(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH, exception.getErrorCode());
	}

	@Test
	@DisplayName("거래 금액과 최소 금액이 다름 - 잔액 사용 취소 실패")
	void cancelTransaction_CancelMustFully() {
		AccountUser user = AccountUser.builder().id(12L).name("Pobi").build();

		Account account = Account.builder()
				.id(1L)
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();

		Transaction transaction = Transaction.builder()
						.account(account)
						.transactionType(USE)
						.transactionRequestType(S)
						.transactionId("transactionId")
						.transactedAt(LocalDateTime.now())
						.amount(CANCEL_AMOUNT + 1000L)
						.balanceSnapshot(9000L)
						.build();

		// given
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(transaction));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));


		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.cancelBalance(
						"transactionId",
						"10000000000",
						CANCEL_AMOUNT));

		// then
		assertEquals(ErrorCode.CANCEL_MUST_FULLY, exception.getErrorCode());
	}


	@Test
	@DisplayName("취소는 1년까지만 가능 - 잔액 사용 취소 실패")
	void cancelTransaction_TooOldOrderTOCancel() {
		AccountUser user = AccountUser.builder().id(12L).name("Pobi").build();

		Account account = Account.builder()
				.id(1L)
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();

		Transaction transaction = Transaction.builder()
						.account(account)
						.transactionType(USE)
						.transactionRequestType(S)
						.transactionId("transactionId")
						.transactedAt(LocalDateTime.now().minusYears(1).minusDays(1))
						.amount(CANCEL_AMOUNT)
						.balanceSnapshot(9000L)
						.build();

		// given
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(transaction));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));


		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.cancelBalance(
						"transactionId",
						"10000000000",
						CANCEL_AMOUNT));

		// then
		assertEquals(ErrorCode.TOO_OLD_ORDER_TO_CANCEL, exception.getErrorCode());
	}
}
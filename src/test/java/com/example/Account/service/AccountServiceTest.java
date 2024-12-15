package com.example.Account.service;

import com.example.Account.Exception.AccountException;
import com.example.Account.domain.Account;
import com.example.Account.domain.AccountUser;
import com.example.Account.dto.AccountDto;
import com.example.Account.repository.AccountUserRepository;
import com.example.Account.type.AccountStatus;
import com.example.Account.repository.AccountRepository;
import com.example.Account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class) // Mockito 테스트를 위한 어노테이션
class AccountServiceTest {
	// 테스트하려는 부분은 AccountService 인데, AccountService 는 AccountRepository 에 의존되어 있음
	// 이 의존된 부분을 가짜(Mock)로 만들어서 AccountService 에 넣어주기
	@Mock
	private AccountRepository accountRepository;

	@Mock
	private AccountUserRepository accountUserRepository;

	@InjectMocks
	private AccountService accountService;


	@Test
	void createAccountSuccess() { // 기존 계좌가 있는 유저가 새로운 계좌를 생성하는 경우
		// given
		AccountUser user = AccountUser.builder().id(12L).name("Pobi").build();

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findFirstByOrderByIdDesc())
				.willReturn(Optional.of(Account.builder()
								.accountNumber("1000000012").build()));
		given(accountRepository.save(any()))
				.willReturn(Account.builder()
								.accountUser(user)
								.accountNumber("1000000013").build());

		ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

		// when
		AccountDto accountDto = accountService.createAccount(1L, 1000L);

		// then
		verify(accountRepository, times(1)).save(captor.capture());
		assertEquals(12L, accountDto.getUserId ());
		assertEquals("1000000013", captor.getValue().getAccountNumber());
	}


	@Test
	void createFirstAccount() { // 계좌를 처음 생성하는경우
		// given
		AccountUser user = AccountUser.builder().id(15L).name("Pobi").build();

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findFirstByOrderByIdDesc())
				.willReturn(Optional.empty());
		given(accountRepository.save(any()))
				.willReturn(Account.builder()
								.accountUser(user)
								.accountNumber("1000000013").build());

		ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

		// when
		AccountDto accountDto = accountService.createAccount(1L, 1000L);

		// then
		verify(accountRepository, times(1)).save(captor.capture());
		assertEquals(15L, accountDto.getUserId ());
		assertEquals("1000000000", captor.getValue().getAccountNumber());
	}


	@Test
	@DisplayName("해당 유저 없음 - 계좌 생성 실패")
	void createAccount_UserNotFound() { // 유저가 없는 경우
		// given
		AccountUser user = AccountUser.builder().id(15L).name("Pobi").build();

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.empty());

		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.createAccount(1L, 1000L));

		// then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}


	@Test
	@DisplayName("유저 당 최대 계좌는 10개")
	void createAccount_maxAccountIs10() {
		// given
		AccountUser user = AccountUser.builder().id(15L).name("Pobi").build();

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.countByAccountUser(any()))
				.willReturn(10);

		// when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.createAccount(1L, 1000L));

		// then
		assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, exception.getErrorCode());
	}

}
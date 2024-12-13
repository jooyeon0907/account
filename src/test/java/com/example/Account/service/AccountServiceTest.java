package com.example.Account.service;

import com.example.Account.domain.Account;
import com.example.Account.domain.AccountStatus;
import com.example.Account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class) // Mockito 테스트를 위한 어노테이션
class AccountServiceTest {
	// 테스트하려는 부분은 AccountService 인데, AccountService 는 AccountRepository 에 의존되어 있음
	// 이 의존된 부분을 가짜(Mock)로 만들어서 AccountService 에 넣어주기
	@Mock
	private AccountRepository accountRepository;

	@InjectMocks
	private AccountService accountService;

	@Test
	@DisplayName("계좌 조회 성공")
	void testXXX() {
		// given
		given(accountRepository.findById(anyLong()))
				.willReturn(Optional.of(
								Account.builder()
								.accountStatus(AccountStatus.UNREGISTERED)
								.accountNumber("65789").build()
							)
				);

		// when
		Account account = accountService.getAccount(4555L);

		// then
		assertEquals("65789", account.getAccountNumber());
		assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
	}

	@Test
	@DisplayName("Test 이름 변경")
	void testGetAccount() {
		// given
		given(accountRepository.findById(anyLong()))
				.willReturn(Optional.of(
								Account.builder()
								.accountStatus(AccountStatus.UNREGISTERED)
								.accountNumber("65789").build()
							)
				);

		// when
		Account account = accountService.getAccount(4555L);

		// then
		assertEquals("65789", account.getAccountNumber());
		assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
	}

	@Test
	void testGetAccount2() {
		// given
		given(accountRepository.findById(anyLong()))
				.willReturn(Optional.of(
								Account.builder()
								.accountStatus(AccountStatus.UNREGISTERED)
								.accountNumber("65789").build()
							)
				);

		// when
		Account account = accountService.getAccount(3L);

		// then
		assertEquals("65789", account.getAccountNumber());
		assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
	}

}
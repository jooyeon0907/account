package com.example.Account.service;

import com.example.Account.domain.Account;
import com.example.Account.domain.AccountStatus;
import com.example.Account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

		ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class); // Long 타입 박스 만들기



		// when
		Account account = accountService.getAccount(4555L);

		// then
	 	// accountService.getAccount() 했을 때 accountRepository 가  findById() 를 1번 불렸다
		verify(accountRepository, times(1)).findById(anyLong());
		//
		verify(accountRepository, times(1)).findById(captor.capture());
		assertEquals(4555L, captor.getValue());
		assertNotEquals(45515L, captor.getValue());
		// accountRepository 가  save 하지 않았다
		verify(accountRepository, times(0)).save(any());
		assertEquals("65789", account.getAccountNumber());
		assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
	}


		@Test
	@DisplayName("계좌 조회 실패 음수로 조회")
	void testFailedToSearchAccount() {
		// given
		// when
		RuntimeException exception = assertThrows(RuntimeException.class,
					() -> accountService.getAccount(-10L));
			// accountService.getAccount(-10L) 동작을 시켰을 때 RuntimeException.class 가 발생할 것이다

		// then
		assertEquals("Minus", exception.getMessage());
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
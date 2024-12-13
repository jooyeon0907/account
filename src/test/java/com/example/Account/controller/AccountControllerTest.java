package com.example.Account.controller;

import com.example.Account.domain.Account;
import com.example.Account.domain.AccountStatus;
import com.example.Account.service.AccountService;
import com.example.Account.service.RedisTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AccountController.class)
class AccountControllerTest {
	@MockBean
	private AccountService accountService;

	@MockitoBean
	private RedisTestService redisTestService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void successGetAccount () throws Exception {
		// given
		given(accountService.getAccount(anyLong()))
				.willReturn(Account.builder()
						.accountNumber("3456")
						.accountStatus(AccountStatus.IN_USER)
						.build());

		// when
		// then
		mockMvc.perform(get("/account/876"))
				.andDo(print())
				.andExpect(jsonPath("$.accountNumber").value("3456"))
				.andExpect(jsonPath("$.accountStatus").value("IN_USER"))
				.andExpect(status().isOk());
	}

}
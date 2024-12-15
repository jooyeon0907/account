package com.example.Account.controller;

import com.example.Account.domain.Account;
import com.example.Account.dto.AccountDto;
import com.example.Account.dto.CreateAccount;
import com.example.Account.service.AccountService;
import com.example.Account.service.RedisTestService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AccountController {
	private final AccountService accountService;
	private final RedisTestService redisTestService;


	@PostMapping("/account")
	public CreateAccount.Response createAccount(
			@RequestBody @Valid CreateAccount.Request request
	){
		return CreateAccount.Response.from(
				accountService.createAccount(
					request.getUserId(),
					request.getInitialBalance()
				)
		);
	}

	@GetMapping("/get-lock")
	public String getLock() {
		return redisTestService.getLock();
	}

	@GetMapping("/account/{id}")
	public Account getAccount(@PathVariable Long id){
		return accountService.getAccount(id);
	}






}

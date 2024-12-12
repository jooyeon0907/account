package com.example.Account.controller;

import com.example.Account.domain.Account;
import com.example.Account.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {
	private final AccountService accountService;

	@GetMapping("/create-account")
	public String createAccount(){
		accountService.createAccount();
		return "success";
	}

	@GetMapping("/account/{id}")
	public Account getAccount(@PathVariable Long id){
		return accountService.getAccount(id);
	}






}

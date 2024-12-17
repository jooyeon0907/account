package com.example.Account.service;

import com.example.Account.Exception.AccountException;
import com.example.Account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
	private final RedissonClient redissonClient;

	public void lock(String accountNumber) {
		RLock lock = redissonClient.getLock(getLockKey(accountNumber));
		log.debug("Trying lock for accountNumber : {}", accountNumber);

		try {
			boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS); // waitTIme 동안 락 획득 기다리고, leaseTIme 동안 아무 작업 안하면 락 해제
			if(!isLock) {
				log.error("=================== Lock acquisition failed ======");
				throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
			}
		} catch (AccountException e){
			throw e;
   		} catch (Exception e){
			log.error("Redis lock failed");
   		}
	}

	public void unLock(String accountNumber){
		log.debug("Unlock for accountNumber: {}", accountNumber);
		redissonClient.getLock(getLockKey(accountNumber)).unlock();
	}

	private String getLockKey(String accountNumber) {
		return "ACLK:" + accountNumber;
	}

}

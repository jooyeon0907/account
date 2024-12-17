package com.example.Account.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AccountLock {
	long tryLockTime() default 5000L; // 기다릴 시간 설정
}

package com.example.Account.Exception;

import com.example.Account.dto.ErrorResponse;
import com.example.Account.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.Account.type.ErrorCode.INTERVAL_SERVER_ERROR;
import static com.example.Account.type.ErrorCode.INVALID_REQUEST;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AccountException.class)
	public ErrorResponse handleAccountException(AccountException e){
		log.error("{} is occurred.", e.getErrorMessage());

		return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e){
		log.error("DataIntegrityViolationException is occurred.", e);

		return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
		log.error("MethodArgumentNotValidException is occurred.", e);

		return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription());
	}

	@ExceptionHandler(Exception.class)
	public ErrorResponse handleException(Exception e){
		log.error("{} is occurred.", e);

		return new ErrorResponse(INTERVAL_SERVER_ERROR,
				INTERVAL_SERVER_ERROR.getDescription());
	}

}

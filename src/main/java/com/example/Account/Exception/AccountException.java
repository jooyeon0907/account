package com.example.Account.Exception;

import com.example.Account.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountException extends RuntimeException{
	private ErrorCode errorCode;
	private String errorMessage;
	/*
		RuntimeException 에도 갖고 있는 정보가 별로 없으므로, 다양한 에러를 표현하는데 한계가 있다.
		그래서 에러 코드와 메시지를 커스텀하여 사용할 수 있음
	 */

	public AccountException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getDescription();
	}
}

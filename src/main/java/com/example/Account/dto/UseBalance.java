package com.example.Account.dto;

import com.example.Account.type.TransactionRequestType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class UseBalance {
	@Getter
	@Setter
	@AllArgsConstructor
	public static class Request {
		@NotNull
		@Min(1)
		private Long userId;

		@NotBlank
		@Size(min = 10, max = 10)
		private String accountNumber;

		@NotNull
		@Min(10)
		@Max(1000_000_000)
		private Long amount;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private String accountNumber;
		private TransactionRequestType transactionResult;
		private String transactionId;
		private Long amount;
		private LocalDateTime transactedAt;

		public static Response from(TransactionDto transactionDto) {
			return Response.builder()
					.accountNumber(transactionDto.getAccountNumber())
					.transactionResult(transactionDto.getTransactionRequestType())
					.transactionId(transactionDto.getTransactionId())
					.amount(transactionDto.getAmount())
					.transactedAt(transactionDto.getTransactedAt())
					.build();
		}
	}


}

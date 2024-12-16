package com.example.Account.dto;

import com.example.Account.domain.Transaction;
import com.example.Account.type.TransactionRequestType;
import com.example.Account.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto { // Dto 를 사용하게 되면 원하는 정보를 마음대로 추가/삭제 할 수 있음
	private String accountNumber;
	private TransactionType transactionType;
	private TransactionRequestType transactionRequestType;
	private Long amount;
	private Long balanceSnapshot;
	private String transactionId;
	private LocalDateTime transactedAt;

	public static TransactionDto fromEntity(Transaction transaction) {
		return TransactionDto.builder()
				.accountNumber(transaction.getAccount().getAccountNumber())
				.transactionType(transaction.getTransactionType())
				.transactionRequestType(transaction.getTransactionRequestType())
				.amount(transaction.getAmount())
				.balanceSnapshot(transaction.getBalanceSnapshot())
				.transactionId(transaction.getTransactionId())
				.transactedAt(transaction.getTransactedAt())
				.build();
	}
}

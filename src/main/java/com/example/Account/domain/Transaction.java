package com.example.Account.domain;

import com.example.Account.type.AccountStatus;
import com.example.Account.type.TransactionRequestType;
import com.example.Account.type.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
	@Id
	@GeneratedValue
	private Long id;

	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	@Enumerated(EnumType.STRING)
	private TransactionRequestType transactionRequestType;

	@ManyToOne // N개의 Transaction 이 특정 1개의 Account 에 연결됨
	private Account account;
	private Long amount;
	private Long balanceSnapshot;

	private String transactionId; // PK 인 id를 사용하면 보안상으로 좋지 않으므로 transactionId 로 관리
	private LocalDateTime transactedAt;  // 거래 시간

	@CreatedDate
	private LocalDateTime cratedAt;
	@LastModifiedDate
	private LocalDateTime updatedAt;

}

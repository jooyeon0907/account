package com.example.Account.domain;

import com.example.Account.type.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
	// Spring Data JAP 에서 제공하는 엔터티 감사(auditing) 기능을 위한 리스너
public class Account {
	@Id // Account 라는 테이블에 pk 로 사용하겠다
	@GeneratedValue
	private Long id;

	@ManyToOne
	private AccountUser accountUser;
	private String accountNumber;

	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;
	private Long balance;

	private LocalDateTime registeredAt;
	private LocalDateTime unregisteredAt;

	@CreatedDate	// createdAt 을 자동으로 저장 (EntityListeners 에 의해 활성화)
	private LocalDateTime cratedAt;
	@LastModifiedDate // updatedAt 을 자동으로 저장 (EntityListeners 에 의해 활성화)
	private LocalDateTime updatedAt;

}


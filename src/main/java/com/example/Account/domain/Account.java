package com.example.Account.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account {
	@Id // Account 라는 테이블에 pk 로 사용하겠다
	@GeneratedValue
	private Long id;

	private String accountNumber;

	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;
}

package com.example.Account.domain;

import com.example.Account.Exception.AccountException;
import com.example.Account.type.AccountStatus;
import com.example.Account.type.ErrorCode;
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
public class Account extends BasicEntity {

	@ManyToOne // N:1 관계 설정, N개의 Account 가 특정 1개의 AccountUser 에 연결되도록 해줌
	private AccountUser accountUser;
	private String accountNumber;

	@Enumerated(EnumType.STRING) // enum 은 0, 1, 2, 3... 처럼 숫자로 저장되어 문자열로 (IN_USE, UNREGISTERED) 저장해주기 위함
	private AccountStatus accountStatus;
	private Long balance;

	private LocalDateTime registeredAt;
	private LocalDateTime unregisteredAt;

	public void useBalance(Long amount) { // 중요한 데이터를 변경하는 로직은 객체 안에서 수행 할 수 있는게 더 안전함
		if (amount > balance) {
			throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
		}
		balance -= amount;
	}

	public void cancelBalance(Long amount) {
		if (amount < 0) {
			throw new AccountException(ErrorCode.INVALID_REQUEST);
		}
		balance += amount;
	}

}


package com.example.Account.domain;

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
@Builder(builderMethodName = "doesNotUserTHisBuilder")
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
	// Spring Data JAP 에서 제공하는 엔터티 감사(auditing) 기능을 위한 리스너
public class BasicEntity {
	@Id // 해당 테이블의 pk 로 사용하겠다
	@GeneratedValue
	private Long id;

	@CreatedDate	// createdAt 을 자동으로 저장 (EntityListeners 에 의해 활성화)
	private LocalDateTime createdAt;
	@LastModifiedDate // updatedAt 을 자동으로 저장 (EntityListeners 에 의해 활성화)
	private LocalDateTime updatedAt;

}

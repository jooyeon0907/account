package com.example.Account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration // 자동으로 빈 등록
@EnableJpaAuditing
	// 애플리케이션을 실행 시, 오토 스캔이 되는 타입이 됨
	// DB에 데이터를 저장하거나 업데이트 시, @ CreatedDate, @LastModifiedDate 어노테이션이 붙은 필드에 자동으로 저장
public class JapAuditingConfiguration {



}

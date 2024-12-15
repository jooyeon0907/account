package com.example.Account.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class AccountUser {
	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private LocalDate createdAt;
	private LocalDate updatedAt;

}

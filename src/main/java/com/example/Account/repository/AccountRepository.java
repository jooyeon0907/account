package com.example.Account.repository;

import com.example.Account.domain.Account;
import com.example.Account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findFirstByOrderByIdDesc(); // 이름을 형식에 맞게 작성하면 자동으로 쿼리를 생성해줌

	Integer countByAccountUser(AccountUser accountUser);

	Optional<Account> findByAccountNumber(String accountNumber);

	List<Account> findByAccountUser(AccountUser accountUser);

}

package com.wezaam.withdrawal.repository;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    List<Withdrawal> findAllByStatusEqualsAndExecuteAtBefore(@Param("status") WithdrawalStatus status, @Param("executeAt") Instant executeAt);
}

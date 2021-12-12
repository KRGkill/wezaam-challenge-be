package com.wezaam.withdrawal.controller.mapper;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static java.util.Objects.isNull;

@Component
public class WithdrawalMapper {

    public Withdrawal requestToWithdrawal(long userId, long paymentMethodId,
                                          double amount, Instant executeAt) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(userId);
        withdrawal.setPaymentMethodId(paymentMethodId);
        withdrawal.setAmount(amount);
        withdrawal.setCreatedAt(Instant.now());
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        if (!isNull(executeAt)) {
            withdrawal.setExecuteAt(executeAt);
        }

        return withdrawal;
    }
}

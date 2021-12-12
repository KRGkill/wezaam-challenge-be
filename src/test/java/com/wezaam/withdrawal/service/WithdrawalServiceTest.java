package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.exception.EntityNotFoundException;
import com.wezaam.withdrawal.exception.TransactionException;
import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class WithdrawalServiceTest {

    @Autowired
    private WithdrawalService withdrawalService;

    @MockBean
    private WithdrawalRepository withdrawalRepository;

    @MockBean
    private PaymentMethodRepository paymentMethodRepository;

    @MockBean
    private WithdrawalProcessingService withdrawalProcessingService;

    @MockBean
    private EventsService eventsService;

    @Test
    void create_whenValidId_thenWithdrawalShouldBeCreated() throws EntityNotFoundException {
        Withdrawal withdrawal = createWithdrawal(1L, 10.0);

        when(withdrawalRepository.save(withdrawal)).thenReturn(withdrawal);

        withdrawalService.create(withdrawal);

        verify(withdrawalRepository).save(withdrawal);
    }

    @Test
    void run_whenValidWithdrawal_thenWithdrawalShouldBeProcessed() throws TransactionException {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(1L);
        Withdrawal withdrawal = createWithdrawal(1L, 10.0);
        withdrawal.setPaymentMethodId(paymentMethod.getId());
        Long transactionId = 1L;

        when(withdrawalRepository.findAllByStatusEqualsAndExecuteAtBefore(eq(WithdrawalStatus.PENDING), any())).thenReturn(List.of(withdrawal));
        when(paymentMethodRepository.findById(withdrawal.getPaymentMethodId())).thenReturn(Optional.of(paymentMethod));
        when(withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod)).thenReturn(transactionId);

        withdrawalService.run();

        verify(withdrawalRepository).save(withdrawal);
        verify(eventsService).send(withdrawal);
    }

    @Test
    void findAll_whenCallMethod_thenWithdrawals() {
        Withdrawal withdrawal1 = createWithdrawal(1L, 10.0);
        Withdrawal withdrawal2 = createWithdrawal(2L, 20.0);

        when(withdrawalRepository.findAll()).thenReturn(List.of(withdrawal1, withdrawal2));

        List<Withdrawal> withdrawalsFound = withdrawalService.findAll();

        assertThat(withdrawalsFound)
                .hasSize(2)
                .extracting(Withdrawal::getId, Withdrawal::getAmount)
                .containsExactlyInAnyOrder(
                        tuple(withdrawal1.getId(), withdrawal1.getAmount()),
                        tuple(withdrawal2.getId(), withdrawal2.getAmount())
                );
        verify(withdrawalRepository).findAll();
    }

    @Test
    void find_whenValidId_thenWithdrawalsShouldBeFound() throws EntityNotFoundException {
        Withdrawal withdrawal = createWithdrawal(1L, 10.0);

        when(withdrawalRepository.findById(withdrawal.getId())).thenReturn(Optional.of(withdrawal));

        Withdrawal withdrawalFound = withdrawalService.findById(withdrawal.getId());

        assertThat(withdrawalFound.getAmount()).isEqualTo(withdrawal.getAmount());
        verify(withdrawalRepository).findById(withdrawal.getId());
    }

    private Withdrawal createWithdrawal(Long id, double amount) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setId(id);
        withdrawal.setUserId(1L);
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setAmount(amount);

        return withdrawal;
    }
}

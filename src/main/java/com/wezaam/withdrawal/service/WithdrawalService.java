package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.exception.EntityNotFoundException;
import com.wezaam.withdrawal.exception.TransactionException;
import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.UserRepository;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.isNull;

@Service
public class WithdrawalService {

    @Autowired
    private WithdrawalRepository withdrawalRepository;
    @Autowired
    private WithdrawalProcessingService withdrawalProcessingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private EventsService eventsService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void create(Withdrawal withdrawal) throws EntityNotFoundException {
        userRepository.findById(withdrawal.getId())
                .orElseThrow(() -> new EntityNotFoundException("Could not find User with id: " + withdrawal.getId()));

        Withdrawal pendingWithdrawal = withdrawalRepository.save(withdrawal);

        if (isNull(pendingWithdrawal.getExecuteAt()) || pendingWithdrawal.getExecuteAt().isBefore(Instant.now())) {
            executorService.submit(() -> process(pendingWithdrawal));
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void run() {
        withdrawalRepository.findAllByStatusEqualsAndExecuteAtBefore(WithdrawalStatus.PENDING, Instant.now())
                .forEach(this::process);
    }

    public List<Withdrawal> findAll() {

        return this.withdrawalRepository.findAll();
    }

    public Withdrawal findById(Long id) throws EntityNotFoundException {

        return this.withdrawalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Withdrawal with id: " + id));
    }

    private void process(Withdrawal withdrawal) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(withdrawal.getPaymentMethodId()).orElse(null);
        if (paymentMethod != null) {
            try {
                var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod);
                withdrawal.setTransactionId(transactionId);
                withdrawal.setStatus(WithdrawalStatus.PROCESSING);
            } catch (TransactionException e) {
                withdrawal.setStatus(WithdrawalStatus.FAILED);
            } catch (Exception e) {
                withdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
            }
            withdrawalRepository.save(withdrawal);
            eventsService.send(withdrawal);
        }
    }
}

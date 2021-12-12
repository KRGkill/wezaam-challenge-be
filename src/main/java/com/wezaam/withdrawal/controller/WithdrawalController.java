package com.wezaam.withdrawal.controller;

import com.wezaam.withdrawal.controller.mapper.WithdrawalMapper;
import com.wezaam.withdrawal.exception.EntityNotFoundException;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.service.WithdrawalService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Api
@RestController
@RequestMapping("/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    private final WithdrawalMapper withdrawalMapper;

    @Autowired
    public WithdrawalController(WithdrawalService withdrawalService, WithdrawalMapper withdrawalMapper) {
        this.withdrawalService = withdrawalService;
        this.withdrawalMapper = withdrawalMapper;
    }

    @PostMapping("/create")
    public ResponseEntity create(@RequestParam long userId,
                                 @RequestParam long paymentMethodId,
                                 @RequestParam double amount,
                                 @RequestParam(required = false) Instant executeAt) throws EntityNotFoundException {
        Withdrawal withdrawal = withdrawalMapper.requestToWithdrawal(userId, paymentMethodId, amount, executeAt);

        withdrawalService.create(withdrawal);

        return new ResponseEntity(withdrawal, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity findAll() {

        return new ResponseEntity(withdrawalService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id) throws EntityNotFoundException {

        return new ResponseEntity(withdrawalService.findById(id), HttpStatus.OK);
    }
}

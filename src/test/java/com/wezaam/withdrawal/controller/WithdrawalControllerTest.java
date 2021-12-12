package com.wezaam.withdrawal.controller;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.service.WithdrawalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class WithdrawalControllerTest {

    @MockBean
    private WithdrawalService withdrawalService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void create_whenWithdrawal_thenWithdrawalIsSaved() throws Exception {
        Withdrawal withdrawal = createWithdrawal(1L, 10.0);

        mockMvc.perform(post("/withdrawals/create")
                        .param("userId", String.valueOf(1))
                        .param("paymentMethodId", String.valueOf(1))
                        .param("amount", String.valueOf(withdrawal.getAmount()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void findAll_whenWithdrawalId_thenReturnWithdrawal() throws Exception {
        Withdrawal withdrawal = createWithdrawal(1L, 10.0);

        given(withdrawalService.findAll()).willReturn(List.of(withdrawal));

        mockMvc.perform(get("/withdrawals"))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"transactionId\":null,\"amount\":10.0,\"createdAt\":null,\"executeAt\":null,\"userId\":1,\"paymentMethodId\":null,\"status\":\"PENDING\"}]"));
    }

    @Test
    void findById_whenWithdrawalId_thenReturnWithdrawal() throws Exception {
        Withdrawal withdrawal = createWithdrawal(1L, 10.0);

        given(withdrawalService.findById(withdrawal.getId())).willReturn(withdrawal);

        mockMvc.perform(get("/withdrawals/{id}", withdrawal.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"transactionId\":null,\"amount\":10.0,\"createdAt\":null,\"executeAt\":null,\"userId\":1,\"paymentMethodId\":null,\"status\":\"PENDING\"}"));
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

package com.wezaam.withdrawal.controller;

import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.model.User;
import com.wezaam.withdrawal.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAll_whenWithdrawalId_thenReturnWithdrawal() throws Exception {
        User user = createUser(1L, "user");

        given(userService.findAll()).willReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"firstName\":\"user\",\"paymentMethods\":[{\"id\":1,\"name\":\"user\"}],\"maxWithdrawalAmount\":100.0}]"));
    }

    @Test
    void findById_whenWithdrawalId_thenReturnWithdrawal() throws Exception {
        User user = createUser(1L, "user");

        given(userService.findById(user.getId())).willReturn(user);

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"firstName\":\"user\",\"paymentMethods\":[{\"id\":1,\"name\":\"user\"}],\"maxWithdrawalAmount\":100.0}"));
    }

    private User createUser(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setFirstName(name);
        user.setPaymentMethods(createPaymentMethods(name));
        user.setMaxWithdrawalAmount(100D);

        return user;
    }

    private List<PaymentMethod> createPaymentMethods(String name) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(1L);
        paymentMethod.setName(name);

        return List.of(paymentMethod);
    }
}

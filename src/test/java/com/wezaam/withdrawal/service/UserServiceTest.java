package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.exception.EntityNotFoundException;
import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.model.User;
import com.wezaam.withdrawal.repository.UserRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void findAll_whenCallMethod_thenUsers() {
        User user1 = createUser(1L, "user1");
        User user2 = createUser(2L, "user2");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> usersFound = userService.findAll();

        assertThat(usersFound)
                .hasSize(2)
                .extracting(User::getId, User::getFirstName)
                .containsExactlyInAnyOrder(
                        tuple(user1.getId(), user1.getFirstName()),
                        tuple(user2.getId(), user2.getFirstName())
                );
        verify(userRepository).findAll();
    }

    @Test
    void find_whenValidId_thenUsersShouldBeFound() throws EntityNotFoundException {
        User user = createUser(1L, "user");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User userFound = userService.findById(user.getId());

        assertThat(userFound.getFirstName()).isEqualTo(user.getFirstName());
        verify(userRepository).findById(user.getId());
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

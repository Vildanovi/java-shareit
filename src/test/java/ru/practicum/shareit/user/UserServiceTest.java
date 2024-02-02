package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityUpdateException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1)
                .name("user1Name")
                .email("user1@mail.ru")
                .build();
        user2 = User.builder()
                .id(2)
                .name("user2Name")
                .email("user1@mail.ru")
                .build();
    }

    @Test
    void put_Exception() {
        User updateUser = new User();
        updateUser.setName("updateuser");
        updateUser.setEmail("user1@mail.ru");
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        when(userRepository.findAll()).thenReturn(userList);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        EntityUpdateException entityUpdateException = assertThrows(EntityUpdateException.class, () -> userService.putUser(user1.getId(), updateUser));
        assertThat(entityUpdateException.getMessage(), equalTo("Объект уже существует: user1@mail.ru"));
    }

}

package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.*;

@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final UserService userService;

    private User user0;
    private User user1;

    @BeforeEach
    void setUp() {
        user0 = User.builder()
                .name("user0Name")
                .email("user0@mail.ru")
                .build();
        user1 = User.builder()
                .name("user1Name")
                .email("user1@mail.ru")
                .build();
    }

    @Test
    void getAllUser() {
        User userFirst = userService.createUser(user0);
        User userSecond = userService.createUser(user1);
        List<User> users = userService.getAllUsers();
        assertThat(users, contains(userFirst, userSecond));
    }

    @Test
    void getById() {
        User user = userService.createUser(user0);
        User userGet = userService.getUser(user.getId());
        assertThat(userGet, equalTo(user));
    }

    @Test
    void put() {
        User updateUser = new User();
        updateUser.setName("updateuser");
        User newUser = userService.createUser(user0);
        User newUserUpdate = userService.putUser(newUser.getId(), updateUser);
        assertThat(newUserUpdate.getName(), equalTo(updateUser.getName()));

    }

    @Test
    void delete() {
        User newUser = userService.createUser(user0);
        int userId = newUser.getId();
        userService.deleteUserById(userId);
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));
    }
}
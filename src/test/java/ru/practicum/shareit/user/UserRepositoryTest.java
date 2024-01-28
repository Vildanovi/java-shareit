package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {

    private final UserRepository userRepository;
    User user1;
    User user2;
    User user3;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder()
                .name("user1Name")
                .email("user1@mail.ru")
                .build());
        user2 = userRepository.save(User.builder()
                .name("user2Name")
                .email("user2@mail.ru")
                .build());
        user3 = userRepository.save(User.builder()
                .name("user3Name")
                .email("user3@mail.ru")
                .build());
    }

    @AfterEach
    void cleanRepositories() {
        userRepository.deleteAll();
    }

    @Test
    void getNotFoundUser() {
        Optional<User> user = userRepository.findById(100);
        assertFalse(user.isPresent());
    }

    @Test
    void getUserById() {
        Optional<User> user = userRepository.findById(user1.getId());
        assertTrue(user.isPresent());
    }

    @Test
    void getAllUser() {
        List<User> users = userRepository.findAll();
        assertThat(users, hasSize(3));
    }
}

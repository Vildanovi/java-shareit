package ru.practicum.shareit.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping()
    @Operation(summary = "Получить всех пользователей")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получить пользователя по идентификатору")
    public User getUser(@PathVariable int userId) {
        return userService.getUser(userId);
    }

    @PostMapping()
    @Operation(summary = "Добавление пользователя")
    public User postUser(@Valid @RequestBody User user) {
        log.debug("Получен POST запрос на добавление пользователя");
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Обновление пользователя")
    public User putUser(@PathVariable (value = "userId") int userId,
                        @RequestBody User user) {
        return userService.putUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Удаление пользователя")
    public void deleteUser(@PathVariable (value = "userId") int userId) {
        userService.deleteUserById(userId);
    }
}

package ru.practicum.shareit.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    @Operation(summary = "Получить всех пользователей")
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::userResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получить пользователя по идентификатору")
    public UserResponseDto getUser(@PathVariable int userId) {
        return UserMapper.userResponseDto(userService.getUser(userId));
    }

    @PostMapping()
    @Operation(summary = "Добавление пользователя")
    public UserResponseDto postUser(@Validated(CreatedBy.class) @RequestBody UserDto userDto) {
        log.debug("Получен POST запрос на добавление пользователя");
        User user = UserMapper.toUser(userDto);
        return UserMapper.userResponseDto(userService.createUser(user));
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Обновление пользователя")
    public UserResponseDto putUser(@PathVariable (value = "userId") int userId,
                        @Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.userResponseDto(userService.putUser(userId, user));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Удаление пользователя")
    public void deleteUser(@PathVariable (value = "userId") int userId) {
        userService.deleteUserById(userId);
    }
}

package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserModelTest {

    private final JacksonTester<UserDto> jsonUserDto;
    private final JacksonTester<UserResponseDto> jsonUserResponseDto;
    private final JacksonTester<User> userJacksonTester;

    private UserDto userDto = UserDto.builder()
            .id(1)
            .name("user")
            .email("user@email.ru")
            .build();
    private UserResponseDto userResponseDto = UserResponseDto.builder()
            .id(1)
            .name("user")
            .email("user@email.ru")
            .build();
    private User user = User.builder()
            .id(1)
            .name("user")
            .email("user@email.ru")
            .build();

    @SneakyThrows
    @Test
    void jsonUserDto() {
        JsonContent<UserDto> result = jsonUserDto.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@email.ru");
    }

    @SneakyThrows
    @Test
    void jsonUserResponseDto() {
        JsonContent<UserResponseDto> result = jsonUserResponseDto.write(userResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@email.ru");
    }

    @SneakyThrows
    @Test
    void userJacksonTester() {
        JsonContent<User> result = userJacksonTester.write(user);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@email.ru");
    }

}

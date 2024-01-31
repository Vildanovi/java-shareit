package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.exception.ValidationBadRequestException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerIT {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private UserService userService;

    private User user;
    private UserDto userDto;
    private UserDto userUpdate;

    @BeforeEach
    void setUp() {
        user = new User(1, "name", "name@mail.ru");
        userDto = new UserDto(1,"name", "name@mail.ru");
        userUpdate = UserDto.builder()
                .name("update")
                .email("update@mail.ru")
                .build();
    }

    @SneakyThrows
    @Test
    void save_User() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService).createUser(any());
    }

    @SneakyThrows
    @Test
    void get_AllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(user));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void save_UserException() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(ValidationBadRequestException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void getById() {
        int userId = 1;
        when(userService.getUser(userId))
                .thenReturn(user);

        mvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService).getUser(userId);
    }

    @SneakyThrows
    @Test
    void update() {
        int userId = user.getId();

        when(userService.putUser(anyInt(), any()))
                .thenReturn(user);
        mvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(userUpdate)))
                .andExpect(status().isOk());

        verify(userService).putUser(anyInt(), any());
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        int userId = 1;

        mvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
        verify(userService).deleteUserById(anyInt());
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentForItemDto;

import java.time.Instant;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemModelTest {

    private final JacksonTester<CommentForItemDto> jsonCommentForItemDto;
    Instant instant = Instant.now();

    CommentForItemDto commentForItemDto = CommentForItemDto.builder()
            .id(1)
            .text("text")
            .authorName("name")
            .created(instant)
            .build();

    @SneakyThrows
    @Test
    void jsonForCommentForItemDto() {
        JsonContent<CommentForItemDto> result = jsonCommentForItemDto.write(commentForItemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(instant.toString());
    }
}

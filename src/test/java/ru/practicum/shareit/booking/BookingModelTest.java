package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingModelTest {

    private final JacksonTester<BookingForItemDto> jsonForItemDto;
    private final JacksonTester<BookingNewDto> jsonUserBookingNewDto;
    private final JacksonTester<BookingResponseDto> jsonResponseDto;
    private final JacksonTester<Booking> jsonBooking;

    LocalDateTime start = LocalDateTime.of(2024, 01, 01, 01, 01);
    LocalDateTime end = LocalDateTime.of(2024, 01, 05, 01, 01);

    User owner = User.builder()
            .id(2)
            .name("ownerName")
            .email("owner@mail.ru")
            .build();

    Item item = Item.builder()
            .id(1)
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .owner(owner)
            .build();

    BookingNewDto bookingNewDto = BookingNewDto.builder()
            .itemId(1)
            .start(start)
            .end(end)
            .build();

    Booking booking = Booking.builder()
            .id(1)
            .start(start)
            .end(end)
            .item(item)
            .booker(owner)
            .status(BookingStatus.WAITING)
            .build();
    BookingForItemDto bookingForItemDto = BookingForItemDto.builder()
            .id(1)
            .start(start)
            .end(end)
            .itemId(1)
            .bookerId(1)
            .build();
    BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
            .id(1)
            .start(start)
            .end(end)
            .item(new BookingResponseDto.Item(1, "name"))
            .booker(new BookingResponseDto.Booker(1, "name"))
            .status(BookingStatus.WAITING)
            .build();

    @SneakyThrows
    @Test
    void jsonForItemDto() {
        JsonContent<BookingForItemDto> result = jsonForItemDto.write(bookingForItemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T01:01:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-05T01:01:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }

    @SneakyThrows
    @Test
    void jsonUserBookingNewDto() {
        JsonContent<BookingNewDto> result = jsonUserBookingNewDto.write(bookingNewDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T01:01:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-05T01:01:00");
    }

    @SneakyThrows
    @Test
    void jsonResponseDto() {
        JsonContent<BookingResponseDto> result = jsonResponseDto.write(bookingResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T01:01:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-05T01:01:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.WAITING.toString());
    }

    @SneakyThrows
    @Test
    void jsonBooking() {
        JsonContent<Booking> result = jsonBooking.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T01:01:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-05T01:01:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.WAITING.toString());
    }
}
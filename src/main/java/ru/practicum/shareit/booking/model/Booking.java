package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "start_date")
    private LocalDateTime start; // дата и время начала бронирования

    @Column(name = "end_date")
    private LocalDateTime end; // дата и время конца бронирования

    @ManyToOne(fetch = FetchType.EAGER)
    private Item item; // вещь, которую пользователь бронирует

    @ManyToOne(fetch = FetchType.EAGER)
    private User booker; // пользователь, который осуществляет бронирование

    @Enumerated
    @Column(name = "status")
    private BookingStatus status; // статус бронирования
}

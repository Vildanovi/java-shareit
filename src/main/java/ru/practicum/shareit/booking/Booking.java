package ru.practicum.shareit.booking;

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
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "start_date")
    private LocalDateTime start; // дата и время начала бронирования

    @Column(name = "end_date")
    private LocalDateTime end; // дата и время конца бронирования

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "item_id")
//    @ToString.Exclude
    @Column(name = "item_id")
    private int itemId; // вещь, которую пользователь бронирует

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "booker_id")
//    @ToString.Exclude
    @Column(name = "booker_id")
    private int bookerId; // пользователь, который осуществляет бронирование

    @Enumerated
    @Column(name = "status")
    private BookingStatus status; // статус бронирования
}

package ru.practicum.shareit.booking.model;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "booking")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Booking booking = (Booking) o;
        return getId() != null && Objects.equals(getId(), booking.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

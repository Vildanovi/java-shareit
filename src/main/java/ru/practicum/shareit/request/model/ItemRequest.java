package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Entity
@Table(name = "requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    @Id
    private Integer id;
    private String description; // текст запроса, содержащий описание требуемой вещи;
    @Column(name = "requestor_id")
    private int requestor; // пользователь, создавший запрос
    private LocalDateTime created; // дата и время создания запроса

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ItemRequest itemRequest = (ItemRequest) o;
        return getId() != null && Objects.equals(getId(), itemRequest.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

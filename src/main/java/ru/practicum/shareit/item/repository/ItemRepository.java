package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) or upper(i.description) like upper(concat('%', ?2, '%'))) and i.available = true")
    List<Item> findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableTrue(String name, String description);

    List<Item> findAllByOwner_Id(Integer userId);
}
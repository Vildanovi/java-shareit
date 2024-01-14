package ru.practicum.shareit.comments.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Data
@Builder
public class Comment {

    private final int id;
    private final String text;
    private final Item item;
    private final User author;
    private final Instant created;

}

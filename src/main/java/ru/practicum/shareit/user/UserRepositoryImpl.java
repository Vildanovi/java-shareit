package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    protected int generatedId = 0;

    @Override
    public List<User> findAll() {
        if (!users.isEmpty()) {
            return new ArrayList<>(users.values());
        }
        return Collections.emptyList();
    }

    @Override
    public User save(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(int userId) {
        users.remove(userId);
    }

    @Override
    public Optional<User> findUserById(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User update(User user) {
        int userId = user.getId();
        User updateUser = users.get(userId);
        if (updateUser != null) {
            updateUser.setName(user.getName());
            updateUser.setEmail(user.getEmail());
        }
        return updateUser;
    }

    private int getId() {
        return ++generatedId;
    }
}

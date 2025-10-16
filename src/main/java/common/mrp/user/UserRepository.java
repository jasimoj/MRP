package common.mrp.user;

import common.database.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserRepository implements Repository<User, Integer> {
    private final List<User> users;
    private int firstIdForNow = 1;
    public UserRepository() {
        users = new ArrayList<>();
    }
    @Override
    public Optional<User> find(Integer id) {
        return users.stream().filter(u -> u.getId() == id).findFirst();
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User save(User user) {
        if (user.getId() == 0) { // neue ID, wenn noch keine gesetzt
            user.setId(firstIdForNow++);
            users.add(user);
        } else {
            // vorhandenen ersetzen
            find(user.getId()).ifPresentOrElse(existing -> {
                existing.setUsername(user.getUsername());
                existing.setEmail(user.getEmail());
                existing.setFavoriteGenre(user.getFavoriteGenre());
                existing.setPassword(user.getPassword());
            }, () -> users.add(user));
        }
        return user;
    }

    @Override
    public User delete(Integer id) {
       return null; // Wird nicht gebraucht, refactor?
    }

    public Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return users.stream().filter(u -> username.equals(u.getUsername())).findFirst();
    }
}

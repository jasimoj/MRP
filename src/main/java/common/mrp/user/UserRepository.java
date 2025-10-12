package common.mrp.user;

import common.database.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository implements Repository<User, Integer> {
    private final Map<String, User> byUsername = new ConcurrentHashMap<>();

    @Override
    public Optional<User> find(Integer id) {
        return Optional.ofNullable(byUsername.get(username));
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public User save(User user) {
        byUsername.put(user.getUsername(), user);

        return user;
    }

    @Override
    public User delete(Integer id) {
        return null;
    }
}

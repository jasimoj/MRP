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
    public UserRepository() {
        users = new ArrayList<>();
    }
    @Override
    public Optional<User> find(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User save(User user) {
        users.add(user);
        return user;
    }

    @Override
    public User delete(Integer integer) {
        return null;
    }

}

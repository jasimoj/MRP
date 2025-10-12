package common.mrp.user;

import common.database.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserRepository implements Repository<User, Integer> {
    private final Map<Integer, User> byId = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1); // einfache ID-Vergabe

    @Override
    public Optional<User> find(Integer id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(byId.values());
    }

    @Override
    public User save(User user) {
        if (user.getId() == 0) { // oder < 1 – je nach Konvention
            user.setId(seq.getAndIncrement());
        }
        byId.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Integer id) {
        return byId.remove(id);
    }
}

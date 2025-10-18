package testutil;

import common.mrp.user.User;
import common.mrp.user.UserRepository;

import java.util.*;

public class FakeUserRepository extends UserRepository {
    private final Map<Integer, User> byId = new HashMap<>();
    private final Map<String, User> byName = new HashMap<>();
    private int seq = 1;

    @Override
    public Optional<User> find(Integer id) {          // <-- public
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<User> findAll() {                     // optional, falls gebraucht
        return new ArrayList<>(byId.values());
    }

    @Override
    public User save(User u) {
        if (u.getId() == 0) u.setId(seq++);
        byId.put(u.getId(), u);
        byName.put(u.getUsername(), u);
        return u;
    }

    @Override
    public User delete(Integer id) {                  // optional – einfache Variante
        User removed = byId.remove(id);
        if (removed != null) byName.remove(removed.getUsername());
        return removed;
    }

    @Override
    public Optional<User> findByUsername(String name) {
        return Optional.ofNullable(byName.get(name));
    }
}

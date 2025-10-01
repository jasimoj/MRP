package common.mrp.user;

import common.database.Repository;

import java.util.List;
import java.util.Optional;

public class UserRepository implements Repository<User, Integer> {

    @Override
    public Optional<User> find(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public User save(User item) {
        return null;
    }

    @Override
    public User delete(Integer id) {
        return null;
    }
}

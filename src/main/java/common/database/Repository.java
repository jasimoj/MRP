package common.database;

import common.mrp.rating.Rating;

import java.util.List;
import java.util.Optional;

public interface Repository<T,ID> {
    Optional<T> find(ID id);
    List<T> findAll();

    List<T> findAll(Integer userid);

    T save(T item);
    void delete(ID id);

    String SQL_ALREADY_EXISTS_CODE = "23505";
}

package common.database;

import java.util.List;
import java.util.Optional;

public interface Repository<T,ID> {

    Optional<T> find(ID id);
    List<T> findAll();
    T save(T item);
    T delete(ID id);
}

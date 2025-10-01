package common.mrp.media;

import common.database.Repository;

import java.util.List;
import java.util.Optional;

public class MediaRepository implements Repository<Media, Integer> {

    @Override
    public Optional<Media> find(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<Media> findAll() {
        return List.of();
    }

    @Override
    public Media save(Media item) {
        return null;
    }

    @Override
    public Media delete(Integer id) {
        return null;
    }
}

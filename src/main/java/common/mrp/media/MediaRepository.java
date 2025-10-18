package common.mrp.media;

import common.database.Repository;
import common.exception.EntityNotFoundException;
import common.mrp.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MediaRepository implements Repository<Media, Integer> {
    private final List<Media> mediaList;
    private Integer firstIdForNow = 1;

    public MediaRepository() {
        this.mediaList = new ArrayList<>();
    }

    @Override
    public Optional<Media> find(Integer id) {
        return mediaList.stream().filter(u -> Objects.equals(u.getId(), id)).findFirst();
    }

    @Override
    public List<Media> findAll() {
        return mediaList;
    }

    @Override
    public Media save(Media media) {
        if (media.getId() == 0) { // neue ID, wenn noch keine gesetzt
            media.setId(firstIdForNow++);
            mediaList.add(media);
        } else {
            // vorhandenen ersetzen
            find(media.getId()).ifPresentOrElse(existing -> {
                existing.setTitle(media.getTitle());
                existing.setDescription(media.getDescription());
                existing.setGenres(media.getGenres());
                existing.setRatingsCount(media.getRatingsCount());
                existing.setMediaType(media.getMediaType());
                existing.setReleaseYear(media.getReleaseYear());
                existing.setAgeRestriction(media.getAgeRestriction());
                existing.setAvgScore(media.getAvgScore());
            }, () -> mediaList.add(media));
        }
        return media;
    }

    @Override
    public Media delete(Integer id) {
        if (id == null) return null;
        var it = mediaList.iterator();
        while (it.hasNext()) {
            Media m = it.next();
            if (Objects.equals(m.getId(), id)) {
                it.remove();
                return m;
            }
        }
        throw new EntityNotFoundException();
    }
}

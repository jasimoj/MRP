package common.mrp.rating;

import common.database.Repository;
import common.exception.EntityNotFoundException;
import common.mrp.media.Media;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RatingRepository implements Repository<Rating, Integer> {
    private final List<Rating> ratings;
    int firstIdForNow = 1;
    public RatingRepository() {
        this.ratings = new ArrayList<>();
    }
    @Override
    public Optional<Rating> find(Integer id) {
        return ratings.stream().filter(r -> Objects.equals(r.getId(), id)).findFirst();
    }

    @Override
    public List<Rating> findAll() {
        return  ratings;
    }

    @Override
    public Rating save(Rating rating) {
        // Muss noch angepasst werden sobald mal eine DB da ist
        //Media soll auch noch verarbeitet werden
        if (rating.getId() == 0) { // neue ID, wenn noch keine gesetzt
            rating.setId(firstIdForNow++);
            ratings.add(rating);
        } else {
            // vorhandenen ersetzen
            find(rating.getId()).ifPresentOrElse(existing -> {
                existing.setComment(rating.getComment());
                existing.setStars(rating.getStars());
            }, () -> ratings.add(rating));
        }
        return rating;
    }

    @Override
    public Rating delete(Integer id) {
        var it = ratings.iterator();
        while (it.hasNext()) {
            Rating r = it.next();
            if (Objects.equals(r.getId(), id)) {
                it.remove();
                return r;
            }
        }
        throw new EntityNotFoundException("Rating not found");
    }
}

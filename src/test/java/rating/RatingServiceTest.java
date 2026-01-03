package rating;

import common.exception.EntityNotFoundException;
import common.exception.ForbiddenException;
import common.mrp.media.MediaRepository;
import common.mrp.rating.Rating;
import common.mrp.rating.RatingRepository;
import common.mrp.rating.RatingService;
import common.mrp.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
    @Mock
    RatingRepository ratingRepository;
    @Mock
    MediaRepository mediaRepository;
    @Mock
    UserRepository userRepository; // wird im Service zwar nicht genutzt, aber benötigt im ctor

    RatingService service;

    @BeforeEach
    void setup() {
        service = new RatingService(ratingRepository, mediaRepository, userRepository);
    }

    @Test
    void getRating_whenExists_returnsRating() {
        Rating r = new Rating();
        r.setId(10);

        when(ratingRepository.find(10)).thenReturn(Optional.of(r));

        Rating result = service.getRating(10);

        assertEquals(10, result.getId());
        verify(ratingRepository).find(10);
    }

    @Test
    void getRating_whenMissing_throwsEntityNotFound() {
        when(ratingRepository.find(99)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getRating(99));
    }

    @Test
    void getAllRatingsFromUser_returnsList() {
        when(ratingRepository.findAll(2)).thenReturn(List.of(new Rating(), new Rating()));
        List<Rating> list = service.getAllRatingsFromUser(2);
        assertEquals(2, list.size());
        verify(ratingRepository).findAll(2);
    }

    @Test
    void updateRating_whenOwner_updatesAndSaves() {
        Rating existing = new Rating();
        existing.setId(5);
        existing.setUserId(2);
        existing.setStars(3);
        existing.setComment("old");

        Rating patch = new Rating();
        patch.setStars(4);
        patch.setComment("new");

        when(ratingRepository.find(5)).thenReturn(Optional.of(existing));
        when(ratingRepository.save(any(Rating.class))).thenAnswer(inv -> inv.getArgument(0));

        Rating result = service.updateRating(5, patch, 2);

        assertEquals(4, result.getStars());
        assertEquals("new", result.getComment());
        verify(ratingRepository).save(existing);
    }

    @Test
    void updateRating_whenNotOwner_throwsForbidden() {
        Rating existing = new Rating();
        existing.setId(5);
        existing.setUserId(99);

        when(ratingRepository.find(5)).thenReturn(Optional.of(existing));

        assertThrows(ForbiddenException.class, () -> service.updateRating(5, new Rating(), 2));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void rateMedia_whenMediaMissing_throwsEntityNotFound() {
        when(mediaRepository.find(7)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.rateMedia(2, 7, 4, "ok"));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void confirmRatingComment_whenOwner_setsConfirmedAndSaves() {
        Rating r = new Rating();
        r.setId(1);
        r.setUserId(2);
        r.setConfirmed(false);

        when(ratingRepository.find(1)).thenReturn(Optional.of(r));
        when(ratingRepository.save(any(Rating.class))).thenAnswer(inv -> inv.getArgument(0));

        Rating result = service.confirmRatingComment(1, 2);

        assertTrue(result.isConfirmed());
        verify(ratingRepository).save(r);
    }

    @Test
    void confirmRatingComment_whenNotOwner_throwsForbidden() {
        Rating r = new Rating();
        r.setId(1);
        r.setUserId(99);

        when(ratingRepository.find(1)).thenReturn(Optional.of(r));

        assertThrows(ForbiddenException.class, () -> service.confirmRatingComment(1, 2));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void deleteRating_whenOwner_deletes() {
        Rating r = new Rating();
        r.setId(1);
        r.setUserId(2);

        when(ratingRepository.find(1)).thenReturn(Optional.of(r));

        service.deleteRating(1, 2);

        verify(ratingRepository).delete(1);
    }

    @Test
    void deleteRating_whenNotOwner_throwsForbidden() {
        Rating r = new Rating();
        r.setId(1);
        r.setUserId(99);

        when(ratingRepository.find(1)).thenReturn(Optional.of(r));

        assertThrows(ForbiddenException.class, () -> service.deleteRating(1, 2));
        verify(ratingRepository, never()).delete(anyInt());
    }

}

package media;

import common.exception.EntityNotFoundException;
import common.exception.ForbiddenException;
import common.mrp.media.Media;
import common.mrp.media.MediaInput;
import common.mrp.media.MediaRepository;
import common.mrp.media.MediaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {

    @Mock
    MediaRepository  mediaRepository;

    @InjectMocks
    MediaService mediaService;

    @Test
    void createMedia_Valid() {
        int userId = 3;
        MediaInput in = new MediaInput();

        in.setTitle("Inception");
        in.setMediaType("movie");
        in.setReleaseYear(2010);
        in.setAgeRestriction(12);
        in.setDescription("Sci-fi thriller");
        in.setGenres(List.of("sci-fi"));

        Media saved = new Media();
        saved.setId(1);
        saved.setTitle("Inception");
        saved.setCreatedByUserId(userId);

        when(mediaRepository.save(any(Media.class))).thenReturn(saved);

        Media result = mediaService.createMedia(in, userId);

        assertEquals(1, result.getId());
        assertEquals("Inception", result.getTitle());
        assertEquals(userId, result.getCreatedByUserId());

        ArgumentCaptor<Media> captor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(captor.capture());
        assertEquals(userId, captor.getValue().getCreatedByUserId());
        assertEquals("Inception", captor.getValue().getTitle());

    }

    @Test
    void deleteMedia_notCreator_throws() {
        int mediaId = 10;
        int creatorId = 1;
        int otherUser = 2;

        Media existing = new Media();
        existing.setId(mediaId);
        existing.setCreatedByUserId(creatorId);

        when(mediaRepository.find(mediaId)).thenReturn(java.util.Optional.of(existing));

        assertThrows(ForbiddenException.class, () -> mediaService.deleteMedia(mediaId, otherUser));
        verify(mediaRepository, never()).delete(anyInt());
    }

    @Test
    void getMedia_returnsMedia_whenExists() {
        int id = 1;
        Media m = new Media();
        m.setId(id);
        m.setTitle("Inception");

        when(mediaRepository.find(id)).thenReturn(Optional.of(m));

        Media result = mediaService.getMedia(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Inception", result.getTitle());
        verify(mediaRepository, times(1)).find(id);
        verifyNoMoreInteractions(mediaRepository);
    }

    @Test
    void getMedia_throws_whenNotFound() {
        int mediaId = 99;
        when(mediaRepository.find(mediaId)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mediaService.getMedia(mediaId));
        verify(mediaRepository, times(1)).find(mediaId);
        verifyNoMoreInteractions(mediaRepository);
    }
}

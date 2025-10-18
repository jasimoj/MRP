package common.mrp.media;

import common.exception.EntityNotFoundException;
import common.exception.ForbiddenException;
import common.exception.MissingRequiredFieldsException;

import java.util.List;

public class MediaService {
    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Media getMedia(int id) {
        return mediaRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    public Media updateMedia(int mediaId, MediaInput media, int currentUserId) {
        Media updatedMedia = mediaRepository.find(mediaId)
                .orElseThrow(EntityNotFoundException::new);
        if (updatedMedia.getCreatedByUserId() != currentUserId) {
            throw new ForbiddenException();
        }
        updatedMedia.setTitle(media.getTitle());
        updatedMedia.setGenres(media.getGenres());
        updatedMedia.setMediaType(media.getMediaType());
        updatedMedia.setAgeRestriction(media.getAgeRestriction());
        updatedMedia.setReleaseYear(media.getReleaseYear());
        return mediaRepository.save(updatedMedia);
    }

    public Media createMedia(MediaInput media, int currentUserId) {
        if (media == null
                || media.getTitle() == null
                || media.getDescription() == null
                || media.getGenres() == null
                || media.getMediaType() == null
                || media.getAgeRestriction() == null
                || media.getReleaseYear() == null) {
            throw new MissingRequiredFieldsException("Missing required fields");
        }

        Media newMedia = new Media();

        newMedia.setTitle(media.getTitle());
        newMedia.setTitle(media.getTitle());
        newMedia.setGenres(media.getGenres());
        newMedia.setMediaType(media.getMediaType());
        newMedia.setAgeRestriction(media.getAgeRestriction());
        newMedia.setReleaseYear(media.getReleaseYear());
        newMedia.setDescription(media.getDescription());
        newMedia.setCreatedByUserId(currentUserId);
        return mediaRepository.save(newMedia);
    }

    public Media deleteMedia(int mediaId,  int currentUserId) {
        Media m = mediaRepository.find(mediaId).orElseThrow(EntityNotFoundException::new);
        if(m.getCreatedByUserId() != currentUserId){
            throw new ForbiddenException();
        }
        return mediaRepository.delete(mediaId);
    }


}

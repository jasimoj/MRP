package common.mrp.media;

import common.exception.EntityNotFoundException;
import common.exception.MissingRequiredFieldsException;

import java.util.List;

public class MediaService {
    private final MediaRepository mediaRepository;
    public MediaService(MediaRepository mediaRepository){
       this.mediaRepository = mediaRepository;
    }

    public Media getMedia(Integer id){
        return mediaRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<Media> getAllMedia(){
        return mediaRepository.findAll();
    }

    public Media updateMedia(Integer id, MediaInput media){
        Media updatedMedia = mediaRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
        updatedMedia.setTitle(media.getTitle());
        updatedMedia.setGenres(media.getGenres());
        updatedMedia.setMediaType(media.getMediaType());
        updatedMedia.setAgeRestriction(media.getAgeRestriction());
        updatedMedia.setReleaseYear(media.getReleaseYear());
        return mediaRepository.save(updatedMedia);
    }

    public Media createMedia(MediaInput media) {
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
        return  mediaRepository.save(newMedia);
    }

    public Media deleteMedia(Integer id){
        return mediaRepository.delete(id);
    }


}

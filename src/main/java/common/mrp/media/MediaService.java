package common.mrp.media;

public class MediaService {
    private final MediaRepository mediaRepository;
    public MediaService(MediaRepository mediaRepository){
       this.mediaRepository = mediaRepository;
    }
}

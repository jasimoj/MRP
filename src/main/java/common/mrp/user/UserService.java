package common.mrp.user;

import common.exception.EntityNotFoundException;
import common.mrp.media.MediaRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

    public UserService( UserRepository userRepository, MediaRepository mediaRepository) {
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
    }

    public User getUser(int id){
        return userRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public UserProfile getProfile(int id){
        return userRepository.getProfile(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<String> getUserFavorites(int id){
        return mediaRepository.findFavoritesByUserId(id);
    }

    public void updateUser(int id, UserProfileUpdate user){
        User updatedUser = userRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
        updatedUser.setEmail(user.getEmail());
        updatedUser.setFavoriteGenre(user.getFavoriteGenre());
        userRepository.save(updatedUser);
    }

    public void deleteUser(int id){
         userRepository.delete(id);
    }

}

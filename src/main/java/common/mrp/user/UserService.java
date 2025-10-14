package common.mrp.user;

import common.exception.EntityNotFoundException;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService( UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Integer id){
        return userRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User updateUser(Integer id, User user){
        User updatedUser = userRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
        user.setUsername(user.getUsername());
        user.setEmail(user.getEmail());
        user.setFavoriteGenre(user.getFavoriteGenre());
        return userRepository.save(updatedUser);
    }

    public User deleteUser(Integer id){
        return userRepository.delete(id);
    }



}

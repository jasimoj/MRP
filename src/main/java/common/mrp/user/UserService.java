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

    //Aktuell nur platzhalter bis DB da ist (falls es hier überhaupt gebraucht wird)
    public User getUserFavorites(Integer id){
        return userRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    //Für leaderboard später?
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void updateUser(Integer id, UserProfileUpdate user){
        User updatedUser = userRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
        updatedUser.setEmail(user.getEmail());
        updatedUser.setFavoriteGenre(user.getFavoriteGenre());
        userRepository.save(updatedUser);
    }

    public User deleteUser(Integer id){
        return userRepository.delete(id);
    }



}

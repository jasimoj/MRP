package common.mrp.user;

import common.exception.EntityNotFoundException;

import java.util.List;

public class UserService {
    // Macht ein ServiceImpl sinn?
    //Dann müsste man alle Services zu Interfaces machen und eine extra serviceImpl Klasse machen
    // die den service implementiert
    private final UserRepository userRepository;
    public UserService() {
        this.userRepository = new UserRepository();
    }
    public User getUser(Integer id){
        return userRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }
    public User createUser(User user){
        User newUser = new User(user.getUsername());
        return userRepository.save(newUser);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User deleteUser(Integer id){
        return userRepository.delete(id);
    }
}

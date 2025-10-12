package common.mrp.auth;

import common.mrp.user.User;
import common.mrp.user.UserRepository;

public class AuthService {
    // Macht ein ServiceImpl sinn?
    //Dann müsste man alle Services zu Interfaces machen und eine extra serviceImpl Klasse machen
    // die den service implementiert

    private final UserRepository userRepository;
    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public User registerUser(User user){
        return userRepository.save(user);
    }

    public User loginUser(User user){
        return userRepository.save(user);
    }


}

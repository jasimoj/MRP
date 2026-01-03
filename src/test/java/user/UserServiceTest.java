package user;

import common.exception.EntityNotFoundException;
import common.mrp.media.MediaRepository;
import common.mrp.user.User;
import common.mrp.user.UserProfile;
import common.mrp.user.UserRepository;
import common.mrp.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;
    private MediaRepository mediaRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        mediaRepository = Mockito.mock(MediaRepository.class);

        userService = new UserService(userRepository, mediaRepository);
    }

    @Test
    void getUser_returnsUser_whenFound() {
        int userId = 1;
        User u = new User();
        u.setId(userId);
        u.setUsername("mustermann");

        when(userRepository.find(userId)).thenReturn(Optional.of(u));

        User result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("mustermann", result.getUsername());
        verify(userRepository).find(userId);
    }

    @Test
    void getUser_throws_whenNotFound() {
        // arrange
        int userId = 999;
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        // act + assert
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));
        verify(userRepository).find(userId);
    }

    @Test
    void getProfile_returnsProfile_whenUserExists() {
        UserProfile p = new UserProfile();
        p.setId(2);
        p.setUsername("user2");
        p.setEmail("user2@example.com");
        p.setFavoriteGenre("drama");
        p.setTotalRatings(0);
        p.setAvgStars(0.0);

        when(userRepository.getProfile(2)).thenReturn(Optional.of(p));

        UserProfile result = userService.getProfile(2);

        assertEquals(2, result.getId());
        assertEquals("user2", result.getUsername());
        assertEquals("user2@example.com", result.getEmail());
        assertEquals("drama", result.getFavoriteGenre());
        assertEquals(0, result.getTotalRatings());
        assertEquals(0.0, result.getAvgStars(), 0.0001);

        verify(userRepository).getProfile(2);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getProfile_throws_whenNotFound() {

        when(userRepository.getProfile(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getProfile(2));
        verify(userRepository).getProfile(2);
    }
}

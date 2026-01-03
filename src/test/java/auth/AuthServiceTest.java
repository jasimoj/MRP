package auth;

import common.exception.CredentialMissmatchException;
import common.mrp.auth.AuthPrincipal;
import common.mrp.auth.AuthService;
import common.mrp.auth.UserCredentials;
import common.mrp.user.User;
import common.mrp.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    private UserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        authService = new AuthService(userRepository);
    }

    @Test
    void registerUser_success_savesUser() {
        UserCredentials creds = new UserCredentials();
        creds.setUsername("alice");
        creds.setPassword("secret");

        User saved = new User();
        saved.setId(10);
        saved.setUsername("alice");
        saved.setPassword("secret");
        saved.setEmail(null);

        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = authService.registerUser(creds);

        assertEquals(10, result.getId());
        assertEquals("alice", result.getUsername());
        assertEquals("secret", result.getPassword());
        assertNull(result.getEmail());

        // prüfen, was wirklich ans repo ging
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User toSave = captor.getValue();
        assertEquals("alice", toSave.getUsername());
        assertEquals("secret", toSave.getPassword());
        assertNull(toSave.getEmail());
    }
    @Test
    void registerUser_missingUsername_throws() {
        UserCredentials creds = new UserCredentials();
        creds.setUsername("");
        creds.setPassword("x");

        assertThrows(CredentialMissmatchException.class, () -> authService.registerUser(creds));
        verifyNoInteractions(userRepository);
    }

    @Test
    void getUserToken_wrongPassword_throws() {
        UserCredentials creds = new UserCredentials();
        creds.setUsername("bob");
        creds.setPassword("wrong");

        User u = new User();
        u.setId(2);
        u.setUsername("bob");
        u.setPassword("pw");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(u));

        assertThrows(CredentialMissmatchException.class, () -> authService.getUserToken(creds));
    }

    @Test
    void getUserToken_userNotFound_throws() {
        UserCredentials creds = new UserCredentials();
        creds.setUsername("nobody");
        creds.setPassword("pw");

        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(CredentialMissmatchException.class, () -> authService.getUserToken(creds));
    }

    @Test
    void verifyFromAuthorizationHeader_validToken_returnsPrincipal() {
        User u = new User();
        u.setId(7);
        u.setUsername("charlie");

        when(userRepository.findByUsername("charlie")).thenReturn(Optional.of(u));

        Optional<AuthPrincipal> p = authService.verifyFromAuthorizationHeader("Bearer charlie-mrpToken");

        assertTrue(p.isPresent());
        assertEquals(7, p.get().getUserId());
        assertEquals("charlie", p.get().getUsername());
    }

    @Test
    void verifyFromAuthorizationHeader_wrongPrefix_returnsEmpty() {
        Optional<AuthPrincipal> p = authService.verifyFromAuthorizationHeader("Basic charlie-mrpToken");
        assertTrue(p.isEmpty());
        verifyNoInteractions(userRepository);
    }

    @Test
    void verifyFromAuthorizationHeader_wrongSuffix_returnsEmpty() {
        Optional<AuthPrincipal> p = authService.verifyFromAuthorizationHeader("Bearer charlie-xyz");
        assertTrue(p.isEmpty());
        verifyNoInteractions(userRepository);
    }
}

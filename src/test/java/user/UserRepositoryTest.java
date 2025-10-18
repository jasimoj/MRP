package user;

import org.junit.jupiter.api.Test;
import testutil.FakeUserRepository;
import testutil.Users;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    @Test
    void save_assigns_ids_and_findByUsername() {
        var repo = new FakeUserRepository();
        var a = repo.save(Users.u("alice", "pw"));
        var b = repo.save(Users.u("bob", "pw"));

        assertTrue(a.getId() > 0);
        assertEquals(a.getId() + 1, b.getId());
        assertTrue(repo.findByUsername("alice").isPresent());
        assertTrue(repo.find(a.getId()).isPresent());
    }
}
package pl.sk.coinTracker.User;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Test
    void test_created_user() {
        var mockUserRepository = mock(UserRepository.class);
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setId(1L);

        when(mockUserRepository.save(user)).thenReturn(user);
        var testService = new UserService(mockUserRepository, new BCryptPasswordEncoder());
        User createdUser = testService.registerUser(user);

        assertEquals(user.getId(), createdUser.getId());
        assertEquals(user.getUsername(), createdUser.getUsername());
        assertEquals(user.getPassword(), createdUser.getPassword());
        assertEquals(createdUser.getRole(), "ROLE_USER");
    }

    @Test
    void check_if_username_exsist_and_return_true() {
        var userRepository = inMemoryUserRepository();

        User user = new User();
        user.setId(1L);
        user.setUsername("user");

        var testService = new UserService(userRepository, null);
        userRepository.save(user);

        assertTrue(testService.usernameExists("user"));

    }

    @Test
    void check_if_username_exist_and_return_false() {
        var userRepository = inMemoryUserRepository();

        User user = new User();
        user.setId(1L);
        user.setUsername("user2");

        var testService = new UserService(userRepository, null);
        userRepository.save(user);

        assertFalse(testService.usernameExists("user"));

    }

    @Test
    void check_if_email_exist_and_return_true(){
        var userRepository = inMemoryUserRepository();

        User user = new User();
        user.setId(1L);
        user.setEmail("email");

        var testService = new UserService(userRepository, null);
        userRepository.save(user);

        assertTrue(testService.emailExists("email"));
    }

    @Test
    void check_if_email_exist_and_return_false(){
        var userRepository = inMemoryUserRepository();

        User user = new User();
        user.setId(1L);
        user.setEmail("email");

        var testService = new UserService(userRepository, null);
        userRepository.save(user);

        assertFalse(testService.emailExists("email2"));

    }

    @Test
    void get_user_from_username_and_return_null(){
        var mockUserRepository = mock(UserRepository.class);
        when(mockUserRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        var testService = new UserService(mockUserRepository, null);

        assertNull(testService.getUserFromUsernamne("string"));
    }

    @Test
    void get_user_from_username_and_return_user(){
        var userRepository = inMemoryUserRepository();

        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setEmail("email");

        var testService = new UserService(userRepository, null);
        userRepository.save(user);

        User givenUser = testService.getUserFromUsernamne("user");

        assertEquals(user.getId(),givenUser.getId());
        assertEquals(user.getUsername(),givenUser.getUsername());
        assertEquals(user.getEmail(), givenUser.getEmail());
    }

    @Test
    void should_return_given_user_id(){
        var userRepository = inMemoryUserRepository();

        User user = new User();
        user.setId(1L);
        user.setUsername("user");

        var testService = new UserService(userRepository, null);
        userRepository.save(user);

        assertEquals(testService.getUserIdFromUsername("user"),1L);

    }

    private UserRepository inMemoryUserRepository() {
        Map<Long, User> users = new HashMap<>();
        return new UserRepository() {
            @Override
            public Optional<User> findByUsername(String username) {
                Optional<User> result = users.values().stream()
                        .filter(user -> user.getUsername().equals(username))
                        .findFirst();
                return result.isPresent() ? result : Optional.empty();
            }

            @Override
            public Optional<User> findByEmail(String email) {
                Optional<User> result = users.values().stream()
                        .filter(user -> user.getEmail().equals(email))
                        .findFirst();
                return result.isPresent() ? result : Optional.empty();
            }

            @Override
            public User save(User user) {
                return users.put(user.getId(), user);
            }
        };
    }

}
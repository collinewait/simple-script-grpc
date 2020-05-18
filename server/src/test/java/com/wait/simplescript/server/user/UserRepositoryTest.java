package com.wait.simplescript.server.user;

import com.wait.simplescript.server.infrastructure.SpringProfiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles(SpringProfiles.TEST)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    public void givenValidDetails_thenUserShouldBeSavedAndReturned() {
        User user = repository.save(Users.user());
        assertThat(user).isNotNull();
        assertThat(repository.count()).isEqualTo(1L);
        assertEquals(Users.FIRST_NAME, user.getFirstName());
    }

    @Test
    public void givenValidEmail_thenUserShouldBeReturned() {
        repository.save(Users.user());
        Optional<User> optionalUser =
                repository.findByEmail(Users.USER_EMAIL);
        assertThat(optionalUser).isNotEmpty();
    }

    @Test
    public void givenInvalidEmail_thenAnEmptyResponseShouldBeReturned() {
        Optional<User> optionalUser =
                repository.findByEmail(Users.USER_EMAIL);
        assertThat(optionalUser).isEmpty();
    }

    @Test
    public void givenUsersExist_thenUsersShouldBeReturned() {
        User anotherUser = User.createUSer("userN", "userL",
                "user@bego.com", "pass", Users.USER_ROLES);
        anotherUser.setId("someId44");
        repository.save(Users.admin());
        repository.save(Users.user());
        repository.save(anotherUser);

        assertEquals(3, repository.count());
        List<User> users = repository.findByIdNot(Users.ADMIN_ID);
        assertEquals(2, users.size());
        assertThat(users).extracting(User::getEmail).contains("user@bego.com");
        assertThat(users).extracting(User::getEmail).contains(Users.USER_EMAIL);
        assertThat(users).extracting(User::getEmail).doesNotContain(Users.ADMIN_EMAIL);
    }
}

package com.wait.simplescript.server.user;

import com.wait.simplescript.server.infrastructure.SpringProfiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles(SpringProfiles.TEST)
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    public void testCreateUser() {
        User user = repository.save(Users.user());
        assertThat(user).isNotNull();
        assertThat(repository.count()).isEqualTo(1L);
        assertEquals(Users.FIRST_NAME, user.getFirstName());
    }

    @Test
    public void testFindByEmail() {
        repository.save(Users.user());
        Optional<User> optionalUser = repository.findByEmail(Users.USER_EMAIL);
        assertThat(optionalUser).isNotEmpty();
    }

    @Test
    public void testFindByEmailWithUnknownEmail() {
        Optional<User> optionalUser = repository.findByEmail(Users.USER_EMAIL);
        assertThat(optionalUser).isEmpty();
    }
}

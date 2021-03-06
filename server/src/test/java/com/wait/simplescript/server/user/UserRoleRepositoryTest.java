package com.wait.simplescript.server.user;

import com.wait.simplescript.server.infrastructure.SpringProfiles;
import com.wait.simplescript.server.user.role.ERole;
import com.wait.simplescript.server.user.role.UserRole;
import com.wait.simplescript.server.user.role.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles(SpringProfiles.TEST)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRoleRepositoryTest {
    @Autowired
    private UserRoleRepository repository;

    @Test
    public void givenValidDetails_thenUserRoleShouldBeSavedAndReturned() {
        UserRole saveRole = repository.save(Users.USER_ROLE);
        assertThat(saveRole).isNotNull();
        assertThat(repository.count()).isEqualTo(1L);
    }

    @Test
    public void givenValidName_thenUserRoleShouldBeReturned() {
        repository.save(Users.USER_ROLE);
        Optional<UserRole> optionalRole = repository.findByName(ERole.USER);
        assertThat(optionalRole).isNotEmpty();
    }

    @Test
    public void givenInvalidName_thenEmptyResponseShouldBeReturned() {
        Optional<UserRole> optionalRole = repository.findByName(ERole.USER);
        assertThat(optionalRole).isEmpty();
    }
}

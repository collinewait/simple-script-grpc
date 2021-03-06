package com.wait.simplescript.server.script;

import com.wait.simplescript.server.infrastructure.SpringProfiles;
import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles(SpringProfiles.TEST)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ScriptRepositoryTest {
    @Autowired
    private ScriptRepository repository;

    @Test
    public void givenValidDetails_thenScriptShouldBeSavedAndReturned() {
        Script script = repository.save(Scripts.SINGLE_OPERATION_SCRIPT);
        assertThat(script).isNotNull();
        assertThat(repository.count()).isEqualTo(1L);
    }

    @Test
    public void givenValidId_thenScriptShouldBeReturned() {
        repository.save(Scripts.SINGLE_OPERATION_SCRIPT);
        Optional<Script> optionalScript =
                repository.findById(Scripts.SCRIPT_ID);
        assertThat(optionalScript).isNotEmpty();
        assertEquals(Scripts.SCRIPT_ID, optionalScript.get().getId());
    }

    @Test
    public void givenInvalidId_thenScriptShouldNotBeReturned() {
        Optional<Script> optionalScript =
                repository.findById(Scripts.SCRIPT_ID);
        assertThat(optionalScript).isEmpty();
    }

    @Test
    public void givenScriptsExist_thenScriptsShouldBeReturned() {
        repository.save(Scripts.SINGLE_OPERATION_SCRIPT);
        repository.save(Scripts.MULTIPLE_OPERATIONS_SCRIPT);
        List<Script> scripts = repository.findAll();
        assertThat(scripts).isNotNull();
        assertEquals(2, scripts.size());
    }

    @Test
    public void givenNoScripts_thenEmptyListShouldBeReturned() {
        List<Script> scripts = repository.findAll();
        assertThat(scripts).isNotNull();
        assertEquals(0, scripts.size());
    }

    @Test
    public void givenValidDetails_thenAnUpdatedScriptShouldBeReturned() {
        Script mockScript = Script.createScript(Users.user(),
                Scripts.SINGLE_SCRIPT_VALUE, new ArrayList<>());
        mockScript.setId("validDetails34");
        Script script = repository.save(mockScript);
        script.setScriptValue(Scripts.MULTIPLE_OPERATIONS_SCRIPT_VALUE);

        Script updatedScript = repository.save(script);
        assertThat(updatedScript).isNotNull();
        assertEquals(Scripts.MULTIPLE_OPERATIONS_SCRIPT_VALUE, updatedScript.getScriptValue());
        assertEquals(1, repository.count());
    }

    @Test
    public void givenValidId_thenScriptShouldBeDeleted() {
        Script mockScript = Script.createScript(Users.user(),
                Scripts.SINGLE_SCRIPT_VALUE, new ArrayList<>());
        mockScript.setId("validDetails34");
        Script savedScript = repository.save(mockScript);
        assertEquals(1, repository.count());

        repository.deleteById(savedScript.getId());
        assertEquals(0, repository.count());
    }

    @Test
    public void givenUserId_thenUserScriptsShouldBeReturned() {
        User mockUser = User.createUSer("first", "bob",
                "colline@wait.com", "pass", Users.USER_ROLES);
        mockUser.setId("phew");
        Script mockScript = Script.createScript(mockUser,
                Scripts.SINGLE_SCRIPT_VALUE, new ArrayList<>());
        mockScript.setId("validDetails34");
        repository.save(Scripts.SINGLE_OPERATION_SCRIPT);
        repository.save(Scripts.MULTIPLE_OPERATIONS_SCRIPT);
        repository.save(mockScript);

        assertEquals(3, repository.count());

        List<Script> scripts = repository.findByUser(Users.USER_ID);

        System.out.println(scripts);
        assertEquals(2, scripts.size());
    }
}

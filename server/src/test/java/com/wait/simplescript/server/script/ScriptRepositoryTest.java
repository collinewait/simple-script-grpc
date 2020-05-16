package com.wait.simplescript.server.script;

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
}

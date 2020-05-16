package com.wait.simplescript.server.script;

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
public class ScriptRepositoryTest {
    @Autowired
    private ScriptRepository repository;

    @Test
    public void testCreateScript() {
        Script script = repository.save(Scripts.SINGLE_OPERATION_SCRIPT);
        assertThat(script).isNotNull();
        assertThat(repository.count()).isEqualTo(1L);
    }

    @Test
    public void testFindByIdWithKnownId() {
        repository.save(Scripts.SINGLE_OPERATION_SCRIPT);
        Optional<Script> optionalScript = repository.findById(Scripts.SCRIPT_ID);
        assertThat(optionalScript).isNotEmpty();
        assertEquals(Scripts.SCRIPT_ID, optionalScript.get().getId());
    }

    @Test
    public void testFindByIdWithUnknownId() {
        Optional<Script> optionalScript = repository.findById(Scripts.SCRIPT_ID);
        assertThat(optionalScript).isEmpty();
    }
}

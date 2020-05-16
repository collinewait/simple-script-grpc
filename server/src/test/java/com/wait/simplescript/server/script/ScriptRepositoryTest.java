package com.wait.simplescript.server.script;

import com.wait.simplescript.server.infrastructure.SpringProfiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

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
}

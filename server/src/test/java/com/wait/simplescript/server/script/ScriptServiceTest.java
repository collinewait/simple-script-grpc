package com.wait.simplescript.server.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScriptServiceTest {
    private ScriptService scriptService;
    private ScriptRepository scriptRepository;

    @BeforeEach
    public void setUp() {
        scriptRepository = mock(ScriptRepository.class);
        scriptService = new ScriptServiceImpl(scriptRepository);
    }

    @Nested
    class FindById {
        @Test
        public void givenValidId_thenScriptShouldBeReturned() {
            when(scriptRepository.findById(anyString()))
                    .thenReturn(Optional.of(Scripts.SINGLE_OPERATION_SCRIPT));
            Optional<Script> script = scriptService.findById(Scripts.SCRIPT_ID);
            assertThat(script).isNotEmpty();
            assertEquals(Scripts.SCRIPT_ID, script.get().getId());
        }

        @Test
        public void givenInvalidId_thenScriptShouldNotBeReturned() {
            when(scriptRepository.findById(anyString()))
                    .thenReturn(Optional.empty());
            Optional<Script> script = scriptService.findById(Scripts.SCRIPT_ID);
            assertThat(script).isEmpty();
        }
    }
}

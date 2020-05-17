package com.wait.simplescript.server.script;

import com.wait.simplescript.server.user.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ScriptServiceTest {
    private ScriptService scriptService;
    private ScriptRepository scriptRepository;

    @BeforeEach
    public void setUp() {
        scriptRepository = mock(ScriptRepository.class);
        scriptService = new ScriptServiceImpl(scriptRepository);
    }

    @Nested
    class CreateScript {
        @Test
        public void givenValidDetails_thenScriptShouldBeReturned() {
            when(scriptRepository.save(any(Script.class))).thenReturn(Scripts
                    .SINGLE_OPERATION_SCRIPT);
            Script script = scriptService.createScript(Users.user(),
                    Scripts.SINGLE_SCRIPT_VALUE);
            assertThat(script).isNotNull();
            assertEquals(Scripts.SCRIPT_ID, script.getId());
        }
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

    @Nested
    class FindAll {
        @Test
        public void givenScriptsExists_thenScriptsShouldBeReturned() {
            when(scriptRepository.findAll())
                    .thenReturn(Arrays.asList(Scripts.SINGLE_OPERATION_SCRIPT
                            , Scripts.MULTIPLE_OPERATIONS_SCRIPT));
            List<Script> scripts = scriptService.findAll();
            assertThat(scripts).isNotEmpty();
            assertEquals(2, scripts.size());
        }

        @Test
        public void givenNoScripts_thenEmptyListShouldBeReturned() {
            when(scriptRepository.findAll())
                    .thenReturn(Collections.emptyList());
            List<Script> scripts = scriptService.findAll();
            assertThat(scripts).isNotNull();
            assertEquals(0, scripts.size());
        }
    }

    @Nested
    class Update {
        @Test
        public void givenValidDetails_thenAnUpdatedScriptShouldBeReturned() {
            when(scriptRepository.save(any(Script.class))).thenReturn(Scripts
                    .MULTIPLE_OPERATIONS_SCRIPT);

            Script script = scriptService
                    .update(Scripts.SINGLE_OPERATION_SCRIPT);

            assertThat(script).isNotNull();
            assertEquals(Scripts.MULTIPLE_OPERATIONS_SCRIPT_VALUE,
                    script.getScriptValue());
        }
    }

    @Nested
    class Delete {
        @Test
        public void givenValidId_thenScriptShouldBeDeleted() {
            doNothing().when(scriptRepository).deleteById(anyString());

            String status = scriptService.deleteById("validId34");
            assertEquals("success", status);
        }
    }
}

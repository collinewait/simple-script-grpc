package com.wait.simplescript.server.script;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScriptUtilsTest {

    @Test
    public void testGenerateScriptWithSingleOperation() {
        List<String> operations =
                Collections.singletonList(ScriptUtils.DO_THIS);
        String scriptValue = ScriptUtils.generateScript(operations);
        assertEquals(ScriptUtils.DO_THIS, scriptValue);
    }

    @Test
    public void testGenerateScriptWithMultipleOperations() {
        String expectedScriptValue = String.format("%s\n%s",
                ScriptUtils.DO_THIS,
                ScriptUtils.DO_THIS);
        List<String> operations = Arrays.asList(ScriptUtils.DO_THIS,
                ScriptUtils.DO_THIS);
        String scriptValue = ScriptUtils.generateScript(operations);
        assertEquals(expectedScriptValue, scriptValue);
    }

    @Test
    public void testGenerateScriptWithoutOperations() {
        List<String> operations = new ArrayList<>();
        Exception exception = assertThrows(InvalidOperationException.class,
                () -> ScriptUtils.generateScript(operations));
        assertThat(exception.getMessage()).contains(ScriptUtils.MISSING_OPERATIONS_MSG);
    }

    @Test
    public void testGenerateScriptWithSingleInvalidStringOperation() {
        List<String> operations = Collections.singletonList("");
        Exception exception = assertThrows(InvalidOperationException.class,
                () -> ScriptUtils.generateScript(operations));
        assertThat(exception.getMessage()).contains(ScriptUtils.INVALID_OPERATIONS_MSG);
    }

    @Test
    public void testGenerateScriptWithMultipleInvalidStringOperations() {
        List<String> operations = Arrays.asList("", "");
        Exception exception = assertThrows(InvalidOperationException.class,
                () -> ScriptUtils.generateScript(operations));
        assertThat(exception.getMessage()).contains(ScriptUtils.INVALID_OPERATIONS_MSG);
    }

    @Test
    public void testGenerateScriptWithLastInvalidStringOperations() {
        List<String> operations = Arrays.asList(ScriptUtils.DO_THIS,
                "SomeInvlidOps");
        Exception exception = assertThrows(InvalidOperationException.class,
                () -> ScriptUtils.generateScript(operations));
        assertThat(exception.getMessage()).contains(ScriptUtils.INVALID_OPERATIONS_MSG);
    }
}

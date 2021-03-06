package com.wait.simplescript.server.script;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScriptUtilsTest {

    @Nested
    class GenerateScript {
        @Test
        public void givenValidSingleOperation_thenScriptValueShouldBeReturned() {
            List<String> operations =
                    Collections.singletonList(ScriptUtils.DO_THIS);
            String scriptValue = ScriptUtils.generateScript(operations);
            assertEquals(ScriptUtils.DO_THIS, scriptValue);
        }

        @Test
        public void givenValidMultipleOperations_thenScriptValueShouldBeReturned() {
            String expectedScriptValue = String.format("%s\n%s",
                    ScriptUtils.DO_THIS,
                    ScriptUtils.DO_THIS);
            List<String> operations = Arrays.asList(ScriptUtils.DO_THIS,
                    ScriptUtils.DO_THIS);
            String scriptValue = ScriptUtils.generateScript(operations);
            assertEquals(expectedScriptValue, scriptValue);
        }

        @Test
        public void givenMissingOperations_thenInvalidOperationExceptionShouldBeThrown() {
            List<String> operations = new ArrayList<>();
            Exception exception = assertThrows(InvalidOperationException.class,
                    () -> ScriptUtils.generateScript(operations));
            assertThat(exception.getMessage()).contains(ScriptUtils.MISSING_OPERATIONS_MSG);
        }

        @Test
        public void givenSingleInvalidOperation_thenInvalidOperationExceptionShouldBeThrown() {
            List<String> operations = Collections.singletonList("");
            Exception exception = assertThrows(InvalidOperationException.class,
                    () -> ScriptUtils.generateScript(operations));
            assertThat(exception.getMessage()).contains(ScriptUtils.INVALID_OPERATIONS_MSG);
        }

        @Test
        public void givenMultipleInvalidOperations_thenInvalidOperationExceptionShouldBeThrown() {
            List<String> operations = Arrays.asList("", "");
            Exception exception = assertThrows(InvalidOperationException.class,
                    () -> ScriptUtils.generateScript(operations));
            assertThat(exception.getMessage()).contains(ScriptUtils.INVALID_OPERATIONS_MSG);
        }

        @Test
        public void givenLastInvalidStringOperations_thenInvalidOperationExceptionShouldBeThrown() {
            List<String> operations = Arrays.asList(ScriptUtils.DO_THIS,
                    "SomeInvlidOps");
            Exception exception = assertThrows(InvalidOperationException.class,
                    () -> ScriptUtils.generateScript(operations));
            assertThat(exception.getMessage()).contains(ScriptUtils.INVALID_OPERATIONS_MSG);
        }
    }

    @Nested
    class ExecuteScriptValue {
        @Test
        public void givenSingleScriptValueStartsWithDoThis_thenExpectedOutputShouldBeReturned() {
            String scriptValue = ScriptUtils.DO_THIS;
            List<String> output = ScriptUtils.executeScriptValue(scriptValue);
            assertEquals(1, output.size());
            assertEquals("19", output.get(0));
        }

        @Test
        public void givenSingleScriptValueStartsWithDoThat_thenExpectedOutputShouldBeReturned() {
            String scriptValue = ScriptUtils.DO_THAT;
            List<String> output = ScriptUtils.executeScriptValue(scriptValue);
            assertEquals(1, output.size());
            assertEquals("ger)", output.get(0));
        }

        @Test
        public void givenSingleScriptValueStartsWithDoTheOther_thenExpectedOutputShouldBeReturned() {
            String scriptValue = ScriptUtils.DO_THE;
            List<String> output = ScriptUtils.executeScriptValue(scriptValue);
            assertEquals(1, output.size());
            assertEquals("DoTheOtherThing(float)", output.get(0));
        }

        @Test
        public void givenMultipleScriptContainsBothDOThaTAndDoThis_thenExpectedOutputShouldBeReturned() {
            String scriptValue = String.format("%s\n%s", ScriptUtils.DO_THIS, ScriptUtils.DO_THAT);
            List<String> output = ScriptUtils.executeScriptValue(scriptValue);
            assertEquals(2, output.size());
            assertEquals("19", output.get(0));
            assertEquals("ger)", output.get(1));
        }
    }
}

package com.wait.simplescript.server.script;

import java.util.*;

public class ScriptUtils {
    public static final String DO_THIS = "DoThisThing(string)";
    public static final String DO_THAT = "DoThatThing(integer)";
    public static final String DO_THE = "DoTheOtherThing(float)";

    public static final String MISSING_OPERATIONS_MSG = "Operations are " +
            "missing";
    public static final String INVALID_OPERATIONS_MSG = "Invalid operations " +
            "were provided";

    public static final Set<String> validOperations = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(DO_THAT,
                    DO_THE, DO_THIS)));

    private ScriptUtils() {
        throw new AssertionError("com.wait.simplescript.server.script" +
                ".Script instances can not be created");
    }

    public static String generateScript(List<String> operations) {
        if (operations.isEmpty()) {
            throw new InvalidOperationException(MISSING_OPERATIONS_MSG);
        }
        if (operations.size() == 1) {
            if (ScriptUtils.validOperations.contains(operations.get(0))) {
                return operations.get(0);
            }
            throw new InvalidOperationException(INVALID_OPERATIONS_MSG);
        }

        List<String> operationsWithoutLastItem = operations.subList(0,
                operations.size() - 1);
        String script = operationsWithoutLastItem.stream().reduce("",
                (partialScript, operation) -> {
                    if (ScriptUtils.validOperations.contains(operation)) {
                        return String.format("%s%s\n",
                                partialScript, operation);
                    }
                    throw new InvalidOperationException(INVALID_OPERATIONS_MSG);
                });
        if (ScriptUtils.validOperations.contains(operations.get(operations.size() - 1))) {
            script = String.format("%s%s", script,
                    operations.get(operations.size() - 1));
            return script;
        }
        throw new InvalidOperationException(INVALID_OPERATIONS_MSG);
    }
}

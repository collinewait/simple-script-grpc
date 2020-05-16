package com.wait.simplescript.server.script;

import com.wait.simplescript.server.user.Users;

import java.util.ArrayList;

public class Scripts {
    public static final String SCRIPT_ID = "23456scriptId356";
    public static final String MULTIPLE_SCRIPT_ID = "46783whoJJJ78";
    public static final Script SINGLE_OPERATION_SCRIPT =
            createSingleOpsScript();
    public static final String MULTIPLE_OPERATIONS_SCRIPT_VALUE =
            String.format("%s\n%s",
            ScriptUtils.DO_THIS,
            ScriptUtils.DO_THIS);
    public static final Script MULTIPLE_OPERATIONS_SCRIPT =
            createMultipleOpsScript();

    private Scripts() {
    }

    private static Script createSingleOpsScript() {
        Script script = Script.createScript(Users.user(),
                ScriptUtils.DO_THIS,
                new ArrayList<>());
        script.setId(SCRIPT_ID);
        return script;
    }

    private static Script createMultipleOpsScript() {
        Script script = Script.createScript(Users.user(),
                MULTIPLE_OPERATIONS_SCRIPT_VALUE,
                new ArrayList<>());
        script.setId(MULTIPLE_SCRIPT_ID);
        return script;
    }
}

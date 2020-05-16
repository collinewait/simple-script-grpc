package com.wait.simplescript.server.script;

public class ScriptNotFoundException extends RuntimeException {
    public ScriptNotFoundException(String id) {
        super(String.format("Could not find script with id %s", id));
    }
}

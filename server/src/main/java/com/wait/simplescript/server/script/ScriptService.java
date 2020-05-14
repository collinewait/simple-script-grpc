package com.wait.simplescript.server.script;

import com.wait.simplescript.server.user.User;

public interface ScriptService {
    Script createScript(User user, String scriptValue);
}

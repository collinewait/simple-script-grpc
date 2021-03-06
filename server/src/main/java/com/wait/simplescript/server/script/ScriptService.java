package com.wait.simplescript.server.script;

import com.wait.simplescript.server.user.User;

import java.util.List;
import java.util.Optional;

public interface ScriptService {
    Script createScript(User user, String scriptValue);
    Optional<Script> findById(String id);
    List<Script> findAll();
    Script update(Script script);
    String deleteById(String id);
    List<Script> findByUser(String id);
}

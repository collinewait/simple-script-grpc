package com.wait.simplescript.server.script;

import com.wait.simplescript.server.user.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ScriptServiceImpl implements ScriptService {
    private final ScriptRepository scriptRepository;

    public ScriptServiceImpl(ScriptRepository scriptRepository) {
        this.scriptRepository = scriptRepository;
    }

    @Override
    public Script createScript(User user, String scriptValue) {
        Script script = Script.createScript(user, scriptValue,
                new ArrayList<>());

        return scriptRepository.save(script);
    }
}

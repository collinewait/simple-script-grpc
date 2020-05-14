package com.wait.simplescript.server.script;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScriptRepository extends MongoRepository<Script, String> {
}

package com.wait.simplescript.server.script;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ScriptRepository extends MongoRepository<Script, String> {
    List<Script> findByUser(String id);
}

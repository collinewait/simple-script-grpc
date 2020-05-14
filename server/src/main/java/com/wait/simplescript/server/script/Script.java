package com.wait.simplescript.server.script;

import com.wait.simplescript.server.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "scripts")
@Getter
@EqualsAndHashCode
@ToString
public class Script {
    @Id
    private String id;
    @DBRef
    private User user;
    private String scriptValue;
    private List<String> executedOutput;

    protected Script() {
    }

    private Script(User user, String scriptValue, List<String> executedOutput) {
        this.user = user;
        this.scriptValue = scriptValue;
        this.executedOutput = executedOutput;
    }

    public static Script createScript(User user, String scriptValue,
                                      List<String> executedOutput) {
        return new Script(user, scriptValue, executedOutput);
    }

    public void setId(String id) {
        this.id = id;
    }
}

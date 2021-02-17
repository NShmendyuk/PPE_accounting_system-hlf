package ru.inside.commands.executor;

import org.springframework.stereotype.Service;
import ru.inside.commands.executor.runner.ScriptRunner;

@Service
public class ScriptExecutor {
    private final String HLF_SCRIPTS_PATH = "/hlf/fabric-samples/4host-swarm/scripts/";

    public String executeScript(String partName) {
        return ScriptRunner.runScript(HLF_SCRIPTS_PATH + partName);
    }
}

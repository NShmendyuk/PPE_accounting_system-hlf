package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.inside.commands.executor.ScriptExecutor;

@RestController("/scripts")
@RequiredArgsConstructor
public class ScriptController {
    private final ScriptExecutor scriptExecutor;

    @GetMapping("/{scriptName}")
    public String runHlfScript(@PathVariable String scriptName) {
        return scriptExecutor.executeScript(scriptName);
    }
}

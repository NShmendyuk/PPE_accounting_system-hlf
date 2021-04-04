package ru.inside.commands.outsider.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.inside.commands.outsider.executor.ScriptExecutor;

@RestController
@RequestMapping("/scripts")
@RequiredArgsConstructor
@Slf4j
public class ScriptController {
    private final ScriptExecutor scriptExecutor;

    @GetMapping("/{scriptName}")
    public String runHlfScript(@PathVariable String scriptName) {
        log.info("Executing script: " + scriptName);
        return scriptExecutor.executeScript(scriptName);
    }
}

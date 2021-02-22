package ru.inside.commands.executor.runner;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public final class ScriptRunner {

    public String runScript(String scriptPath){
        BufferedReader read;
        StringBuilder builder = new StringBuilder();
        try {
            Process proc = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", scriptPath });
            read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                log.error("Error. Script executing were interrupted.");
            }
            while (read.ready()) {
                builder.append(read.readLine());
            }
        } catch (IOException e) {
            log.error("Cannot run script");
        }
        if (builder.length() != 0) {
            log.info("returning next output information:\n" + builder);
            return builder.toString();
        } else
            return "empty response";
    }
}

package ru.inside.commands.executor.runner;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@UtilityClass
public final class ScriptRunner {

    public String runScript(String scriptPath){
        BufferedReader read = null;
        try {
            Process proc = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", scriptPath }); //Whatever you want to execute
            read = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            while (read.ready()) {
                System.out.println(read.readLine());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        if (read != null) {
            return read.lines().collect(Collectors.joining());
        } else
            return "empty response";
    }
}

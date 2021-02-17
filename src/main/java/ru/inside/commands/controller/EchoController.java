package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/check")
@RequiredArgsConstructor
public class EchoController {

    @GetMapping("/check")
    public String check() {
        return "API is working.\n";
    }
}

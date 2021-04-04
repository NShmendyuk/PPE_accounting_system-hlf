package ru.inside.commands.outsider.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/check")
@RequiredArgsConstructor
@Slf4j
public class EchoController {

    @GetMapping("/check")
    public String check(HttpServletRequest requestContext) {
        log.info("API is checked from {}:{}", requestContext.getRemoteAddr(), requestContext.getRemotePort());
        return "API is working.\n";
    }
}

package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smart_contract/transfer")
@RequiredArgsConstructor
@Slf4j
public class PPEWaitPageController {
    @PostMapping("/apply")
    public void applyPPEFromSmartContract(@RequestParam String ppeName, @RequestParam String ownerName,
                                                  @RequestParam String subsidiaryName, @RequestParam Float price) {
        log.info("Applied PPE transfer: [{}; owner: {}; from: {}; price:{}].", ppeName, ownerName, subsidiaryName, price);
    }

    @PostMapping("/apply/all")
    public void applyAllPPEFromSmartContract() {
        log.info("Applying all PPE transfers.");
    }
}

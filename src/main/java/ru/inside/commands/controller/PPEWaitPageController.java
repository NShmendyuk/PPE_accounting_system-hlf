package ru.inside.commands.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.service.PPEService;
import ru.inside.commands.service.controller.PPEControllerService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/smart_contract/transfer")
@RequiredArgsConstructor
@Slf4j
public class PPEWaitPageController {
    private final PPEControllerService ppeControllerService;

    @PostMapping("/apply")
    public void applyPPEFromSmartContract(@RequestParam String inventoryNumber, @RequestParam String ppeName,
                                          @RequestParam String ownerName, @RequestParam String subsidiaryName,
                                          @RequestParam Float price) {
        log.info("Applying PPE {} transfer: [{}; owner: {}; from: {}; price:{}].", inventoryNumber, ppeName, ownerName, subsidiaryName, price);
        ppeControllerService.applyPPEFromChainCode(inventoryNumber);
    }

    @PostMapping("/apply/all")
    public void applyAllPPEFromSmartContract() {
        log.info("Applying all PPE transfers.");
        List<PPEForm> waitAllPPE = ppeControllerService.getAllInWaitList();
        ppeControllerService.applyAllPPEFromChainCode(waitAllPPE);
    }
}

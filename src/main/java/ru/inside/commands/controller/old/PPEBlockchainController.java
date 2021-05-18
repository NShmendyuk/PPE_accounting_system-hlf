package ru.inside.commands.controller.old;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.inside.commands.hyperledger.ChainCodeControllerService;
import ru.inside.commands.hyperledger.entity.PPEContract;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/devtools/chaincode/ppe")
@Slf4j
public class PPEBlockchainController {
    private final ChainCodeControllerService chainCodeControllerService;

    @GetMapping("/init/test")
    public String getPPE() {
        chainCodeControllerService.initTestLedger();
        return "test ledger";
    }

    @GetMapping("/all")
    public List<PPEContract> getAllPPE() {
        return chainCodeControllerService.getAllPPE();
    }

    @GetMapping("/{inventoryNumber}")
    public PPEContract getPPE(@PathVariable String inventoryNumber){
        return chainCodeControllerService.getPPEByInventoryNumber(inventoryNumber);
    }
}

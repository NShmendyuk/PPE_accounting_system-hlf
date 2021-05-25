package ru.inside.commands.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.service.controller.PPEControllerService;

import java.nio.charset.StandardCharsets;
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
        byte[] contents = ppeControllerService.applyPPEFromChainCode(inventoryNumber);
//        HttpHeaders headers = new HttpHeaders();
//
//        try {
//            headers.setContentType(MediaType.APPLICATION_PDF);
//
//            String filename = "apply transfer ppe (" + inventoryNumber + ") with employee ("
//                    + ownerName +"); price (" + price.toString() + ") generated" + ".pdf";
//            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
//                    .filename(filename, StandardCharsets.UTF_8)
//                    .build();
//            headers.setContentDisposition(contentDisposition);
//            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
//        } catch (Exception ex) {
//            log.error("Cannot set headers for file");
//        }
//        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }

    @PostMapping("/apply/all")
    public ResponseEntity<byte[]> applyAllPPEFromSmartContract() {
        log.info("Applying all PPE transfers.");
        byte[] contents = ppeControllerService.applyAllPPEFromChainCode();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        String filename = "apply transfer all PPE generated" + ".pdf";
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(filename, StandardCharsets.UTF_8)
                .build();
        headers.setContentDisposition(contentDisposition);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }
}

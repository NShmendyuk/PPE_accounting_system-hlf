package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.service.controller.PPEControllerService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ppe")
@RequiredArgsConstructor
@Slf4j
public class PPEPageController {
    public final PPEControllerService ppeControllerService;

    @GetMapping("")
    public ModelAndView getPPEPage() {
        ModelAndView modelAndView = new ModelAndView("ppePage");

        List<PPEForm> ppeFormList = ppeControllerService.getAllPPEForms();

        modelAndView.addObject("ppesInStock", ppeFormList);
        modelAndView.addObject("ppeForm", new PPEForm());
        return modelAndView;
    }

    @GetMapping("/history")
    public ModelAndView getPPEHistoryPage(@RequestParam String inventoryNumber) {
        log.info("GET request with ppe inventoryNumber: {}; show history page", inventoryNumber);
        ModelAndView modelAndView = new ModelAndView("historyPage");

        List<PPEForm> ppeHistoryForms = ppeControllerService.getPPEHistory(inventoryNumber);

        if (ppeHistoryForms.size() == 0 ) {
            modelAndView.addObject("ppeForm", new PPEForm());
            modelAndView.addObject("ppeHistoryPage", "0/0");
            return modelAndView;
        }

        String ppeHistoryPage = 1 + "/" + ppeHistoryForms.size();

        modelAndView.addObject("ppeForm", ppeHistoryForms.get(0));
        modelAndView.addObject("ppeHistoryPage", ppeHistoryPage);
        return modelAndView;
    }

    @GetMapping("/history/previous")
    public ModelAndView getPPEHistoryPagePrevious(@RequestParam String inventoryNumber, @RequestParam String ppeHistoryPage) {
        log.info("GET request with ppe inventoryNumber: {}; show history page", inventoryNumber);
        ModelAndView modelAndView = new ModelAndView("historyPage");

        int ppeHistoryPageRequest = Integer.parseInt(ppeHistoryPage.substring(0, ppeHistoryPage.indexOf("/"))
                .replace("/", "")) - 1;
        ppeHistoryPageRequest = ppeHistoryPageRequest - 1;

        if (ppeHistoryPageRequest < 0) {
            ppeHistoryPageRequest = 0;
        }
        List<PPEForm> ppeHistoryForms = ppeControllerService.getPPEHistory(inventoryNumber);

        if (ppeHistoryForms.size() == 0 ) {
            modelAndView.addObject("ppeForm", new PPEForm());
            modelAndView.addObject("ppeHistoryPage", "0/0");
            return modelAndView;
        }

        modelAndView.addObject("ppeForm", ppeHistoryForms.get(ppeHistoryPageRequest));
        modelAndView.addObject("ppeHistoryPage", ppeHistoryPageRequest + 1 + "/" + ppeHistoryForms.size());
        return modelAndView;
    }

    @GetMapping("/history/next")
    public ModelAndView getPPEHistoryPageNext(@RequestParam String inventoryNumber, @RequestParam String ppeHistoryPage) {
        log.info("GET request with ppe inventoryNumber: {}; show history page", inventoryNumber);
        ModelAndView modelAndView = new ModelAndView("historyPage");

        int ppeHistoryPageRequest = Integer.parseInt(ppeHistoryPage.substring(0, ppeHistoryPage.indexOf("/"))
                .replace("/", "")) - 1;


        ppeHistoryPageRequest = ppeHistoryPageRequest + 1;

        List<PPEForm> ppeHistoryForms = ppeControllerService.getPPEHistory(inventoryNumber);

        if (ppeHistoryForms.size() == 0 ) {
            modelAndView.addObject("ppeForm", new PPEForm());
            modelAndView.addObject("ppeHistoryPage", "0/0");
            return modelAndView;
        }

        if (ppeHistoryPageRequest > ppeHistoryForms.size() - 1) {
            ppeHistoryPageRequest = ppeHistoryForms.size() - 1;
        }

        modelAndView.addObject("ppeForm", ppeHistoryForms.get(ppeHistoryPageRequest));
        modelAndView.addObject("ppeHistoryPage", ppeHistoryPageRequest + 1 + "/" + ppeHistoryForms.size());
        return modelAndView;
    }

    @GetMapping("/")
    public ModelAndView getPPEByInventoryNumber(@RequestParam String inventoryNumber) {
        log.info("GET request with ppe inventoryNumber: {}", inventoryNumber);
        ModelAndView modelAndView = new ModelAndView("ppePage");
        PPEForm ppeForm = ppeControllerService.getPPEForm(inventoryNumber);
        log.info("response: {}", ppeForm.toString());

        modelAndView.addObject("ppeForm", ppeForm);

        return modelAndView;
    }

    @PostMapping("/apply_new_ppe")
    public ModelAndView addPPEWithOwner(@RequestParam String ppeName, @RequestParam Float price,
                               @RequestParam String inventoryNumber, @RequestParam String ownerPersonnelNumber,
                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                               @RequestParam Long lifeTimeDays) {
        log.info("name: {}, price: {}, {}, {}, date: {}, {}",
                ppeName, price, inventoryNumber, ownerPersonnelNumber, date, lifeTimeDays);
        ppeControllerService.addPPEForm(ppeName, price, inventoryNumber, ownerPersonnelNumber, date, lifeTimeDays);

        return getPPEPage();
    }

    @PatchMapping("/decommiss")
    public void decommissioningPPE(@RequestParam String personnelNumber, @RequestParam String inventoryNumber) {
        log.info("Patch request for decommissioning ppe {} from employee {}", inventoryNumber, personnelNumber);
        ppeControllerService.decommissioning(personnelNumber, inventoryNumber);
    }
}

package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.service.controller.PPEControllerService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/ppe")
@RequiredArgsConstructor
@Slf4j
public class PPEPageController {
    public final PPEControllerService ppeControllerService;

    @GetMapping("")
    public ModelAndView getPPEPage() {
        ModelAndView modelAndView = new ModelAndView("ppePage");
        modelAndView.addObject("ppeForm", new PPEForm());
        return modelAndView;
    }

    @GetMapping("/")
    public ModelAndView getPPEById(@RequestParam Long id) {
        log.info("GET request with ppe id: {}", id);
        ModelAndView modelAndView = new ModelAndView("ppePage");
        PPEForm ppeForm = ppeControllerService.getPPEForm(id);
        log.info("response: {}", ppeForm.toString());

        modelAndView.addObject("ppeForm", ppeForm);

        return modelAndView;
    }

    @PostMapping("/apply_new_ppe")
    public ModelAndView addPPE(@RequestParam String ppeName, @RequestParam Float price,
                               @RequestParam String inventoryNumber, @RequestParam String ownerPersonnelNumber,
                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                               @RequestParam Long lifeTimeDays) {
        log.info("name: {}, price: {}, {}, {}, date: {}, {}",
                ppeName, price, inventoryNumber, ownerPersonnelNumber, date, lifeTimeDays);
        ppeControllerService.addPPEForm(ppeName, price, inventoryNumber, ownerPersonnelNumber, date, lifeTimeDays);
        return getPPEPage();
    }
}

package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.service.PPEService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainPageController {
    private final PPEService ppeService;

    @GetMapping(value = {"/", "/index", "/mainpage"})
    public ModelAndView getMainPage() {
        ModelAndView modelAndView = new ModelAndView("index");
        Long waitAll = 35L;
        int countPPEAtSubsidiary = ppeService.getTotalPPE();
        modelAndView.addObject("waitAll", waitAll);
        modelAndView.addObject("countPPEAtSubsidiary", countPPEAtSubsidiary);
        return modelAndView;
    }

    @GetMapping(value = "waiting_apply")
    public ModelAndView getWaitingAccessPage() {
        ModelAndView modelAndView = new ModelAndView("waitApplyPage");
        try {
            List<PPEForm> waitAllPPE = ppeService.getAllInWaitList(); //TODO: stubbed

            Float allCost = 0F;
            for (PPEForm ppe : waitAllPPE) {
                allCost += ppe.getPrice();
            }

            modelAndView.addObject("countWaitAll", waitAllPPE.size());
            modelAndView.addObject("allCost", allCost);
            modelAndView.addObject("waitAllPPE", waitAllPPE);

            log.info("showing {} PPE from transaction list which wait for apply", waitAllPPE.size());
        } catch (Exception ex) {
            log.info("none");
            return modelAndView;
        }
        return modelAndView;
    }
}

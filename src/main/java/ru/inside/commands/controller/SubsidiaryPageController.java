package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.SubsidiaryDto;
import ru.inside.commands.entity.enums.SubsidiaryStatus;
import ru.inside.commands.entity.forms.SubsidiaryForm;
import ru.inside.commands.hyperledger.PeerDiscoveryService;
import ru.inside.commands.service.SubsidiaryService;
import ru.inside.commands.service.controller.SubsidiaryControllerService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/subsidiary")
@RequiredArgsConstructor
@Slf4j
public class SubsidiaryPageController {
    private final SubsidiaryControllerService subsidiaryControllerService;

    @GetMapping
    public ModelAndView getSubsidiaryPage() {
        ModelAndView modelAndView = new ModelAndView("subsidiaryPage");
        List<SubsidiaryForm> subsidiaryForms = subsidiaryControllerService.getAllAnotherSubsidiary();

        modelAndView.addObject("subsidiaryConnected", subsidiaryForms);
        modelAndView.addObject("subsidiaryFormApply", new SubsidiaryForm());
        return modelAndView;
    }

    @PostMapping("/add")
    public ModelAndView addSubsidiary(@RequestParam String name, @RequestParam String peerName) {
        log.info("Post request to add: {}; peer name: {}", name, peerName);
        subsidiaryControllerService.addSubsidiaryInfo(name, peerName);
        return getSubsidiaryPage();
    }
}

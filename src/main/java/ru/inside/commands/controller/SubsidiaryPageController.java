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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/subsidiary")
@RequiredArgsConstructor
@Slf4j
public class SubsidiaryPageController {
    private final PeerDiscoveryService peerDiscoveryService;
    private final SubsidiaryService subsidiaryService;

    @GetMapping
    public ModelAndView getSubsidiaryPage() {
        ModelAndView modelAndView = new ModelAndView("subsidiaryPage");
        List<SubsidiaryForm> subsidiaryForms = new ArrayList<>();
        Collection<String> mspIds = new ArrayList<>();
        try {
            mspIds = peerDiscoveryService.getMSPIDsInfo();
        } catch (Exception ex) {
            log.warn("Process to find peers in hlf network were denied!");
        }


        List<Subsidiary> subsidiaryList = subsidiaryService.getAll();

        for (Subsidiary subsidiary : subsidiaryList) {
            SubsidiaryForm subsidiaryForm = new SubsidiaryForm();
            subsidiaryForm.setName(subsidiary.getName());
            subsidiaryForm.setPeerName(subsidiary.getPeerName());
            subsidiaryForm.setStatus(SubsidiaryStatus.UNACCESSED); //TODO: check by hyperledger service
            mspIds.forEach(mspId -> {
                if (mspId.equals(subsidiary.getPeerName())) {
                    subsidiaryForm.setStatus(SubsidiaryStatus.ACCESSED);
                }
            });
            subsidiaryForms.add(subsidiaryForm);
        }

        modelAndView.addObject("subsidiaryConnected", subsidiaryForms);
        modelAndView.addObject("subsidiaryFormApply", new SubsidiaryForm());
        return modelAndView;
    }

    @PostMapping("/add")
    public ModelAndView addSubsidiary(@RequestParam String name, @RequestParam String peerName) {
        log.info("Post request to add: {}; peer name: {}", name, peerName);
        if (! (name.length() == 0 || peerName.length() == 0)) {
            log.info("New Subsidiary. name: {}, peerName: {}", name, peerName);
            SubsidiaryDto subsidiaryDto = new SubsidiaryDto();
            subsidiaryDto.setName(name);
            subsidiaryDto.setPeerName(peerName);
            subsidiaryService.add(subsidiaryDto);
        }
        return getSubsidiaryPage();
    }
}

package ru.inside.commands.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.enums.SubsidiaryStatus;
import ru.inside.commands.entity.forms.SubsidiaryForm;
import ru.inside.commands.hyperledger.PeerDiscoveryService;
import ru.inside.commands.service.SubsidiaryService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubsidiaryControllerServiceImpl implements SubsidiaryControllerService {
    private final PeerDiscoveryService peerDiscoveryService;
    private final SubsidiaryService subsidiaryService;

    public List<SubsidiaryForm> getAllAnotherSubsidiary() {
        List<SubsidiaryForm> subsidiaryForms = new ArrayList<>();
        Collection<String> mspIds = new ArrayList<>();
        try {
            mspIds = peerDiscoveryService.getMSPIDsInfo();
        } catch (Exception ex) {
            log.warn("Process to find peers in hlf network were denied!");
        }

        List<Subsidiary> subsidiaryList = subsidiaryService.getAll();

        subsidiaryList = subsidiaryList.stream().filter(subsidiary -> {
            try {
                return !subsidiary.getName().equals(subsidiaryService.getSelfSubsidiary().getName());
            } catch (NoEntityException e) {
                log.warn("No self subsidiary description");
            }
            return false;
        }).collect(Collectors.toList());

        for (Subsidiary subsidiary : subsidiaryList) {
            SubsidiaryForm subsidiaryForm = new SubsidiaryForm();
            subsidiaryForm.setName(subsidiary.getName());
            subsidiaryForm.setPeerName(subsidiary.getPeerName());
            subsidiaryForm.setStatus(mspIds.contains(subsidiary.getPeerName()) ? SubsidiaryStatus.ACCESSED : SubsidiaryStatus.UNACCESSED);
            subsidiaryForms.add(subsidiaryForm);
        }
        return subsidiaryForms;
    }

    public void addSubsidiaryInfo(String name, String peerName) {
        if (! (name.length() == 0 || peerName.length() == 0)) {
            log.info("New Subsidiary. name: {}, peerName: {}", name, peerName);
            Subsidiary subsidiary = new Subsidiary();
            subsidiary.setName(name);
            subsidiary.setPeerName(peerName);
            subsidiaryService.add(subsidiary);
        }
    }
}

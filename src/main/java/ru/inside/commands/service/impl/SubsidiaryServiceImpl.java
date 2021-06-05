package ru.inside.commands.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.repository.SubsidiaryRepository;
import ru.inside.commands.service.SubsidiaryService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubsidiaryServiceImpl implements SubsidiaryService {
    private final SubsidiaryRepository subsidiaryRepository;

    private String selfSubsidiaryName = "ГПН-Д1";
    private String selfPeerMspId = "Org1MSP";

    @PostConstruct
    private void initSelfOrgDefinition() {
        log.info("self subsidiary: name={}, peerMspId={}", selfSubsidiaryName, selfPeerMspId);
        if (subsidiaryRepository.findByName(selfSubsidiaryName).isEmpty()) {
            Subsidiary subsidiary = new Subsidiary();
            subsidiary.setPeerName(selfPeerMspId);
            subsidiary.setName(selfSubsidiaryName);
            subsidiaryRepository.save(subsidiary);
        }
    }

    public Subsidiary getSelfSubsidiary() throws NoEntityException {
        return subsidiaryRepository.findByName(selfSubsidiaryName).orElseThrow(() ->
                NoEntityException.createWithParam(Subsidiary.class.getSimpleName(), selfSubsidiaryName));
    }

    public Subsidiary getByName(String subsidiaryName) throws NoEntityException {
        return subsidiaryRepository.findByName(subsidiaryName).orElseThrow(() ->
                NoEntityException.createWithParam(Subsidiary.class.getSimpleName().toLowerCase(), subsidiaryName));
    }

    public Subsidiary add(Subsidiary subsidiary) {
        return subsidiaryRepository.save(subsidiary);
    }

    public List<Subsidiary> getAll() {
        return subsidiaryRepository.findAll();
    }
}

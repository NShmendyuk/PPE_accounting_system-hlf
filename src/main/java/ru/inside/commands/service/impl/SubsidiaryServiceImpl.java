package ru.inside.commands.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.EmployeeDto;
import ru.inside.commands.entity.dto.SubsidiaryDto;
import ru.inside.commands.repository.EmployeeRepository;
import ru.inside.commands.repository.SubsidiaryRepository;
import ru.inside.commands.service.SubsidiaryService;
import ru.inside.commands.service.helper.DtoConverter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubsidiaryServiceImpl implements SubsidiaryService {
    private final SubsidiaryRepository subsidiaryRepository;

    private String selfSubsidiaryName = "ГПН-Д2";
    private String selfPeerMspId = "Org2MSP";

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

    public SubsidiaryDto get(Long id) throws NoEntityException {
        Subsidiary subsidiary = subsidiaryRepository.findById(id).orElseThrow(() ->
            NoEntityException.createWithId(Subsidiary.class.getSimpleName().toLowerCase(), id));
        return DtoConverter.convertSubsidiaryToDto(subsidiary);
    }

    public Subsidiary getSelfSubsidiary() throws NoEntityException {
        return subsidiaryRepository.findByName(selfSubsidiaryName).orElseThrow(() ->
                NoEntityException.createWithParam(Subsidiary.class.getSimpleName(), selfSubsidiaryName));
    }

    public Subsidiary getByName(String subsidiaryName) throws NoEntityException {
        Subsidiary subsidiary = subsidiaryRepository.findByName(subsidiaryName).orElseThrow(() ->
                NoEntityException.createWithParam(Subsidiary.class.getSimpleName().toLowerCase(), subsidiaryName));
        return subsidiary;
    }

    public SubsidiaryDto add(SubsidiaryDto subsidiaryDto){
        for (Subsidiary subsidiary : subsidiaryRepository.findAll()) {
            if (subsidiary.getName().equals(subsidiaryDto.getName())) {
                subsidiary.setPeerName(subsidiaryDto.getPeerName());
                return DtoConverter.convertSubsidiaryToDto(subsidiaryRepository.save(subsidiary));
            }
            if (subsidiary.getPeerName().equals(subsidiaryDto.getPeerName())) {
                subsidiary.setName(subsidiaryDto.getName());
                return DtoConverter.convertSubsidiaryToDto(subsidiaryRepository.save(subsidiary));
            }
        }
        return DtoConverter.convertSubsidiaryToDto(subsidiaryRepository.save(DtoConverter.convertDtoToSubsidiary(subsidiaryDto)));
    }

    public Subsidiary add(Subsidiary subsidiary) {
        return subsidiaryRepository.save(subsidiary);
    }

    public List<Subsidiary> getAll() {
        return subsidiaryRepository.findAll();
    }
}

package ru.inside.commands.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.PPEDto;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.hyperledger.ChainCodeControllerService;
import ru.inside.commands.hyperledger.entity.PPEContract;
import ru.inside.commands.repository.EmployeeRepository;
import ru.inside.commands.repository.PPERepository;
import ru.inside.commands.service.EmployeeService;
import ru.inside.commands.service.PPEService;
import ru.inside.commands.service.SubsidiaryService;
import ru.inside.commands.service.helper.DtoConverter;
import ru.inside.commands.service.helper.PPEConverter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PPEServiceImpl implements PPEService {
    private final ChainCodeControllerService chainCodeControllerService;
    private final SubsidiaryService subsidiaryService;
    private final EmployeeService employeeService;
    private final PPERepository ppeRepository;

    public PPE getPPEByInventoryNumber(String inventoryNumber) throws NoEntityException {
        return ppeRepository.findByInventoryNumber(inventoryNumber).orElseThrow(() ->
                NoEntityException.createWithParam(PPE.class.getSimpleName().toLowerCase(), inventoryNumber));
    }

    public List<PPE> getAllPPE() {
        return ppeRepository.findAll();
    }

    public boolean isPPEExist(String inventoryNumber) {
        return ppeRepository.existsByInventoryNumber(inventoryNumber);
    }

    public long getTotalPPE() {
        return ppeRepository.findAll().stream().filter(ppe -> !ppe.getPpeStatus().equals(PPEStatus.TRANSFER)).count();
    }

    public PPE addPPE(PPE ppe) {
        return ppeRepository.save(ppe);
    }

    /**
     * function for decommissing PPE, which have used up their service life
     */
    public void updateAllStatus() {
        List<PPE> ppeList = ppeRepository.findAll();
        ppeList.parallelStream().filter(ppe -> ppe.getEmployee() != null)
                .forEach(ppe -> {
                    LocalDateTime startUseDate = ppe.getStartUseDate();
                    if (startUseDate == null) {
                        startUseDate = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
                        ppe.setStartUseDate(startUseDate);
                    }
                    LocalDateTime nowTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
                    Duration lifeTimeSpent = Duration.between(startUseDate, nowTime);
                    Duration lifeTimeDuration = ppe.getLifeTime();

                    log.info("ppe {}: days for use: {}", ppe.getInventoryNumber(), lifeTimeDuration.minus(lifeTimeSpent).toDays());

                    if (ppe.getPpeStatus() != PPEStatus.DECOMMISSIONED
                            && ppe.getPpeStatus() != PPEStatus.SPOILED
                            && lifeTimeDuration.minus(lifeTimeSpent).toDays() < 0) {
                        ppe.setPpeStatus(PPEStatus.SPOILED);
                        log.info("ppe {} were spoiled by time! Replacement required!", ppe.getInventoryNumber());
                        ppeRepository.save(ppe);
                    }
            });
    }


    public void dismissPPE(String inventoryNumber) throws NoEntityException {
        PPE ppe = ppeRepository.findByInventoryNumber(inventoryNumber).orElseThrow(() ->
                NoEntityException.createWithParam(PPE.class.getSimpleName().toLowerCase(), inventoryNumber));
        ppe.setPpeStatus(PPEStatus.DECOMMISSIONED);
        ppeRepository.save(ppe);
        chainCodeControllerService.deletePPE(inventoryNumber);
    }

    public void transferPPE(PPE ppe, Subsidiary subsidiary) {
        chainCodeControllerService.transferPPEToSubsidiary(ppe, subsidiary);
        ppe.setPpeStatus(PPEStatus.TRANSFER);
        ppe.setEmployee(null);
        ppeRepository.save(ppe);
    }

    public List<PPE> getAllWaitFromChainCode() {
        List<PPEContract> ppeContracts = new ArrayList<>();
        try{
            ppeContracts = chainCodeControllerService.getAllPPE();
        } catch (NullPointerException ex) {
            log.warn("ChainCode getAllPPE returned null.");
        } catch (Exception ex) {
            log.error("Cannot getAllPPE from chaincode controller!!!");
        }
        List<PPE> ppeWaitList = new ArrayList<>();

        Subsidiary selfSubsidiary = null;
        try {
            selfSubsidiary = subsidiaryService.getSelfSubsidiary();
        } catch (NoEntityException e) {
            log.error("Cannot find self subsidiary definition");
        }
        String selfSubsidiaryName = "";
        try {
            selfSubsidiaryName = selfSubsidiary.getName();
        } catch (Exception ex) {
            log.error("No self subsidiary definition for name");
        }

        String finalSelfSubsidiaryName = selfSubsidiaryName;
        ppeContracts.forEach(ppeContract -> {
            if (ppeContract.getOwnerID() == null || ppeContract.getOwnerID().equals("") ||
                    ppeContract.getSubsidiary() == null || ppeContract.getSubsidiary().equals("") ||
                    ppeContract.getInventoryNumber() == null || ppeContract.getInventoryNumber().equals("")) {
                log.warn("PPEContract in chaincode have empty fields!!!");
            } else {
                try {
                    //it's for self org and ppe is not exist at subsidiary or exist with transfered status
                    if (ppeContract.getSubsidiary().equals(finalSelfSubsidiaryName) &&
                            (!isPPEExist(ppeContract.getInventoryNumber()) || (
                                    isPPEExist(ppeContract.getInventoryNumber()) &&
                                            getPPEByInventoryNumber(ppeContract.getInventoryNumber())
                                                    .getPpeStatus().equals(PPEStatus.TRANSFER)))) {
                        PPE ppe = PPEConverter.ppeContractToPPE(ppeContract);
                        Employee employee = new Employee();
                        employee.setPersonnelNumber(ppeContract.getOwnerID());
                        employee.setEmployeeName(ppeContract.getOwnerName());
                        try {
                            employee.setSubsidiary(subsidiaryService.getByName(ppeContract.getSubsidiary()));
                        } catch (Exception ex) {
                            log.error("Cannot set subsidiary while convert ppeContract to PPE from waitList");
                        }
                        ppe.setEmployee(employee);
                        ppeWaitList.add(ppe);
                    }
                } catch (NoEntityException e) {
                    log.error("Cannot get self subsidiary definition while get all from waitList");
                }
            }
        });
        return ppeWaitList;
    }
}

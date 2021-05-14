package ru.inside.commands.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.dto.PPEDto;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.hyperledger.ChainCodeControllerService;
import ru.inside.commands.hyperledger.entity.PPEContract;
import ru.inside.commands.service.EmployeeService;
import ru.inside.commands.service.PPEService;
import ru.inside.commands.service.helper.FormConverter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PPEControllerServiceImpl implements PPEControllerService {
    private final PPEService ppeService;
    private final EmployeeService employeeService;
    private final ChainCodeControllerService chainCodeControllerService;

    public PPEForm getPPEForm(String inventoryNumber) {
        PPEForm ppeForm = new PPEForm();
        try {
            ppeForm = FormConverter.getPPEAsForm(ppeService.getPPEByInventoryNumber(inventoryNumber));
        } catch (NoEntityException e) {
            log.warn("{}", e.getMessage());
        }
        log.info("Found: {}", ppeForm.toString());
        return ppeForm;
    }

    public void addPPEForm(String name, Float price, String inventoryNumber,
                           String ownerPersonnelNumber,  LocalDate date, Long lifeTimeDays) {
        log.info("add new ppe: {}, owner: {}", name, ownerPersonnelNumber);
        PPEDto ppeDto = new PPEDto();
        ppeDto.setName(name);
        ppeDto.setPrice(price);
        ppeDto.setInventoryNumber(inventoryNumber);
        ppeDto.setStartUseDate(LocalDateTime.of(date, LocalTime.MIDNIGHT));
        ppeDto.setLifeTime(Duration.ofDays(lifeTimeDays));

        ppeDto.setPpeStatus(PPEStatus.APPLIED);

        try {
            ppeDto.setOwnerId(employeeService.getEmployeeByPersonnelNumber(ownerPersonnelNumber).getId());
        } catch (NoEntityException e) {
            log.warn("No employee with personnel number: {}", ownerPersonnelNumber);
            ppeDto.setOwnerId(null);
        }
        try {
            ppeService.addPPE(ppeDto);
        } catch (NoEntityException ignored) {
        }
    }

    public List<PPEForm> getPPEHistory(String inventoryNumber) {
        List<PPEContract> ppeHistoryList = chainCodeControllerService.getPPEHistoryByInventoryNumber(inventoryNumber);
        List<PPEForm> ppeHistoryForms = new ArrayList<>();
        ppeHistoryList.forEach(ppeContract -> {
            PPEForm ppeForm = new PPEForm();
            ppeForm.setPpeName(ppeContract.getName());
            ppeForm.setPrice(ppeContract.getPrice());
            ppeForm.setInventoryNumber(ppeContract.getInventoryNumber());
            ppeForm.setLifeTime(Duration.ofDays(ppeContract.getLifeTime()));
            ppeForm.setStartUseDate(LocalDateTime.parse(ppeContract.getStartUseDate()));

            ppeForm.setPpeStatus(PPEStatus.COMMISSIONED.toString()); //TODO: add status to chaincode

            ppeForm.setOwnerPersonnelNumber(ppeContract.getOwnerID());
            ppeForm.setOwnerName(ppeContract.getOwnerName());
            ppeForm.setSubsidiaryName(ppeContract.getSubsidiary());
            ppeHistoryForms.add(ppeForm);
        });
        return ppeHistoryForms;
    }
}

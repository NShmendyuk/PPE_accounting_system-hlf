package ru.inside.commands.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.dto.PPEDto;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.service.EmployeeService;
import ru.inside.commands.service.PPEService;
import ru.inside.commands.service.helper.FormConverter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PPEControllerServiceImpl implements PPEControllerService {
    private final PPEService ppeService;
    private final EmployeeService employeeService;

    public PPEForm getPPEForm(Long id) {
        PPEForm ppeForm = new PPEForm();
        try {
            ppeForm = FormConverter.getPPEAsForm(ppeService.getPPE(id));
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
            ppeDto.setOwnerId(employeeService.getEmployeeByPersonnelNumber(Long.valueOf(ownerPersonnelNumber)).getId());
        } catch (NoEntityException e) {
            log.warn("No employee with personnel number: {}", ownerPersonnelNumber);
            ppeDto.setOwnerId(null);
        }
        try {
            ppeService.addPPE(ppeDto);
        } catch (NoEntityException ignored) {
        }
    }
}

package ru.inside.commands.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.dto.PPEDto;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.hyperledger.ChainCodeControllerService;
import ru.inside.commands.repository.EmployeeRepository;
import ru.inside.commands.repository.PPERepository;
import ru.inside.commands.service.PPEService;
import ru.inside.commands.service.helper.DtoConverter;

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
    private final PPERepository ppeRepository;
    private final EmployeeRepository employeeRepository;

    public PPE getPPEByInventoryNumber(String inventoryNumber) throws NoEntityException {
        PPE ppe = ppeRepository.findByInventoryNumber(inventoryNumber).orElseThrow(() ->
                NoEntityException.createWithParam(PPE.class.getSimpleName().toLowerCase(), inventoryNumber));
        return ppe;
    }

    public int getTotalPPE() {
        List<PPE> ppeList = ppeRepository.findAll();
        return ppeList.size();
    }

    public PPE updateStatus(Long id, PPEStatus status) throws NoEntityException {
        PPE ppe = ppeRepository.findById(id).orElseThrow(() ->
                NoEntityException.createWithId(PPE.class.getSimpleName().toLowerCase(), id));
        ppe.setPpeStatus(status);
        return ppeRepository.save(ppe);
    }

    public PPE addPPE(PPEDto ppeDto) throws NoEntityException {
        Employee employee = null;
        Long ownerId = ppeDto.getOwnerId();
        if (ownerId != null) {
            employee = employeeRepository.findById(ownerId).orElseThrow(() ->
                    NoEntityException.createWithId(Employee.class.getSimpleName().toLowerCase(), ownerId));
        }
        return ppeRepository.save(DtoConverter.convertDtoToPPE(ppeDto, employee));
    }

    public void updateAllStatus() {
        List<PPE> ppeList = ppeRepository.findAll();
        ppeList.forEach(ppe -> {
            LocalDateTime startUseDate = ppe.getStartUseDate();
            LocalDateTime nowTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
            Duration lifeTimeSpent = Duration.between(nowTime, startUseDate);
            Duration lifeTimeDuration = ppe.getLifeTime();

            if (ppe.getPpeStatus() != PPEStatus.DECOMMISSIONED
                    && ppe.getPpeStatus() != PPEStatus.SPOILED
                    && lifeTimeSpent.minus(lifeTimeDuration).toDays() < 0) {
                ppe.setPpeStatus(PPEStatus.SPOILED);
            }
        });
    }

    public List<PPEForm> getAllInWaitList() {
        List<PPEForm> ppeFormList = new ArrayList<>();
        PPEForm stubForm = new PPEForm();

        //TODO: stubbed
        stubForm.setInventoryNumber("244405500422");
        stubForm.setLifeTime(Duration.ofDays(300));
        stubForm.setOwnerName("Иванов Иван Петрович");
        stubForm.setOwnerPersonnelNumber("220222");
        stubForm.setStartUseDate(LocalDateTime.now());
        stubForm.setPpeName("Перчатки рабочие, 35");
        stubForm.setPrice(400F);
        stubForm.setPpeStatus(PPEStatus.TRANSFER.toString());
        stubForm.setSubsidiaryName("ГПН-Снабжение");

        ppeFormList.add(stubForm);
        ppeFormList.add(stubForm);
        ppeFormList.add(stubForm);
        ppeFormList.add(stubForm);
        ppeFormList.add(stubForm);
//        chainCodeControllerService.getAllPPE();
        return ppeFormList;
    }
}

package ru.inside.commands.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.dto.PPEDto;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.repository.EmployeeRepository;
import ru.inside.commands.repository.PPERepository;
import ru.inside.commands.service.PPEService;
import ru.inside.commands.service.helper.DtoConverter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PPEServiceImpl implements PPEService {
    private final PPERepository ppeRepository;
    private final EmployeeRepository employeeRepository;

    public PPEDto getPPE(Long id) throws NoEntityException {
        PPE ppe = ppeRepository.findById(id).orElseThrow(() ->
                NoEntityException.createWithId(PPE.class.getSimpleName().toLowerCase(), id));
        return DtoConverter.convertPPEToDto(ppe);
    }

    public PPEDto updateStatus(Long id, PPEStatus status) throws NoEntityException {
        PPE ppe = ppeRepository.findById(id).orElseThrow(() ->
                NoEntityException.createWithId(PPE.class.getSimpleName().toLowerCase(), id));
        ppe.setPpeStatus(status);
        return DtoConverter.convertPPEToDto(ppeRepository.save(ppe));
    }

    public PPEDto addPPE(PPEDto ppeDto) throws NoEntityException {
        Employee employee = null;
        Long ownerId = ppeDto.getOwnerId();
        if (ownerId != null) {
            employee = employeeRepository.findById(ownerId).orElseThrow(() ->
                    NoEntityException.createWithId(Employee.class.getSimpleName().toLowerCase(), ownerId));
        }
        return DtoConverter.convertPPEToDto(ppeRepository.save(DtoConverter.convertDtoToPPE(ppeDto, employee)));
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
}

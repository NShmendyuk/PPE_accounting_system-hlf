package ru.inside.commands.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.repository.PPERepository;
import ru.inside.commands.service.PPEService;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PPEServiceImpl implements PPEService {
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

    public void dismissPPE(String inventoryNumber) throws NoEntityException {
        PPE ppe = ppeRepository.findByInventoryNumber(inventoryNumber).orElseThrow(() ->
                NoEntityException.createWithParam(PPE.class.getSimpleName().toLowerCase(), inventoryNumber));
        ppe.setPpeStatus(PPEStatus.DECOMMISSIONED);
        ppeRepository.save(ppe);
    }

    public void transferPPE(PPE ppe, Subsidiary subsidiary) {
        ppe.setPpeStatus(PPEStatus.TRANSFER);
        ppe.setEmployee(null);
        ppeRepository.save(ppe);
    }
}

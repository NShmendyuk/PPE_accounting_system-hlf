package ru.inside.commands.service;

import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.dto.PPEDto;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.entity.forms.PPEForm;

import java.util.List;

public interface PPEService {
    PPE getPPEByInventoryNumber(String inventoryNumber) throws NoEntityException;
    PPE updateStatus(Long id, PPEStatus status) throws NoEntityException;
    PPE addPPE(PPEDto ppeDto) throws NoEntityException;
    int getTotalPPE();
    List<PPEForm> getAllInWaitList();
    void updateAllStatus();
}

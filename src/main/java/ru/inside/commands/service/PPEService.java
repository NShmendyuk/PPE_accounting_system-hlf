package ru.inside.commands.service;

import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.PPEDto;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.entity.forms.PPEForm;

import java.util.List;

public interface PPEService {
    PPE getPPEByInventoryNumber(String inventoryNumber) throws NoEntityException;
    PPE addPPE(PPE ppe);
    int getTotalPPE();
    void updateAllStatus();
    void transferPPE(PPE ppe, Subsidiary subsidiary);
    void dismissPPE(String inventoryNumber) throws NoEntityException;
    List<PPE> getAllWaitFromChainCode();

}

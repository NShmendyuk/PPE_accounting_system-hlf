package ru.inside.commands.service;

import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;

import java.util.List;

public interface PPEService {
    PPE getPPEByInventoryNumber(String inventoryNumber) throws NoEntityException;
    List<PPE> getAllPPE();
    PPE addPPE(PPE ppe);
    long getTotalPPE();
    void updateAllStatus();
    void transferPPE(PPE ppe, Subsidiary subsidiary);
    void dismissPPE(String inventoryNumber) throws NoEntityException;
    List<PPE> getAllWaitFromChainCode();
    boolean isPPEExist(String inventoryNumber);
}

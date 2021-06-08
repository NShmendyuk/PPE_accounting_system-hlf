package ru.inside.commands.hyperledger;

import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.hyperledger.entity.PPEContract;

import java.util.List;

public interface ChainCodeControllerService {
    void initTestLedger();
    List<PPEContract> getAllPPE();
    PPEContract getPPEByInventoryNumber(String inventoryNumber);
    void addPPE(PPE ppe);
    void changePPE(PPE ppe);
    void transferPPEToSubsidiary(PPE ppe, Subsidiary anotherSubsidiary, String status);
    void applyPPETransfering(String inventoryNumber, String status);

    List<PPEContract> getPPEHistoryByInventoryNumber(String inventoryNumber);

    void deletePPE(String inventoryNumber);
    boolean checkPPEExist(String inventoryNumber);
}

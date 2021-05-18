package ru.inside.commands.hyperledger.controller;

import com.owlike.genson.Genson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.hyperledger.ChainCodeControllerService;
import ru.inside.commands.hyperledger.entity.PPEContract;
import ru.inside.commands.hyperledger.fabric.chaincode.PPEChainCodeService;
import ru.inside.commands.hyperledger.fabric.configuration.HlfConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class HlFChainCodeInstanceControllerService implements ChainCodeControllerService {
    private PPEChainCodeService chainCodeController;
    private final Genson genson = new Genson();

    private HlFChainCodeInstanceControllerService(HlfConfiguration hlfConfiguration) {
        try {
            chainCodeController = new PPEChainCodeService(hlfConfiguration.getContract());
            log.info("ChainCode instance initialized. Connected to hyperledger environment");
        } catch (Exception ex) {
            log.error("Cannot init ChainCode instance. Hyperledger environment unactive", ex);
        }
    }

    public void initTestLedger() {
        try {
            chainCodeController.initializeTestPPELedger();
            log.info("contract (initTestLedger) submit");
        } catch (Exception e) {
            log.error("Init test ledger request to chaincode were denied", e);
        }
    }

    public List<PPEContract> getAllPPE() {
        List<PPEContract> allPPE = new ArrayList<>();
        try {
            allPPE = Arrays.asList(genson.deserialize(chainCodeController.getAllPPE(), PPEContract[].class));
            log.info("chaincode message: {}", allPPE);
        } catch (Exception e) {
            log.error("Get All request to chaincode were denied");
        }
        return allPPE;
    }

    public void addPPE(PPE ppe) {
        String ownerName = "";
        String ownerID = "";
        if (ppe.getEmployee() != null) {
            ownerName = ppe.getEmployee().getEmployeeName();
            ownerID = ppe.getEmployee().getPersonnelNumber();
        }

        String name = ppe.getName();
        String status = ppe.getPpeStatus().toString();
        Float price = ppe.getPrice();
        String inventoryNumber = ppe.getInventoryNumber();
        String startUseDate = ppe.getStartUseDate().toString();
        Integer lifeTime = Integer.parseInt(String.valueOf(ppe.getLifeTime().toDays()));

        String subsidiary = ppe.getEmployee().getSubsidiary().getName(); //must have
        try {
            chainCodeController.addPPE(ownerName, ownerID,
                    name, status, price, inventoryNumber,
                    startUseDate, lifeTime, subsidiary);
            log.info("contract (addPPE) submit");
        } catch (Exception e) {
            log.error("Add new PPE request to chaincode were denied", e);
        }
    }

    public PPEContract getPPEByInventoryNumber(String inventoryNumber) {
        PPEContract ppeInfo = new PPEContract();
        try {
            ppeInfo = genson.deserialize(chainCodeController.getPPE(inventoryNumber), PPEContract.class);
            log.info("chaincode (getPPEByInventoryNumber) message: {}", ppeInfo.toString());
        } catch (Exception ex) {
            log.error("Get ppe {} request to chaincode were denied", inventoryNumber);
        }
        return ppeInfo;
    }

    public List<PPEContract> getPPEHistoryByInventoryNumber(String inventoryNumber) {
        List<PPEContract> ppeHistoryList = new ArrayList<>();
        try {
            ppeHistoryList = Arrays.asList(genson.deserialize(chainCodeController.getPPEHistory(inventoryNumber), PPEContract[].class));
            log.info("contract (getPPEHistoryById) message: {}", ppeHistoryList.toString());
        } catch (Exception ex) {
            log.error("Get ppe {} history request to chaincode were denied", inventoryNumber);
        }
        return ppeHistoryList;
    }

    public void changePPE(PPE ppe) {
        String ownerName = ppe.getEmployee().getEmployeeName();
        String ownerID = ppe.getEmployee().getId().toString();
        String name = ppe.getName();
        String status = ppe.getPpeStatus().toString();
        Float price = ppe.getPrice();
        String inventoryNumber = ppe.getInventoryNumber();
        String startUseDate = ppe.getStartUseDate().toString();
        Integer lifeTime = Integer.parseInt(String.valueOf(ppe.getLifeTime().toDays()));
        String subsidiary = ppe.getEmployee().getSubsidiary().getName();

        try {
            chainCodeController.changePPE(ownerName, ownerID,
                    name, status, price, inventoryNumber,
                    startUseDate, lifeTime, subsidiary);
            log.info("contract (changePPE) submit");
        } catch (Exception e) {
            log.error("change PPE {} request to chaincode were denied", inventoryNumber, e);
        }
    }

    public void deletePPE(String inventoryNumber) {
        try {
            chainCodeController.deletePPE(inventoryNumber);
            log.info("contract (deletePPE) submit");
        } catch (Exception e) {
            log.error("Delete PPE {} request to chaincode were denied", inventoryNumber);
        }
    }

    public boolean checkPPEExist(String inventoryNumber) {
        String ppeInfo = "chaincode executing denied";
        try {
            ppeInfo = Arrays.toString(chainCodeController.isPPEExist(inventoryNumber));
        } catch (Exception e) {
            log.error("Check existing ppe {} sample request to chaincode were denied", inventoryNumber);
        }
        log.info("chaincode message isExist: {}", ppeInfo);
        return ppeInfo.equals("true");
    }

    public void transferPPEToSubsidiary(PPE ppe, Subsidiary anotherSubsidiary) {
        String inventoryNumber = ppe.getInventoryNumber();
        String toSubsidiary = anotherSubsidiary.getName();

        try {
            chainCodeController.transferPPEToAnotherSubsidiary(inventoryNumber, toSubsidiary);
            log.info("contract (transferPPEToSubsidiary) submit");
        } catch (Exception e) {
            log.error("transfer ppe {} request to chaincode were denied", inventoryNumber);
        }
    }
}

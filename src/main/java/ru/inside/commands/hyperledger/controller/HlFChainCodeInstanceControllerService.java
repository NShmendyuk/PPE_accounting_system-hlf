package ru.inside.commands.hyperledger.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.stereotype.Service;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.hyperledger.ChainCodeControllerService;
import ru.inside.commands.hyperledger.fabric.chaincode.PPEChainCodeController;
import ru.inside.commands.hyperledger.fabric.configuration.HlfConfiguration;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class HlFChainCodeInstanceControllerService implements ChainCodeControllerService {
    private PPEChainCodeController chainCodeController;

    private HlFChainCodeInstanceControllerService(HlfConfiguration hlfConfiguration) {
        try {
            chainCodeController = hlfConfiguration.getChainCodeController();
        } catch (Exception ex) {
            log.error("Cannot find ChainCode instance. Hyperledger environment unactive", ex);
        }
    }

    public void initTestLedger() {
        try {
            chainCodeController.initializeTestPPELedger();
        } catch (InvalidArgumentException | ProposalException e) {
            log.error("Init test ledger request to chaincode were denied", e);
        }
    }

    public String getAllPPE() {
        String allPPE = "";
        try {
            allPPE = chainCodeController.getAllPPE();
        } catch (InvalidArgumentException | ProposalException e) {
            log.error("Get All request to chaincode were denied", e);
        }
        log.info("chaincode message: {}", allPPE);
        return allPPE;
    }

    public void addPPE(PPE ppe) {
        String ppeInfo = "";

        String ppeID = ppe.getId().toString();
        String ownerName = ppe.getEmployee().getEmployeeName();
        String ownerID = ppe.getEmployee().getEmployeeID().toString();
        String name = ppe.getName();
        Float price = ppe.getPrice();
        String inventoryNumber = ppe.getInventoryNumber();
        String startUseDate = ppe.getStartUseDate().toString();
        Integer lifeTime = Integer.parseInt(String.valueOf(ppe.getLifeTime().toDays()));
        String subsidiary = ppe.getEmployee().getSubsidiary().getName();
        String prevSubsidiary = "None";
        try {
            ppeInfo = chainCodeController.addPPE(ppeID, ownerName, ownerID,
                    name, price, inventoryNumber, startUseDate, lifeTime, subsidiary, prevSubsidiary);
        } catch (InvalidArgumentException | ProposalException e) {
            log.error("Add new PPE request to chaincode were denied", e);
        }
        log.info("chaincode message: {}", ppeInfo);
    }

    public String getPPEById(Long id) {
        String ppeInfo = "";
        try {
            ppeInfo = chainCodeController.getPPE(id.toString());
        } catch (InvalidArgumentException | ProposalException e) {
            log.error("Get All request to chaincode were denied", e);
        }
        log.info("chaincode message: {}", ppeInfo);
        return ppeInfo;
    }

    public void changePPE(PPE ppe) {
        String ppeInfo = "";

        String ppeID = ppe.getId().toString();
        String ownerName = ppe.getEmployee().getEmployeeName();
        String ownerID = ppe.getEmployee().getId().toString();
        String name = ppe.getName();
        Float price = ppe.getPrice();
        String inventoryNumber = ppe.getInventoryNumber();
        String startUseDate = ppe.getStartUseDate().toString();
        Integer lifeTime = Integer.parseInt(String.valueOf(ppe.getLifeTime().toDays()));
        String subsidiary = ppe.getEmployee().getSubsidiary().getName();
        String prevSubsidiary = "None";

        try {
            ppeInfo = chainCodeController.changePPE(ppeID, ownerName, ownerID,
                    name, price, inventoryNumber, startUseDate, lifeTime, subsidiary, prevSubsidiary);
        } catch (InvalidArgumentException | ProposalException e) {
            log.error("change PPE {} request to chaincode were denied", ppeID, e);
        }
        log.info("chaincode message: {}", ppeInfo);
    }

    public void deletePPE(Long id) {
        try {
            chainCodeController.deletePPE(id.toString());
        } catch (InvalidArgumentException | ProposalException e) {
            log.error("Delete PPE {} request to chaincode were denied", id, e);
        }
    }

    public void checkPPEExist(Long id) {
        String ppeInfo = "";
        try {
            ppeInfo = chainCodeController.isPPEExist(id.toString());
        } catch (InvalidArgumentException | ProposalException e) {
            log.error("Check existing ppe sample request to chaincode were denied", e);
        }
        log.info("chaincode message: {}", ppeInfo);
    }

    public void transferPPEToSubsidiary(PPE ppe, Subsidiary anotherSubsidiary) {
        String ppeID = ppe.getId().toString();
        String newOwnerID = ppe.getEmployee().getId().toString();
        String newInventoryNumber = ppe.getInventoryNumber();
        String fromSubsidiary = ppe.getEmployee().getSubsidiary().getName();
        String toSubsidiary = anotherSubsidiary.getName();

        String ppeInfo = "";
        try {
            ppeInfo = chainCodeController.transferPPEToAnotherSubsidiary(ppeID, newOwnerID, newInventoryNumber,
                    fromSubsidiary, toSubsidiary);
        } catch (InvalidArgumentException | ProposalException e) {
            log.error("transfer ppe sample request to chaincode were denied", e);
        }
        log.info("chaincode message: {}", ppeInfo);
    }

    public void transferPPEToSubsidiary(PPE ppe, Subsidiary anotherSubsidiary, Long ownerID, Long invNumber) {
        String ppeID = ppe.getId().toString();
        String newOwnerID = ownerID.toString();
        String newInventoryNumber = invNumber.toString();
        String fromSubsidiary = ppe.getEmployee().getSubsidiary().getName();
        String toSubsidiary = anotherSubsidiary.getName();

        String ppeInfo = "";
        try {
            ppeInfo = chainCodeController.transferPPEToAnotherSubsidiary(ppeID, newOwnerID, newInventoryNumber,
                    fromSubsidiary, toSubsidiary);
        } catch (InvalidArgumentException | ProposalException e) {
            log.error("transfer ppe sample request to chaincode were denied", e);
        }
        log.info("chaincode message: {}", ppeInfo);
    }
}

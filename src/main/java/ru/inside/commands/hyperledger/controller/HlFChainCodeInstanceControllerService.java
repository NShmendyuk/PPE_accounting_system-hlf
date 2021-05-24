package ru.inside.commands.hyperledger.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private HlFChainCodeInstanceControllerService(HlfConfiguration hlfConfiguration) {
        try {
            chainCodeController = new PPEChainCodeService(hlfConfiguration.getContract());
            if (hlfConfiguration.getContract() != null) {
                log.info("ChainCode instance initialized. Connected to hyperledger environment");
            } else {
                log.warn("Cannot init ChainCode instance. Hyperledger environment unactive");
            }
        } catch (Exception ex) {
            log.error("Initializing ChainCode service layer were denied!");
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
            byte[] ppeContractBytes = chainCodeController.getAllPPE();
            String stringPPEContract = new String(ppeContractBytes);
            log.info("get All PPE Contract as String: {}", stringPPEContract);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);

            PPEContract[] ppeContracts = objectMapper.readValue(stringPPEContract, PPEContract[].class);
            allPPE = Arrays.asList(ppeContracts);
            log.info("chaincode message: {}", allPPE);
        } catch (Exception e) {
            log.error("Get All request to chaincode were denied");
        }
        return allPPE;
    }

    public void addPPE(PPE ppe) {
        String ownerName = "";
        String ownerID = "";
        Subsidiary subsidiary = null;
        if (ppe.getEmployee() != null) {
            ownerName = ppe.getEmployee().getEmployeeName();
            ownerID = ppe.getEmployee().getPersonnelNumber();
            log.info("contract (createPPE) with employee {}", ownerID);
            subsidiary = ppe.getEmployee().getSubsidiary();
        }


        String subsidiaryName = "";
        if (subsidiary != null) {
            log.info("subsidiary:{}", subsidiary);
            subsidiaryName = subsidiary.getName(); //must have
        }

        String name = ppe.getName();
        String status = ppe.getPpeStatus().toString();
        Float price = ppe.getPrice();
        String inventoryNumber = ppe.getInventoryNumber();
        Integer lifeTime = Integer.parseInt(Long.toString(ppe.getLifeTime().toDays()));
        log.info("added info about ppe");

        String startUseDate = "";
        try {
            log.info("try add start use date: {}", ppe.getStartUseDate().toString());
            startUseDate = ppe.getStartUseDate().toString();
        } catch (Exception ex) {
            log.warn("Cannot parse date time to string");
        }

        try {
            log.info("contract (addPPE) sending request");
            chainCodeController.addPPE(ownerName, ownerID,
                    name, status, price, inventoryNumber,
                    startUseDate, lifeTime, subsidiaryName);
            log.info("contract (addPPE) submit");
        } catch (Exception e) {
            log.error("Add new PPE request to chaincode were denied!", e);
        }
    }

    public PPEContract getPPEByInventoryNumber(String inventoryNumber) {
        PPEContract ppeInfo = new PPEContract();
        try {
            byte[] ppeContractBytes = chainCodeController.getPPE(inventoryNumber);

            String stringJson = new String(ppeContractBytes);
            log.info("get ppe as json array string: {}", stringJson);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);

            ppeInfo = objectMapper.readValue(stringJson, PPEContract.class);
//            List<PPEContract> ppeOneList = Arrays.asList(ppeContracts);
//            ppeInfo = ppeOneList.get(0);
            log.info("chaincode (getPPEByInventoryNumber) message: {}", ppeInfo.toString());
        } catch (Exception ex) {
            log.error("Get ppe {} request to chaincode were denied", inventoryNumber);
        }
        return ppeInfo;
    }

    public List<PPEContract> getPPEHistoryByInventoryNumber(String inventoryNumber) {
        List<PPEContract> ppeHistoryList = new ArrayList<>();
        try {
            byte[] historyBytes = chainCodeController.getPPEHistory(inventoryNumber);
            log.info("history: {}", new String(historyBytes));
            String stringJsonHistory = new String(historyBytes);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);

            PPEContract[] ppeContracts = objectMapper.readValue(stringJsonHistory, PPEContract[].class);
            log.info("history of {}: {}", inventoryNumber, ppeContracts);
            ppeHistoryList = Arrays.asList(ppeContracts);
            log.info("contract (getPPEHistoryById) message: {}", ppeHistoryList.toString());
        } catch (Exception ex) {
            log.error("Get ppe {} history request to chaincode were failed!", inventoryNumber);
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
            ppeInfo = new String(chainCodeController.isPPEExist(inventoryNumber));
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

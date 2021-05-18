package ru.inside.commands.hyperledger.fabric.chaincode;

import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import java.util.concurrent.TimeoutException;


@Slf4j
public class PPEChainCodeService {
    private Contract contract;

    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_GET_ALL = "GetAllPPEs";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_INITTESTLEDGER = "InitTestLedger";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_CREATE = "CreatePPE";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_READ = "ReadPPE";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_READHISTORY = "ReadPPEHistory";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_UPDATE = "UpdatePPE";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_DELETE = "DeletePPE";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_ISEXIST = "PPEExists";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_TRANSFER = "TransferPPE";

    public PPEChainCodeService(Contract contract) {
        this.contract = contract;
        log.info("initialized chaincode activity");
    }

    public byte[] getAllPPE() throws ContractException {
        log.info("get all ppe by request to chaincode");
        return contract.evaluateTransaction(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_GET_ALL);
    }

    public void initializeTestPPELedger() throws InterruptedException, TimeoutException, ContractException {
        contract.submitTransaction(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_INITTESTLEDGER);
        log.info("test leadger init requested to chaincode");
    }

    public void addPPE(String ownerName, String ownerID, String name, String status, Float price, String inventoryNumber,
                         String startUseDate, Integer lifeTime, String subsidiary)
            throws InterruptedException, TimeoutException, ContractException {
        log.info("create ppe by request to chaincode with inv numb:{}, employee name: {}, ppe name: {}, subsidiary: {}",
                inventoryNumber, ownerName, name, subsidiary);
        contract.submitTransaction(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_CREATE, ownerName, ownerID, name, status,
                price.toString(), inventoryNumber, startUseDate, lifeTime.toString(), subsidiary);
    }

    public byte[] getPPE(String ppeInventoryNumber) throws ContractException {
        log.info("read ppe by request to chaincode with ppe inventory number:{}", ppeInventoryNumber);
        return contract.evaluateTransaction(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_READ, ppeInventoryNumber);
    }

    public byte[] getPPEHistory(String ppeID) throws ContractException {
        log.info("read ppe history by request to chaincode with ppe id:{}", ppeID);
        return contract.evaluateTransaction(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_READHISTORY, ppeID);
    }

    public void changePPE(String ownerName, String ownerID,
                          String name, String status, Float price, String inventoryNumber,
                            String startUseDate, Integer lifeTime, String subsidiary)
            throws InterruptedException, TimeoutException, ContractException {
        log.info("update ppe {} by request to chaincode", inventoryNumber);
        contract.submitTransaction(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_UPDATE, ownerName, ownerID, name, status,
                price.toString(), inventoryNumber, startUseDate, lifeTime.toString(), subsidiary);
    }

    public void deletePPE(String inventoryNumber) throws InterruptedException, TimeoutException, ContractException {
        log.info("delete ppe {} by request to chaincode", inventoryNumber);
        contract.submitTransaction(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_DELETE, inventoryNumber);
    }

    public byte[] isPPEExist(String inventoryNumber) throws ContractException {
        log.info("check if ppe {} exist by request to chaincode", inventoryNumber);
        return contract.evaluateTransaction(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_ISEXIST, inventoryNumber);
    }

    /**
     * @param inventoryNumber - PPE inventory number
     * @param toSubsidiary - the company to which the employee is transferred
     */
    public void transferPPEToAnotherSubsidiary(String inventoryNumber, String toSubsidiary)
            throws InterruptedException, TimeoutException, ContractException {
        log.info("transfer ppe {} by request to chaincode to subsidiary: {}", inventoryNumber, toSubsidiary);
        contract.submitTransaction(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_TRANSFER, inventoryNumber, toSubsidiary);
    }
}

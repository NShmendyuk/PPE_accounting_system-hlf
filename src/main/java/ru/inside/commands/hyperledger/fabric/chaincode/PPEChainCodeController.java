package ru.inside.commands.hyperledger.fabric.chaincode;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import ru.inside.commands.hyperledger.fabric.client.HlfClient;

import java.util.Collection;

@NoArgsConstructor
@Slf4j
public class PPEChainCodeController {
    private Channel currentChannel;

    private QueryByChaincodeRequest queryByChainCodeRequest;
    private ChaincodeID ppeContract;

    private String chainCodeName = "ppesmart";

    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_GET_ALL = "GetAllPPEs";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_INITTESTLEDGER = "InitTestLedger";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_CREATE = "CreatePPE";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_READ = "ReadPPE";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_UPDATE = "UpdatePPE";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_DELETE = "DeletePPE";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_ISEXIST = "PPEExists";
    private String HLF_CHAINCODE_PPESMART_FUNCTION_NAME_TRANSFER = "TransferPPE";

    public void initializeChainCodeActivity(HlfClient hlfClient, Channel currentChannel) {
        log.info("initialize chaincode activity");
        this.currentChannel = currentChannel;
        queryByChainCodeRequest = hlfClient.getClient().newQueryProposalRequest();
        ppeContract = ChaincodeID.newBuilder().setName(chainCodeName).build();
        queryByChainCodeRequest.setChaincodeID(ppeContract);
        log.info("initialized chaincode successfull");
    }

    public String getAllPPE() throws InvalidArgumentException, ProposalException {
        log.info("get all ppe by request to chaincode");
        queryByChainCodeRequest.setFcn(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_GET_ALL);
        Collection<ProposalResponse> res = currentChannel.queryByChaincode(queryByChainCodeRequest);
        String stringResponse = "no response";
        for (ProposalResponse pres : res) {
            stringResponse = new String(pres.getChaincodeActionResponsePayload());
            log.info(stringResponse);
        }
        return stringResponse;
    }

    public void initializeTestPPELedger() throws InvalidArgumentException, ProposalException  {
        log.info("test leadger init request to chaincode");
        queryByChainCodeRequest.setFcn(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_INITTESTLEDGER);
        currentChannel.queryByChaincode(queryByChainCodeRequest);
    }

    public String addPPE(String ppeID, String ownerName,
                         String ownerID, String name,
                         Float price, String inventoryNumber,
                         String startUseDate, Integer lifeTime,
                         String subsidiary, String prevSubsidiary)
            throws InvalidArgumentException, ProposalException {
        log.info("create ppe by request to chaincode with id:{}, employee name: {}, ppe name: {}, subsidiary: {}",
                ppeID, ownerName, name, subsidiary);

        queryByChainCodeRequest.setFcn(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_CREATE);
        queryByChainCodeRequest.setArgs(ppeID, ownerName, ownerID, name, String.valueOf(price),
                inventoryNumber, startUseDate, String.valueOf(lifeTime), subsidiary, prevSubsidiary);

        Collection<ProposalResponse> res = currentChannel.queryByChaincode(queryByChainCodeRequest);
        String stringResponse = "no response";
        for (ProposalResponse pres : res) {
            stringResponse = new String(pres.getChaincodeActionResponsePayload());
            log.info(stringResponse);
        }
        return stringResponse;
    }

    public String getPPE(String ppeID) throws InvalidArgumentException, ProposalException {
        log.info("read ppe by request to chaincode with id:{}", ppeID);
        queryByChainCodeRequest.setFcn(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_READ);
        queryByChainCodeRequest.setArgs(ppeID);

        Collection<ProposalResponse> res = currentChannel.queryByChaincode(queryByChainCodeRequest);
        String stringResponse = "no response";
        for (ProposalResponse pres : res) {
            stringResponse = new String(pres.getChaincodeActionResponsePayload());
            log.info(stringResponse);
        }
        return stringResponse;
    }

    public String changePPE(String ppeID, String ownerName,
                            String ownerID, String name, Float price,
                            String inventoryNumber,
                            String startUseDate,
                            Integer lifeTime, String subsidiary,
                            String prevSubsidiary)
            throws InvalidArgumentException, ProposalException {
        log.info("update ppe {} by request to chaincode", ppeID);
        queryByChainCodeRequest.setFcn(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_UPDATE);
        queryByChainCodeRequest.setArgs(ppeID, ownerName, ownerID, name, String.valueOf(price),
                inventoryNumber, startUseDate, String.valueOf(lifeTime), subsidiary, prevSubsidiary);

        Collection<ProposalResponse> res = currentChannel.queryByChaincode(queryByChainCodeRequest);
        String stringResponse = "no response";
        for (ProposalResponse pres : res) {
            stringResponse = new String(pres.getChaincodeActionResponsePayload());
            log.info(stringResponse);
        }
        return stringResponse;
    }

    public void deletePPE(String ppeID) throws ProposalException, InvalidArgumentException {
        log.info("delete ppe {} by request to chaincode", ppeID);
        queryByChainCodeRequest.setFcn(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_DELETE);
        queryByChainCodeRequest.setArgs(ppeID);

        currentChannel.queryByChaincode(queryByChainCodeRequest);
    }

    public String isPPEExist(String ppeID) throws ProposalException, InvalidArgumentException {
        log.info("check if ppe {} exist by request to chaincode", ppeID);
        queryByChainCodeRequest.setFcn(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_ISEXIST);
        queryByChainCodeRequest.setArgs(ppeID);

        Collection<ProposalResponse> res = currentChannel.queryByChaincode(queryByChainCodeRequest);
        String stringResponse = "no response";
        for (ProposalResponse pres : res) {
            stringResponse = new String(pres.getChaincodeActionResponsePayload());
            log.info(stringResponse);
        }
        return stringResponse;
    }

    /**
     *
     * @param ppeID - inventory number of PPE in subsidiary
     * @param newOwnerID - employee id in another subsidiary where he transfer
     * @param newInventoryNumber - new inventory number in another subsidiary
     * @param fromSubsidiary - the company from which the employee is transferred
     * @param toSubsidiary - the company to which the employee is transferred
     * @return info about ppe transferring
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public String transferPPEToAnotherSubsidiary(String ppeID,
                                                 String newOwnerID,
                                                 String newInventoryNumber,
                                                 String fromSubsidiary,
                                                 String toSubsidiary)
            throws ProposalException, InvalidArgumentException {
        log.info("transfer ppe {} by request to chaincode to subsidiary: {}, from {}. owner new ID: {}", ppeID,
                toSubsidiary, fromSubsidiary, newOwnerID);
        queryByChainCodeRequest.setFcn(HLF_CHAINCODE_PPESMART_FUNCTION_NAME_TRANSFER);
        queryByChainCodeRequest.setArgs(ppeID, newOwnerID, newInventoryNumber, fromSubsidiary, toSubsidiary);

        Collection<ProposalResponse> res = currentChannel.queryByChaincode(queryByChainCodeRequest);
        String stringResponse = "no response";
        for (ProposalResponse pres : res) {
            stringResponse = new String(pres.getChaincodeActionResponsePayload());
            log.info(stringResponse);
        }
        return stringResponse;
    }
}

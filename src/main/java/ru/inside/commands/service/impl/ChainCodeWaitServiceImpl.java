package ru.inside.commands.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.hyperledger.entity.PPEContract;
import ru.inside.commands.hyperledger.service.ChainCodeControllerService;
import ru.inside.commands.service.ChainCodeWaitService;
import ru.inside.commands.service.PPEService;
import ru.inside.commands.service.helper.PPEConverter;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChainCodeWaitServiceImpl implements ChainCodeWaitService {
    public final ChainCodeControllerService chainCodeControllerService;
    public final PPEService ppeService;

    public List<PPE> getAllWaitFromChainCode(Subsidiary selfSubsidiary) {
        List<PPEContract> ppeContracts = new ArrayList<>();
        try{
            ppeContracts = chainCodeControllerService.getAllPPE();
        } catch (NullPointerException ex) {
            log.warn("ChainCode getAllPPE returned null.");
        } catch (Exception ex) {
            log.error("Cannot getAllPPE from chaincode controller!!!");
        }
        List<PPE> ppeWaitList = new ArrayList<>();


        String selfSubsidiaryName = selfSubsidiary.getName();
        ppeContracts.forEach(ppeContract -> {
            try {
                //it's for self organisation (subsidiary)
                //and ppe is not exist at self subsidiary
                //or exist with transfered status
                if (ppeContract.getSubsidiary().equals(selfSubsidiaryName) &&
                        (!ppeService.isPPEExist(ppeContract.getInventoryNumber()) ||
                                (ppeService.getPPEByInventoryNumber(ppeContract.getInventoryNumber())
                                        .getPpeStatus().equals(PPEStatus.TRANSFER)))) {
                    PPE ppe = PPEConverter.ppeContractToPPE(ppeContract);

                    Employee employee = Employee.builder()
                            .personnelNumber(ppeContract.getOwnerID())
                            .employeeName(ppeContract.getOwnerName())
                            .subsidiary(selfSubsidiary).build();

                    ppe.setEmployee(employee);
                    ppeWaitList.add(ppe);
                }
            } catch (NoEntityException e) {
                log.error("Cannot get self subsidiary definition while get all from waitList");
            }

        });
        return ppeWaitList;
    }
}

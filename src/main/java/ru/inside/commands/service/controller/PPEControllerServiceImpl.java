package ru.inside.commands.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.hyperledger.ChainCodeControllerService;
import ru.inside.commands.hyperledger.entity.PPEContract;
import ru.inside.commands.service.EmployeeService;
import ru.inside.commands.service.PPEService;
import ru.inside.commands.service.helper.FormConverter;
import ru.inside.commands.service.helper.PPEConverter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PPEControllerServiceImpl implements PPEControllerService {
    private final PPEService ppeService;
    private final EmployeeService employeeService;
    private final ChainCodeControllerService chainCodeControllerService;

    public PPEForm getPPEForm(String inventoryNumber) {
        PPEForm ppeForm = new PPEForm();
        try {
            ppeForm = FormConverter.getPPEAsForm(ppeService.getPPEByInventoryNumber(inventoryNumber));
        } catch (NoEntityException e) {
            log.warn("{}", e.getMessage());
        }
        log.info("Found: {}", ppeForm.toString());
        return ppeForm;
    }

    public void addPPEForm(String name, Float price, String inventoryNumber,
                           String ownerPersonnelNumber,  LocalDate date, Long lifeTimeDays) {
        log.info("add new ppe: {}, owner: {}", name, ownerPersonnelNumber);
        PPE ppe = new PPE();
        ppe.setName(name);
        ppe.setPrice(price);
        ppe.setInventoryNumber(inventoryNumber);
        ppe.setLifeTime(Duration.ofDays(lifeTimeDays));
        ppe.setPpeStatus(PPEStatus.APPLIED);

        ppe.setStartUseDate(null);
        try {
            ppe = ppeService.addPPE(ppe);
        } catch (Exception ex) {
            log.error("Cannot add new ppe {}! Check inventory number for existing", inventoryNumber);
            return ;
        }

        if (!ownerPersonnelNumber.equals("")) {
            try {
                Employee employee = employeeService.addPPEToEmployee(ppe, ownerPersonnelNumber);
                ppe.setEmployee(employee);
                log.info("PPE {} is assigned to employee {}", inventoryNumber, employee.getPersonnelNumber());
            } catch (NoEntityException ex) {
                log.error("Cannot add ppe {} to employee {}", inventoryNumber, ownerPersonnelNumber);
            } catch (Exception ex) {
                log.error("Something went wrong while add ppe to employee");
            }
        }

        try {
            addPPEToChainCode(ppeService.getPPEByInventoryNumber(inventoryNumber));
        } catch (NoEntityException e) {
            log.error("PPE was not added into database!!! Ask IT-developer to rewrite code!!");
        }
    }

    private void addPPEToChainCode(PPE ppe) {
        try {
            log.info("try to add ppe into chaincode");
            chainCodeControllerService.addPPE(ppe);
        } catch (NullPointerException ex) {
            log.warn("Cannot add ppe {} into chaincode", ppe.getInventoryNumber());
        } catch (Exception ex) {
            log.error("Error! Cannot add ppe {} into chaincode", ppe.getInventoryNumber());
        }
    }

    public List<PPEForm> getPPEHistory(String inventoryNumber) {
        List<PPEContract> ppeHistoryList = chainCodeControllerService.getPPEHistoryByInventoryNumber(inventoryNumber);
        List<PPEForm> ppeHistoryForms = new ArrayList<>();
        ppeHistoryList.forEach(ppeContract -> {
            PPEForm ppeForm = new PPEForm();
            ppeForm.setInventoryNumber(ppeContract.getInventoryNumber());
            ppeForm.setPpeName(ppeContract.getName());
            ppeForm.setPrice(ppeContract.getPrice());
            ppeForm.setLifeTime(Duration.ofDays(ppeContract.getLifeTime()));
            ppeForm.setStartUseDate(LocalDateTime.parse(ppeContract.getStartUseDate()));
            ppeForm.setPpeStatus(ppeContract.getStatus());

            ppeForm.setOwnerPersonnelNumber(ppeContract.getOwnerID());
            ppeForm.setOwnerName(ppeContract.getOwnerName());
            ppeForm.setSubsidiaryName(ppeContract.getSubsidiary());
            ppeHistoryForms.add(ppeForm);
        });
        return ppeHistoryForms;
    }

    public void decommissioning(String employeePersonnelNumber, String inventoryNumber) {
        Employee employee;
        try {
            employee = employeeService.getEmployeeByPersonnelNumber(employeePersonnelNumber);
        } catch (NoEntityException e) {
            log.warn("Employee {} not found! Cannot decommission ppe {}", employeePersonnelNumber, inventoryNumber);
            return;
        }
        List<PPE> updatedPPEList = new ArrayList<>();
        employee.getPpe().forEach(ppe -> {
            if (!ppe.getInventoryNumber().equals(inventoryNumber)) {
                updatedPPEList.add(ppe);
            }
        });
        employee.setPpe(updatedPPEList);
        try {
            employeeService.addEmployee(employee);
        } catch (NoEntityException e) {
            log.error("Cannot update list of ppe to employee {} by saving", employeePersonnelNumber);
        }
        try {
            ppeService.dismissPPE(inventoryNumber);
        } catch (NoEntityException e) {
            log.error("ppe {} not found while executing process to decommission!", inventoryNumber);
        }
    }

    public List<PPEForm> getAllInWaitList() {
        List<PPE> ppeList = ppeService.getAllWaitFromChainCode();
        List<PPEForm> ppeFormList = new ArrayList<>();
        ppeList.forEach(ppe -> {
            PPEForm ppeForm = new PPEForm();

            ppeForm.setPpeName(ppe.getName());
            ppeForm.setPrice(ppe.getPrice());
            ppeForm.setInventoryNumber(ppe.getInventoryNumber());
            ppeForm.setPpeStatus(ppe.getPpeStatus().toString());
            ppeForm.setLifeTime(ppe.getLifeTime());
            ppeForm.setStartUseDate(ppe.getStartUseDate());

            ppeForm.setOwnerPersonnelNumber(ppe.getEmployee().getPersonnelNumber());
            ppeForm.setOwnerName(ppe.getEmployee().getEmployeeName());
            ppeForm.setSubsidiaryName(ppe.getEmployee().getSubsidiary().getName());

            ppeFormList.add(ppeForm);
        });
        return ppeFormList;
    }

    public void applyPPEFromChainCode(String inventoryNumber) {
        PPEContract ppeContract = chainCodeControllerService.getPPEByInventoryNumber(inventoryNumber);
        Employee employee;
        try {
            employee = employeeService.getEmployeeByPersonnelNumber(ppeContract.getOwnerID());
        } catch (NoEntityException e) {
            log.error("Cannot find employee {} for transfering ppe {} from another subsidiary by chaincode",
                    ppeContract.getOwnerID(), inventoryNumber);
            return;
        }

        PPE ppe = PPEConverter.ppeContractToPPE(ppeContract, employee);
        try {
            PPE existedPPE = ppeService.getPPEByInventoryNumber(inventoryNumber);
            ppe.setId(existedPPE.getId());
        } catch (NoEntityException ignored) {
        }

        try {
            employeeService.addPPEToEmployee(ppe, employee.getPersonnelNumber());
        } catch (NoEntityException e) {
            log.error("Employee {} were deleted from database while adding new ppe {}!!! Cannot add new ppe from chaincode",
                    employee.getPersonnelNumber(), inventoryNumber);
        }
    }

    public void applyAllPPEFromChainCode(List<PPEForm> ppeWaitList) {
        ppeWaitList.forEach(ppeForm -> {
            applyPPEFromChainCode(ppeForm.getInventoryNumber());
        });
    }
}

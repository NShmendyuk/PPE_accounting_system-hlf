package ru.inside.commands.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
import ru.inside.commands.service.SubsidiaryService;
import ru.inside.commands.service.helper.FormConverter;
import ru.inside.commands.service.helper.PPEConverter;
import ru.inside.commands.service.helper.PdfGenerator;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PPEControllerServiceImpl implements PPEControllerService {
    private final PPEService ppeService;
    private final EmployeeService employeeService;
    private final ChainCodeControllerService chainCodeControllerService;
    private final SubsidiaryService subsidiaryService;
    private final PdfGenerator pdfGenerator;

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

    public List<PPEForm> getAllPPEForms() {
        List<PPE> ppeList = ppeService.getAllPPE();
        List<PPEForm> ppeFormList = new ArrayList<>();

        ppeList.forEach(ppe -> {
            PPEForm ppeForm = new PPEForm();
            ppeForm.setPpeName(ppe.getName());
            ppeForm.setInventoryNumber(ppe.getInventoryNumber());
            ppeForm.setLifeTime(ppe.getLifeTime());
            ppeForm.setStartUseDate(ppe.getStartUseDate());
            ppeForm.setPpeStatus(ppe.getPpeStatus().toString());
            ppeForm.setPrice(ppe.getPrice());
            try {
                ppeForm.setOwnerName(ppe.getEmployee().getEmployeeName());
                ppeForm.setOwnerPersonnelNumber(ppe.getEmployee().getPersonnelNumber());
                ppeForm.setSubsidiaryName(ppe.getEmployee().getSubsidiary().getName());
            } catch (Exception ex) {
                ppeForm.setOwnerName("Неопределено");
                ppeForm.setOwnerPersonnelNumber("Отсутствует");
                //TODO: stubbed (need enum)
                ppeForm.setSubsidiaryName("НЕОПРЕДЕЛЕНО");
            }
            ppeFormList.add(ppeForm);
        });

        return ppeFormList;
    }

    public void addPPEForm(String name, Float price, String inventoryNumber,
                           String ownerPersonnelNumber,  LocalDate date, Long lifeTimeDays) {
        log.info("add new ppe: {}, owner: {}", name, ownerPersonnelNumber);

        //TODO: refactor check
        if (ownerPersonnelNumber == null || date == null) {
            return;
        }

        try {
            employeeService.getEmployeeByPersonnelNumber(ownerPersonnelNumber);
        } catch (NoEntityException e) {
            log.warn("Adding ppe to ppe lifecycle system were denied!");
            return;
        }

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

        if (!(ownerPersonnelNumber == null || ownerPersonnelNumber.equals("")) && (date != null)) {
            ppe.setStartUseDate(LocalDateTime.of(date, LocalTime.MIDNIGHT));
            try {
                Employee employee = employeeService.addPPEToEmployee(ppe, ownerPersonnelNumber);
                ppe.setEmployee(employee);
                log.info("PPE {} is assigned to employee {}", inventoryNumber, employee.getPersonnelNumber());
            } catch (NoEntityException ex) {
                log.error("Cannot add ppe {} to employee {}", inventoryNumber, ownerPersonnelNumber);
            } catch (Exception ex) {
                log.error("Something went wrong while add ppe to employee");
            }
        } else {
            Employee employee = new Employee();
            try {
                employee.setSubsidiary(subsidiaryService.getSelfSubsidiary());
            } catch (NoEntityException e) {
                log.warn("Cannot set subsidiary for PPE on contract");
            }
            ppe.setEmployee(employee);
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
            try {
                ppeForm.setStartUseDate(LocalDateTime.parse(ppeContract.getStartUseDate()));
            } catch (Exception ex) {
                log.warn("Cannot parse LocalDateTime (as String to LocalDateTime) from PPEContract");
            }
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
        List<PPE> ppeList = new ArrayList<>();
        try {
            ppeList = ppeService.getAllWaitFromChainCode();
        } catch (Exception ex) {
            log.error("Cannot get all wait ppe from chaincode!");
        }

        List<PPEForm> ppeFormList = new ArrayList<>();
        ppeList.forEach(ppe -> {
            PPEForm ppeForm = new PPEForm();

            try {
                ppeForm.setPpeName(ppe.getName());
                ppeForm.setPrice(ppe.getPrice());
                ppeForm.setInventoryNumber(ppe.getInventoryNumber());
            } catch (Exception ex) {
                log.error("Cannot set first info of ppe from waitList to form");
            }

            try {
                ppeForm.setPpeStatus(ppe.getPpeStatus().toString());
                ppeForm.setLifeTime(ppe.getLifeTime());
                ppeForm.setStartUseDate(ppe.getStartUseDate());
            } catch (Exception ex) {
                log.error("Cannot set time and status info of ppe from waitList to form");
            }

            try {
                ppeForm.setOwnerPersonnelNumber(ppe.getEmployee().getPersonnelNumber());
                ppeForm.setOwnerName(ppe.getEmployee().getEmployeeName());
            } catch (Exception ex) {
                log.error("Cannot set second info of ppe from waitList to form");
            }

            try {
                ppeForm.setSubsidiaryName(ppe.getEmployee().getSubsidiary().getName());
            } catch (Exception ex) {
                log.warn("Set subsidiary to self subsidiary");
                try {
                    ppeForm.setSubsidiaryName(subsidiaryService.getSelfSubsidiary().getName());
                } catch (NoEntityException e) {
                    log.error("Cannot get self subsidiary definition while convert ppe to form from waitList!!!");
                }
            }

            ppeFormList.add(ppeForm);
        });
        return ppeFormList;
    }

    public byte[] applyPPEFromChainCode(String inventoryNumber) {
        PPE ppe = new PPE();
        try {
            ppe = applyPPEProcess(inventoryNumber);
            log.info("apply PPE {}", ppe.getInventoryNumber());
        } catch (Exception ex) {
            log.error("Cannot apply PPE when apply proccess from chaincode!");
        }

        Employee employee = new Employee();
        try {
            employee = applyToEmployeeProcess(ppe);
            log.info("apply PPE for employee {}", employee.getPersonnelNumber());
        } catch (Exception ex) {
            log.error("Cannot apply PPE to Employee when apply process from chaincode!");
        }

        File file = pdfGenerator.generateSingleApplyTransferDocument(ppe, employee);
        byte[] bytePdfArray = new byte[0];
        try {
            bytePdfArray = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.warn("generated file not found");
        }
        return bytePdfArray;
    }

    public byte[] applyAllPPEFromChainCode() {
        List<PPEForm> waitAllPPE = getAllInWaitList();

        waitAllPPE.forEach(ppeForm -> {
            PPE ppe = applyPPEProcess(ppeForm.getInventoryNumber());
            applyToEmployeeProcess(ppe);
        });

        File file = pdfGenerator.generateAllApplyTransferDocument(waitAllPPE);

        byte[] bytePdfArray = new byte[0];
        try {
            bytePdfArray = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.warn("generated file not found");
        }
        return bytePdfArray;
    }

    private PPE applyPPEProcess(String inventoryNumber) {
        PPEContract ppeContract = chainCodeControllerService.getPPEByInventoryNumber(inventoryNumber);
        Employee employee;
        try {
            employee = employeeService.getEmployeeByPersonnelNumber(ppeContract.getOwnerID());
        } catch (NoEntityException e) {
            log.error("Cannot find employee {} for transfering ppe {} from another subsidiary by chaincode",
                    ppeContract.getOwnerID(), inventoryNumber);
            return new PPE();
        }

        PPE ppe = new PPE();
        try {
            ppe = PPEConverter.ppeContractToPPE(ppeContract);
        } catch (Exception ex) {
            log.error("Cannot convert ppe while apply!");
        }

        try {
            if (ppeService.isPPEExist(inventoryNumber)) {
                PPE existedPPE = ppeService.getPPEByInventoryNumber(inventoryNumber);
                ppe.setId(existedPPE.getId());
            }
            ppe.setEmployee(employee);
            return ppeService.addPPE(ppe);
        } catch (Exception ex) {
            log.error("Cannot add ppe from waitlist while apply! Try to add with employee");
        }
        return ppe;
    }

    private Employee applyToEmployeeProcess(PPE ppe) {
        Employee employee = new Employee();

        try {
            employee = ppe.getEmployee();
            log.info("add ppe {} to employee {}!", ppe.getInventoryNumber(), employee.getPersonnelNumber());
            return employeeService.addPPEToEmployee(ppe, employee.getPersonnelNumber());
        } catch (NoEntityException e) {
            log.error("Employee {} were deleted from database while adding new ppe {}!!! Cannot add new ppe from chaincode",
                    employee.getPersonnelNumber(), ppe.getInventoryNumber());
        }
        return employee;
    }

}

package ru.inside.commands.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.EmployeeDto;
import ru.inside.commands.entity.enums.SubsidiaryStatus;
import ru.inside.commands.entity.forms.EmployeeForm;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.entity.forms.SubsidiaryForm;
import ru.inside.commands.service.EmployeeService;
import ru.inside.commands.service.PPEService;
import ru.inside.commands.service.SubsidiaryService;
import ru.inside.commands.service.helper.PdfGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeControllerServiceImpl implements EmployeeControllerService {
    private final EmployeeService employeeService;
    private final SubsidiaryService subsidiaryService;
    private final PPEService ppeService;
    private final PdfGenerator pdfGenerator;

    public List<EmployeeForm> getAllEmployee() {
        List<EmployeeForm> employeeFormList = new ArrayList<>();
        List<Employee> employeeList = employeeService.getAllEmployee();
        employeeList.forEach(employee -> {

            EmployeeForm employeeForm = new EmployeeForm();
            employeeForm.setEmployeeName(employee.getEmployeeName());
            employeeForm.setOccupation(employee.getOccupation());
            employeeForm.setPersonnelNumber(employee.getPersonnelNumber());

            employeeFormList.add(employeeForm);
        });

        return employeeFormList;
    }

    public List<PPEForm> getAllPPEOfEmployee(String personnelNumber) {
        Employee employee = new Employee();
        employee.setEmployeeName("Empty employee");
        try {
            employee = employeeService.getEmployeeByPersonnelNumber(personnelNumber);
        } catch (NoEntityException e) {
            log.error("Employee with personnel number \"{}\" not found", personnelNumber);
            return new ArrayList<PPEForm>();
        }
        List<PPE> ppeList = employee.getPpe();
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

    public EmployeeForm getEmployeeByPersonnelNumber(String personnelNumber) {
        Employee employee = new Employee();
        EmployeeForm employeeForm = new EmployeeForm();
        try {
            employee = employeeService.getEmployeeByPersonnelNumber(personnelNumber);
        } catch (NoEntityException e) {
            log.error("{}", e.getMessage());
        }

        employeeForm.setEmployeeName(employee.getEmployeeName());
        employeeForm.setOccupation(employee.getOccupation());
        employeeForm.setPersonnelNumber(employee.getPersonnelNumber());

        return employeeForm;
    }

    public List<SubsidiaryForm> getAllSubsidiary() {
        List<SubsidiaryForm> subsidiaryFormList = new ArrayList<>();
        List<Subsidiary> subsidiaryList = subsidiaryService.getAll();
        subsidiaryList.forEach(subsidiary -> {
            SubsidiaryForm subsidiaryForm = new SubsidiaryForm();
            subsidiaryForm.setName(subsidiary.getName());
            subsidiaryForm.setPeerName(subsidiary.getPeerName());
            subsidiaryForm.setStatus(SubsidiaryStatus.ACCESSED); //TODO: stubbed for list
            subsidiaryFormList.add(subsidiaryForm);
        });
        return subsidiaryFormList;
    }

    public byte[] transferEmployeeToSubsidiary(String personnelNumber, String subsidiaryName) {
        try {
            String selfSubsidiaryName = subsidiaryService.getSelfSubsidiary().getName();
            if (subsidiaryName.equals(selfSubsidiaryName)) {
                log.warn("transfer to self organization not valid!");
                return new byte[0];
            }
        } catch (NoEntityException ignored) {
        }

        Employee employee;
        try {
            employee = employeeService.getEmployeeByPersonnelNumber(personnelNumber);
        } catch (NoEntityException e) {
            log.error("Cannot find employee {}!! Cannot transfer to {}", personnelNumber, subsidiaryName);
            return null;
        }

        Subsidiary subsidiary;
        try {
            subsidiary = subsidiaryService.getByName(subsidiaryName);
            employee.setSubsidiary(subsidiary);
        } catch (NoEntityException e) {
            log.error("Cannot find definition for subsidiary {}", subsidiaryName);
            return "NOT FOUND! NOT TRANSFERED".getBytes();
        }
        employee.getPpe().forEach(ppe -> {
            ppeService.transferPPE(ppe, subsidiary);
            log.info("need to transfer by smartContract for ppe: {}, status: {} to {}",
                    ppe.getName(), ppe.getPpeStatus(), subsidiary.getName());
        });

        File file = pdfGenerator.generateTransferDocument(employee, subsidiaryName);
        byte[] bytePdfArray = new byte[0];
        try {
            bytePdfArray = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.warn("generated file not found");
        }

        try {
            employeeService.dismissEmployee(personnelNumber);
        } catch (NoEntityException e) {
            log.error("Cannot dismiss employee {} when ppe transfer! Employee not found", personnelNumber);
        }

        return bytePdfArray;
    }

    public void addEmployee(String name, String occupation, String personnelNumber) {
        Employee employee = new Employee();
        employee.setEmployeeName(name);
        employee.setOccupation(occupation);
        employee.setPersonnelNumber(personnelNumber);
        try {
            employeeService.addEmployee(employee);
        } catch (NoEntityException e) {
            log.error("Add employee to database failed!");
        }
    }
}

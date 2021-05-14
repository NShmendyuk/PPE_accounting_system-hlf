package ru.inside.commands.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeControllerServiceImpl implements EmployeeControllerService {
    private final EmployeeService employeeService;
    private final SubsidiaryService subsidiaryService;
    private final PPEService ppeService;

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

    public void transferEmployeeToSubsidiary(String personnelNumber, String subsidiaryName) {
        Employee employee;
        try {
            employee = employeeService.getEmployeeByPersonnelNumber(personnelNumber);
        } catch (NoEntityException e) {
            log.error("{}", e.getMessage());
            return ;
        }

        try {
            employee.setSubsidiary(subsidiaryService.getByName(subsidiaryName));
        } catch (NoEntityException e) {
            log.error("{}", e.getMessage());
        }
        employee.getPpe().forEach(ppe -> {
            //TODO: by smartContract
            log.info("need to transfer by smartContract for ppe: {}, status: {}", ppe.getName(), ppe.getPpeStatus());
        });

    }

    public void addEmployee(String name, String occupation, String personnelNumber) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmployeeName(name);
        employeeDto.setOccupation(occupation);
        employeeDto.setPersonnelNumber(personnelNumber);
        try {
            employeeService.addEmployee(employeeDto);
        } catch (NoEntityException e) {
            log.error("Add employee to database failed!");
        }
    }
}

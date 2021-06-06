package ru.inside.commands.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.repository.EmployeeRepository;
import ru.inside.commands.service.EmployeeService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    public Employee getEmployeeByPersonnelNumber(String personnelNumber) throws NoEntityException {
        return employeeRepository.findByPersonnelNumber(personnelNumber).orElseThrow(() ->
                NoEntityException.createWithParam(Employee.class.getSimpleName().toLowerCase(), personnelNumber));
    }

    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    public void dismissEmployee(String personnelNumber) throws NoEntityException {
        Employee employee = employeeRepository.findByPersonnelNumber(personnelNumber).orElseThrow(() ->
                NoEntityException.createWithParam(Employee.class.getSimpleName().toLowerCase(), personnelNumber));
        employee.setSubsidiary(null);
        employeeRepository.delete(employee);
        log.info("employee {} were deleted from organization", personnelNumber);
    }

    public Employee addEmployee(Employee employee, Subsidiary selfSubsidiary) {
        employee.setSubsidiary(selfSubsidiary);
        return employeeRepository.save(employee);
    }

    public Employee addPPEToEmployee(PPE ppe, String ownerPersonnelNumber) throws NoEntityException {
        Employee employee = employeeRepository.findByPersonnelNumber(ownerPersonnelNumber).orElseThrow(() ->
                NoEntityException.createWithParam(Employee.class.getSimpleName(), ownerPersonnelNumber));
        ppe.setPpeStatus(PPEStatus.COMMISSIONED);
        List<PPE> ppeList = employee.getPpe();
        ppeList.add(ppe);
        employee.setPpe(ppeList);

        return employeeRepository.save(employee);
    }
}

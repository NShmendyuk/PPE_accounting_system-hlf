package ru.inside.commands.service;

import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.EmployeeDto;
import ru.inside.commands.entity.dto.PPEDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto getEmployee(Long id) throws NoEntityException;
    Employee getEmployeeByPersonnelNumber(String personnelNumber) throws NoEntityException;
    List<Employee> getAllEmployee();
    Employee transferToSubsidiary(Long personnelNumber, Long subsidiaryId) throws NoEntityException;
    void dismissEmployee(String personnelNumber) throws NoEntityException;
    EmployeeDto addEmployee(EmployeeDto employeeDto) throws NoEntityException;
    Employee addEmployee(Employee employee) throws NoEntityException;
    Employee addPPEToEmployee(PPE ppe, String ownerPersonnelNumber) throws NoEntityException;
}

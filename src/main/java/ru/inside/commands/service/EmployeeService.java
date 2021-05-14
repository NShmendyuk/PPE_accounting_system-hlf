package ru.inside.commands.service;

import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto getEmployee(Long id) throws NoEntityException;
    Employee getEmployeeByPersonnelNumber(String personnelNumber) throws NoEntityException;
    List<Employee> getAllEmployee();
    EmployeeDto transferToSubsidiary(Long personnelNumber, Long subsidiaryId) throws NoEntityException;
    EmployeeDto dismissEmployee(Long id) throws NoEntityException;
    EmployeeDto addEmployee(EmployeeDto employeeDto) throws NoEntityException;
    Employee addEmployee(Employee employee) throws NoEntityException;
}

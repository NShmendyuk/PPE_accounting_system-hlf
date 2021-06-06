package ru.inside.commands.service;

import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;

import java.util.List;

public interface EmployeeService {
    Employee getEmployeeByPersonnelNumber(String personnelNumber) throws NoEntityException;
    List<Employee> getAllEmployee();
    void dismissEmployee(String personnelNumber) throws NoEntityException;
    Employee addEmployee(Employee employee, Subsidiary selfSubsidiary) throws NoEntityException;
    Employee addPPEToEmployee(PPE ppe, String ownerPersonnelNumber) throws NoEntityException;
}

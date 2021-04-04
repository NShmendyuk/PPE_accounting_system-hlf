package ru.inside.commands.service;

import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto getEmployee(Long id) throws NoEntityException;
    List<EmployeeDto> getAllEmployee();
    EmployeeDto transferToSubsidiary(Long employeeId, Long subsidiaryId) throws NoEntityException;
    EmployeeDto dismissEmployee(Long id) throws NoEntityException;
    EmployeeDto addEmployee(EmployeeDto employeeDto) throws NoEntityException;
}

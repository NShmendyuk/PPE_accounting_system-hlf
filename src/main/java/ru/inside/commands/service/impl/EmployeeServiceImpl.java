package ru.inside.commands.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.EmployeeDto;
import ru.inside.commands.repository.EmployeeRepository;
import ru.inside.commands.repository.PPERepository;
import ru.inside.commands.repository.SubsidiaryRepository;
import ru.inside.commands.service.EmployeeService;
import ru.inside.commands.service.helper.DtoConverter;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final SubsidiaryRepository subsidiaryRepository;

    public EmployeeDto getEmployee(Long id) throws NoEntityException {
        return DtoConverter.convertEmployeeToDto(employeeRepository.findById(id).orElseThrow(() ->
                NoEntityException.createWithId(Employee.class.getSimpleName().toLowerCase(), id)));
    }

    public Employee getEmployeeByPersonnelNumber(Long personnelNumber) throws NoEntityException {
        return employeeRepository.findByEmployeeID(personnelNumber).orElseThrow(() ->
                NoEntityException.createWithParam(Employee.class.getSimpleName().toLowerCase(), personnelNumber.toString()));
    }

    public List<EmployeeDto> getAllEmployee() {
        List<EmployeeDto> employeeDtos = new ArrayList<>();
        employeeRepository.findAll().forEach(employee -> {
            employeeDtos.add(DtoConverter.convertEmployeeToDto(employee));
        });
        return employeeDtos;
    }

    public EmployeeDto transferToSubsidiary(Long employeeId, Long subsidiaryId) throws NoEntityException {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                NoEntityException.createWithParam(Employee.class.getSimpleName().toLowerCase(), employeeId.toString()));
        Subsidiary subsidiary = subsidiaryRepository.findById(subsidiaryId).orElseThrow(() ->
                NoEntityException.createWithId(Subsidiary.class.getSimpleName().toLowerCase(), subsidiaryId));
        employee.setSubsidiary(subsidiary);
        return DtoConverter.convertEmployeeToDto(employeeRepository.save(employee));
    }

    public EmployeeDto dismissEmployee(Long id) throws NoEntityException {
        Employee employee = employeeRepository.findById(id).orElseThrow(() ->
                NoEntityException.createWithId(Employee.class.getSimpleName().toLowerCase(), id));
        employee.setSubsidiary(null);
        return DtoConverter.convertEmployeeToDto(employeeRepository.save(employee));
    }

    public EmployeeDto addEmployee(EmployeeDto employeeDto) throws NoEntityException {
        List<PPE> ppe = null;
        Subsidiary subsidiary = null;

        if (employeeDto.getSubsidiaryId() != null) {
            subsidiary = subsidiaryRepository.findById(employeeDto.getSubsidiaryId()).orElseThrow(() ->
                    NoEntityException.createWithId(Subsidiary.class.getSimpleName().toLowerCase(), employeeDto.getSubsidiaryId()));
        }

        return DtoConverter.convertEmployeeToDto(employeeRepository.save(DtoConverter.convertDtoToEmployee(employeeDto, ppe, subsidiary)));
    }
}

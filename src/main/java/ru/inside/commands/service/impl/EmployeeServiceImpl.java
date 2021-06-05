package ru.inside.commands.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.EmployeeDto;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.repository.EmployeeRepository;
import ru.inside.commands.repository.SubsidiaryRepository;
import ru.inside.commands.service.EmployeeService;
import ru.inside.commands.service.SubsidiaryService;
import ru.inside.commands.service.helper.DtoConverter;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final SubsidiaryRepository subsidiaryRepository;
    private final SubsidiaryService subsidiaryService;

    public EmployeeDto getEmployee(Long id) throws NoEntityException {
        return DtoConverter.convertEmployeeToDto(employeeRepository.findById(id).orElseThrow(() ->
                NoEntityException.createWithId(Employee.class.getSimpleName().toLowerCase(), id)));
    }

    public Employee getEmployeeByPersonnelNumber(String personnelNumber) throws NoEntityException {
        return employeeRepository.findByPersonnelNumber(personnelNumber).orElseThrow(() ->
                NoEntityException.createWithParam(Employee.class.getSimpleName().toLowerCase(), personnelNumber));
    }

    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    public Employee transferToSubsidiary(Long personnelNumber, Long subsidiaryId) throws NoEntityException {
        Employee employee = employeeRepository.findById(personnelNumber).orElseThrow(() ->
                NoEntityException.createWithParam(Employee.class.getSimpleName().toLowerCase(), personnelNumber.toString()));
        Subsidiary subsidiary = subsidiaryRepository.findById(subsidiaryId).orElseThrow(() ->
                NoEntityException.createWithId(Subsidiary.class.getSimpleName().toLowerCase(), subsidiaryId));
        employee.setSubsidiary(subsidiary);
        return employeeRepository.save(employee);
    }

    public void dismissEmployee(String personnelNumber) throws NoEntityException {
        Employee employee = employeeRepository.findByPersonnelNumber(personnelNumber).orElseThrow(() ->
                NoEntityException.createWithParam(Employee.class.getSimpleName().toLowerCase(), personnelNumber));
        employee.setSubsidiary(null);
        employeeRepository.delete(employee);
        log.info("employee {} were deleted from organization", personnelNumber);
    }

    public EmployeeDto addEmployee(EmployeeDto employeeDto) throws NoEntityException {
        List<PPE> ppe = null;
        Subsidiary subsidiary;

        if (employeeDto.getSubsidiaryId() != null) {
            subsidiary = subsidiaryRepository.findById(employeeDto.getSubsidiaryId()).orElseThrow(() ->
                    NoEntityException.createWithId(Subsidiary.class.getSimpleName().toLowerCase(), employeeDto.getSubsidiaryId()));
        } else {
            subsidiary = subsidiaryService.getSelfSubsidiary();
        }

        return DtoConverter.convertEmployeeToDto(employeeRepository.save(DtoConverter.convertDtoToEmployee(employeeDto, ppe, subsidiary)));
    }

    public Employee addEmployee(Employee employee) {
        Subsidiary subsidiary;
        if (employee.getSubsidiary() == null) {
            try {
                subsidiary = subsidiaryService.getSelfSubsidiary();
                employee.setSubsidiary(subsidiary);
            } catch (NoEntityException e) {
                log.warn("Self definition for subsidiary not found while add new employee {}", employee.getPersonnelNumber());
            }
        }
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

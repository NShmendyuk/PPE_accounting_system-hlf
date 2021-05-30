package ru.inside.commands.service.helper;

import lombok.experimental.UtilityClass;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.EmployeeDto;
import ru.inside.commands.entity.dto.PPEDto;
import ru.inside.commands.entity.dto.SubsidiaryDto;
import ru.inside.commands.entity.enums.PPEStatus;

import java.util.List;

@UtilityClass
public class DtoConverter {

    public SubsidiaryDto convertSubsidiaryToDto(Subsidiary subsidiary) {
        SubsidiaryDto subsidiaryDto = new SubsidiaryDto();
        subsidiaryDto.setId(subsidiary.getId());
        subsidiaryDto.setName(subsidiary.getName());
        subsidiaryDto.setPeerName(subsidiary.getPeerName());
        return subsidiaryDto;
    }

    public Subsidiary convertDtoToSubsidiary(SubsidiaryDto subsidiaryDto) {
        return Subsidiary.builder()
                .id(subsidiaryDto.getId())
                .name(subsidiaryDto.getName())
                .peerName(subsidiaryDto.getPeerName())
                .build();
    }

    public EmployeeDto convertEmployeeToDto(Employee employee) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setPersonnelNumber(employee.getPersonnelNumber());
        employeeDto.setEmployeeName(employee.getEmployeeName());
        employeeDto.setId(employee.getId());
        employeeDto.setOccupation(employee.getOccupation());
        employee.getPpe().forEach(ppe -> {
            employeeDto.getPpeId().add(ppe.getId());
        });
        employeeDto.setSubsidiaryId(employee.getSubsidiary().getId());

        return employeeDto;
    }

    public Employee convertDtoToEmployee(EmployeeDto employeeDto, List<PPE> ppe, Subsidiary subsidiary) {
        return Employee.builder().id(employeeDto.getId())
                .personnelNumber(employeeDto.getPersonnelNumber())
                .employeeName(employeeDto.getEmployeeName())
                .occupation(employeeDto.getOccupation())
                .ppe(ppe).subsidiary(subsidiary).build();
    }
}

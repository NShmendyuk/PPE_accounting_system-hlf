package ru.inside.commands.service.helper;

import lombok.experimental.UtilityClass;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.EmployeeDto;
import ru.inside.commands.entity.dto.PPEDto;
import ru.inside.commands.entity.dto.SubsidiaryDto;
import ru.inside.commands.entity.enums.PPEStatus;

@UtilityClass
public class DtoConverter {

    public SubsidiaryDto convertSubsidiaryToDto(Subsidiary subsidiary) {
        SubsidiaryDto subsidiaryDto = new SubsidiaryDto();
        subsidiaryDto.setId(subsidiary.getId());
        subsidiaryDto.setName(subsidiary.getName());
        return subsidiaryDto;
    }

    public Subsidiary convertDtoToSubsidiary(SubsidiaryDto subsidiaryDto) {
        return Subsidiary.builder().id(subsidiaryDto.getId()).name(subsidiaryDto.getName()).build();
    }

    public EmployeeDto convertEmployeeToDto(Employee employee) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmployeeID(employee.getEmployeeID());
        employeeDto.setEmployeeName(employee.getEmployeeName());
        employeeDto.setId(employee.getId());
        employeeDto.setOccupation(employee.getOccupation());
        employeeDto.setPpeId(employee.getPpe().getId());
        employeeDto.setSubsidiaryId(employee.getSubsidiary().getId());

        return employeeDto;
    }

    public Employee convertDtoToEmployee(EmployeeDto employeeDto, PPE ppe, Subsidiary subsidiary) {
        return Employee.builder().id(employeeDto.getId())
                .employeeID(employeeDto.getEmployeeID())
                .employeeName(employeeDto.getEmployeeName())
                .occupation(employeeDto.getOccupation())
                .ppe(ppe).subsidiary(subsidiary).build();
    }

    public PPEDto convertPPEToDto(PPE ppe) {
        PPEDto ppeDto = new PPEDto();
        ppeDto.setId(ppe.getId());
        ppeDto.setInventoryNumber(ppe.getInventoryNumber());
        ppeDto.setLifeTime(ppe.getLifeTime());
        ppeDto.setName(ppe.getName());
        ppeDto.setOwnerId(ppe.getEmployee().getId());
        ppeDto.setPpeStatus(ppe.getPpeStatus().toString());
        ppeDto.setPrice(ppe.getPrice());
        ppeDto.setStartUseDate(ppe.getStartUseDate());
        return ppeDto;
    }

    public PPE convertDtoToPPE(PPEDto ppeDto, Employee employee) {
        return PPE.builder().id(ppeDto.getId()).employee(employee).inventoryNumber(ppeDto.getInventoryNumber())
                .lifeTime(ppeDto.getLifeTime()).name(ppeDto.getName()).ppeStatus(PPEStatus.valueOf(ppeDto.getPpeStatus()))
                .price(ppeDto.getPrice()).startUseDate(ppeDto.getStartUseDate()).build();
    }
}

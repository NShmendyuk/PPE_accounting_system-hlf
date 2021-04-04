package ru.inside.commands.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.EmployeeDto;
import ru.inside.commands.entity.dto.SubsidiaryDto;
import ru.inside.commands.repository.EmployeeRepository;
import ru.inside.commands.repository.SubsidiaryRepository;
import ru.inside.commands.service.SubsidiaryService;
import ru.inside.commands.service.helper.DtoConverter;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubsidiaryServiceImpl implements SubsidiaryService {
    private final SubsidiaryRepository subsidiaryRepository;
    private final EmployeeRepository employeeRepository;

    public SubsidiaryDto get(Long id) throws NoEntityException {
        Subsidiary subsidiary = subsidiaryRepository.findById(id).orElseThrow(() ->
            NoEntityException.createWithId(Subsidiary.class.getSimpleName().toLowerCase(), id));
        return DtoConverter.convertSubsidiaryToDto(subsidiary);
    }

    public SubsidiaryDto add(SubsidiaryDto subsidiaryDto){
        for (Subsidiary subsidiary : subsidiaryRepository.findAll()) {
            if (subsidiary.getName().equals(subsidiaryDto.getName())) {
                return DtoConverter.convertSubsidiaryToDto(subsidiary);
            }
        }
        return DtoConverter.convertSubsidiaryToDto(subsidiaryRepository.save(DtoConverter.convertDtoToSubsidiary(subsidiaryDto)));
    }

    public List<SubsidiaryDto> getAll() {
        List<SubsidiaryDto> subsidiaryDtos = new ArrayList<>();
        subsidiaryRepository.findAll().forEach(subsidiary -> {
            subsidiaryDtos.add(DtoConverter.convertSubsidiaryToDto(subsidiary));
        });
        return subsidiaryDtos;
    }

    public List<EmployeeDto> getAllEmployeeFromSubsidiary(Long id) {
        List<EmployeeDto> employeeDtos = new ArrayList<>();
        List<Employee> employeeList = employeeRepository.findAllBySubsidiaryId(id);
        employeeList.forEach(employee -> {
            employeeDtos.add(DtoConverter.convertEmployeeToDto(employee));
        });
        return employeeDtos;
    }
}

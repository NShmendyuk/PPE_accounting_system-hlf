package ru.inside.commands.service;

import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.dto.EmployeeDto;
import ru.inside.commands.entity.dto.SubsidiaryDto;

import java.util.List;

public interface SubsidiaryService {
    SubsidiaryDto get(Long id) throws NoEntityException;
    Subsidiary getByName(String subsidiaryName) throws NoEntityException;
    SubsidiaryDto add(SubsidiaryDto subsidiaryDto);
    Subsidiary add(Subsidiary subsidiary);
    List<Subsidiary> getAll();
    Subsidiary getSelfSubsidiary() throws NoEntityException;
}

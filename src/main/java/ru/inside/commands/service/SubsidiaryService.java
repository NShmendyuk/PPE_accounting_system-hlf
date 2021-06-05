package ru.inside.commands.service;

import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Subsidiary;

import java.util.List;

public interface SubsidiaryService {
    Subsidiary getByName(String subsidiaryName) throws NoEntityException;
    Subsidiary add(Subsidiary subsidiary);
    List<Subsidiary> getAll();
    Subsidiary getSelfSubsidiary() throws NoEntityException;
}

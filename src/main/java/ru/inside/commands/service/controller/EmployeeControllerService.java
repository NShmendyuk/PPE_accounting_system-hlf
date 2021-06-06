package ru.inside.commands.service.controller;

import ru.inside.commands.controller.exception.BadRequestBodyException;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.forms.EmployeeForm;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.entity.forms.SubsidiaryForm;

import java.util.List;

public interface EmployeeControllerService {
    List<EmployeeForm> getAllEmployee();
    List<PPEForm> getAllPPEOfEmployee(String personnelNumber);
    EmployeeForm getEmployeeByPersonnelNumber(String personnelNumber);
    List<SubsidiaryForm> getAllSubsidiary();
    byte[] transferEmployeeToSubsidiary(String personnelNumber, String subsidiaryName) throws NoEntityException, BadRequestBodyException;
    void addEmployee(String name, String occupation, String personnelNumber);
}

package ru.inside.commands.service.controller;

import ru.inside.commands.entity.forms.EmployeeForm;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.entity.forms.SubsidiaryForm;

import java.util.List;

public interface EmployeeControllerService {
    List<EmployeeForm> getAllEmployee();
    List<PPEForm> getAllPPEOfEmployee(String personnelNumber);
    EmployeeForm getEmployeeByPersonnelNumber(String personnelNumber);
    List<SubsidiaryForm> getAllSubsidiary();
    void transferEmployeeToSubsidiary(String personnelNumber, String subsidiaryName);
    void addEmployee(String name, String occupation, String personnelNumber);
}

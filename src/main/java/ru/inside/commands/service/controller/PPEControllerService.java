package ru.inside.commands.service.controller;

import ru.inside.commands.entity.forms.PPEForm;

import java.time.LocalDate;
import java.util.List;

public interface PPEControllerService {
    PPEForm getPPEForm(String inventoryNumber);
    List<PPEForm> getAllPPEForms();
    void addPPEForm(String name, Float price, String inventoryNumber,
                    String ownerPersonnelNumber, LocalDate date, Long lifeTimeDays);
    List<PPEForm> getPPEHistory(String inventoryNumber);
    void decommissioning(String employeePersonnelNumber, String inventoryNumber);
    List<PPEForm> getAllInWaitList();
    byte[] applyPPEFromChainCode(String inventoryNumber);
    byte[] applyAllPPEFromChainCode();
}

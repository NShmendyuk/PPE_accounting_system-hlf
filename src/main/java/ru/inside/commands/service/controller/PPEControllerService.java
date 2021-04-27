package ru.inside.commands.service.controller;

import ru.inside.commands.entity.forms.PPEForm;

import java.time.LocalDate;

public interface PPEControllerService {
    PPEForm getPPEForm(Long id);
    void addPPEForm(String name, Float price, String inventoryNumber,
                    String ownerPersonnelNumber, LocalDate date, Long lifeTimeDays);
}

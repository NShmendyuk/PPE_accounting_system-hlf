package ru.inside.commands.service.controller;

import ru.inside.commands.entity.forms.SubsidiaryForm;

import java.util.List;

public interface SubsidiaryControllerService {
    List<SubsidiaryForm> getAllAnotherSubsidiary();
    void addSubsidiaryInfo(String name, String peerName);
}

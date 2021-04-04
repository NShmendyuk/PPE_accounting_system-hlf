package ru.inside.commands.service;

import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.dto.PPEDto;
import ru.inside.commands.entity.enums.PPEStatus;

public interface PPEService {
    PPEDto getPPE(Long id) throws NoEntityException;
    PPEDto updateStatus(Long id, PPEStatus status) throws NoEntityException;
    PPEDto addPPE(PPEDto ppeDto) throws NoEntityException;
    void updateAllStatus();
}

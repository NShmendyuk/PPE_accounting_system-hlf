package ru.inside.commands.service;

import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;

import java.util.List;

public interface ChainCodeWaitService {
    List<PPE> getAllWaitFromChainCode(Subsidiary selfSubsidiary);
}

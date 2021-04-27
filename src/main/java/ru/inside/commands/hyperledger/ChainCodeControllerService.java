package ru.inside.commands.hyperledger;

public interface ChainCodeControllerService {
    void initTestLedger();
    String getAllPPE();
    String getPPEById(Long id);
}

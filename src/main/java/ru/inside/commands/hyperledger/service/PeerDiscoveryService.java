package ru.inside.commands.hyperledger.service;

import java.util.Collection;

public interface PeerDiscoveryService {
    Collection<String> getMSPIDsInfo();
}

package ru.inside.commands.hyperledger;

import java.util.List;

public interface PeerDiscoveryService {
    List<String> getAllActivePeersName();
    List<String> getAllActiveMspId();
}

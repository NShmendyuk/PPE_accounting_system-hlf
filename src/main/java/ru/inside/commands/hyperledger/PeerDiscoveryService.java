package ru.inside.commands.hyperledger;

import java.util.Collection;

public interface PeerDiscoveryService {
    Collection<String> getMSPIDsInfo();
}

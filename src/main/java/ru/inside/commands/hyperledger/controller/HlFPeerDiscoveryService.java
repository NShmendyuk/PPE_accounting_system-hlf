package ru.inside.commands.hyperledger.controller;

import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.stereotype.Service;
import ru.inside.commands.hyperledger.PeerDiscoveryService;
import ru.inside.commands.hyperledger.fabric.configuration.HlfConfiguration;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class HlFPeerDiscoveryService implements PeerDiscoveryService {
    private Network network;

    private HlFPeerDiscoveryService(HlfConfiguration hlfConfiguration) {
        this.network = hlfConfiguration.getNetwork();
    }

    public Collection<String> getPeersInfo() {
        log.info("===begin info===");
        Collection<Peer> peers = network.getChannel().getPeers();
        Collection<String> orgMSPIds = new ArrayList<>();
        log.info("===peers info===");
        peers.forEach(peer -> {
            log.info(peer.toString());
        });
        log.info("===peers org msp ids===");
        log.info("{}", network.getChannel().getPeersOrganizationMSPIDs().toString());
        log.info("===orderers org msp ids===");
        log.info("{}", network.getChannel().getOrderersOrganizationMSPIDs().toString());
        log.info("===discovered chaincode names===");
        log.info("{}", network.getChannel().getDiscoveredChaincodeNames().toString());
        log.info("===end info===");
        return network.getChannel().getOrderersOrganizationMSPIDs();
    }
}

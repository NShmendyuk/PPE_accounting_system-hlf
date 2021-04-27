package ru.inside.commands.hyperledger.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.Channel;
import org.springframework.stereotype.Service;
import ru.inside.commands.hyperledger.PeerDiscoveryService;
import ru.inside.commands.hyperledger.fabric.chaincode.PPEChainCodeController;
import ru.inside.commands.hyperledger.fabric.channel.HlfChannel;
import ru.inside.commands.hyperledger.fabric.configuration.HlfConfiguration;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HlFPeerDiscoveryService implements PeerDiscoveryService {
    private HlfChannel currentChannel;

    private HlFPeerDiscoveryService(HlfConfiguration hlfConfiguration) {
        try {
            currentChannel = hlfConfiguration.getChannel();
        } catch (Exception ex) {
            log.error("Cannot find channel. Hyperledger environment unactive");
        }
    }

    //TODO: stubbed
    public List<String> getAllActivePeersName() {
        try {
            if (currentChannel != null && currentChannel.getChannel() != null) {
                List<String> peersName = new ArrayList<>();
                currentChannel.getChannel().getPeers().forEach(peer -> {
                    peersName.add(peer.getName());
                });

                return peersName;
            }
        } catch (Exception ex) {
            log.error("Cannot find peers name by hyperledger environment. Channel is not active.");
        }

        List<String> stubbedPeersName = new ArrayList<>();
        stubbedPeersName.add("peer0.gpn-gs1.gazprom.com");
        stubbedPeersName.add("peer0.gpn-supply.gazprom.com");
        return stubbedPeersName;
    }

    public List<String> getAllActiveMspId() {
        try {
            if (currentChannel != null && currentChannel.getChannel() != null) {
                List<String> mspIds = new ArrayList<>();
                currentChannel.getChannel().getPeersOrganizationMSPIDs().forEach(mspId -> {
                    mspIds.add(mspId);
                });

                return mspIds;
            }
        } catch (Exception ex) {
            log.error("Cannot find msp id by hyperledger environment. Channel is not active.");
        }

        List<String> stubbedPeersName = new ArrayList<>();
        stubbedPeersName.add("peer0.gpn-gs1.gazprom.com");
        stubbedPeersName.add("peer0.gpn-supply.gazprom.com");
        return stubbedPeersName;
    }
}

package ru.inside.commands.hyperledger.fabric.channel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

@Slf4j
public class HlfChannel {
    @Getter
    private Channel channel;

    private String HLF_DOCKER_INSTANCE_OF_HLFPEER_GRPC_LINK = "grpc://localhost:7051";
    private String HLF_DOCKER_INSTANCE_OF_HLFORDERER_GRPC_LINK = "grpc://localhost:7050";
    private String HLF_INSTANCE_PEER_NAME = "peer0.org1.example.com";
    private String HLF_INSTANCE_ORDERER_NAME = "orderer";
    private String HLF_INSTANCE_CHANNEL_NAME = "mychannel";

    /**
     *
     * @param client - user/admin HlFClient
     */
    public Channel initChannel(HFClient client) throws InvalidArgumentException, TransactionException {
        Peer peer = client.newPeer(HLF_INSTANCE_PEER_NAME, HLF_DOCKER_INSTANCE_OF_HLFPEER_GRPC_LINK);
        Orderer orderer = client.newOrderer(HLF_INSTANCE_ORDERER_NAME, HLF_DOCKER_INSTANCE_OF_HLFORDERER_GRPC_LINK);
        channel = client.newChannel(HLF_INSTANCE_CHANNEL_NAME);
        channel.addPeer(peer);
        channel.addOrderer(orderer);
        channel.initialize();
        return channel;
    }
}

package ru.inside.commands.hyperledger.fabric.configuration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.springframework.stereotype.Component;
import ru.inside.commands.hyperledger.fabric.ca.HlfUser;
import ru.inside.commands.hyperledger.fabric.ca.RegistCAClient;
import ru.inside.commands.hyperledger.fabric.ca.admin.CAAuthAdmin;
import ru.inside.commands.hyperledger.fabric.chaincode.PPEChainCodeController;
import ru.inside.commands.hyperledger.fabric.channel.HlfChannel;
import ru.inside.commands.hyperledger.fabric.client.HlfClient;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class HlfConfiguration {
    private RegistCAClient registCAClient;
    private CAAuthAdmin adminCA;
    private HlfClient hlfClient; // admin
    @Getter
    private HlfChannel channel;

    @Getter
    private PPEChainCodeController chainCodeController;

    private HFClient currentClient;
    private Channel currentChannel;

    public HlfConfiguration() {
        registCAClient = new RegistCAClient();
        adminCA = new CAAuthAdmin();
        hlfClient = new HlfClient();
        channel = new HlfChannel();
        chainCodeController = new PPEChainCodeController();
    }

    //@PostConstruct TODO:
    public PPEChainCodeController initializeHyperledgerFabricConfiguration() {
        HFCAClient caClient = registCAClient.initializeCryptoSuite();
        HlfUser adminUser;

        try {
            adminUser = adminCA.enrollAdmin(caClient);
        } catch (EnrollmentException | InvalidArgumentException e) {
            log.error("Required enroll admin were rejected");
            return null;
        }

        currentClient = hlfClient.initClient(adminUser);

        try {
            currentChannel = channel.initChannel(currentClient);
        } catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException | TransactionException e) {
            log.error("Cannot initialize channel!", e);
        }

        chainCodeController.initializeChainCodeActivity(hlfClient, currentChannel);
        return chainCodeController;
    }
}

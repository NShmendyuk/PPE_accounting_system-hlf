package ru.inside.commands.hyperledger.fabric.configuration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Component;
import ru.inside.commands.hyperledger.fabric.ca.RegistCAClient;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Component(value="HlfConfiguration")
@Slf4j
public class HlfConfiguration {

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    @Getter
    private Contract contract;

    @Getter
    private Network network;

    public HlfConfiguration () {
        try {
            RegistCAClient registCAClient = new RegistCAClient();
            registCAClient.initializeUsersCA(); // проинициализируем пользователя в Fabric CA
            initConnect(); // подключимся к сети
        } catch (Exception ex) {
            log.error("Cannot init connection to hyperledger instances");
        }
    }

    // helper function for getting connected to the gateway
    private static Gateway connect() throws Exception {
        // Load a file system based wallet for managing identities.
        Path walletPath = Paths.get("wallet");
        log.info("path to wallet: {}", walletPath.toAbsolutePath().toString());

        Wallet wallet = Wallets.newFileSystemWallet(walletPath);
        log.info("wallet found. list of identity: {}", wallet.list());

        // load a CCP
        Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations",
                "peerOrganizations", "org1.example.com", "connection-org1.yaml");

        Gateway.Builder builder = Gateway.createBuilder();
        log.info("Gateway builder created");

        builder.identity(wallet.get("appUser00006"));
        log.info("Set gateway identity as appUser00006");

        log.info("try to set network config by file {}; path:{}", networkConfigPath.getFileName(), networkConfigPath.toAbsolutePath().toString());
        builder.networkConfig(networkConfigPath.toAbsolutePath());
        log.info("Added gateway network config by path");

        builder.discovery(true);
        log.info("Gateway discovery set true");

        return builder.connect();
    }

    private void initConnect() {
        Gateway gateway = null;
        try {
            gateway = connect();
            log.info("Connected to hyperledger peer");
        } catch (Exception e) {
            log.error("Cannot init connection to gateway");
        }
        if (gateway != null) {
            network = gateway.getNetwork("mychannel");
        } else {
            log.error("Gateway to hlf peer not found!!!");
            return;
        }
        contract = network.getContract("ppesmart");
    }
}

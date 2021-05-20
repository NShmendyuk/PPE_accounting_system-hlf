package ru.inside.commands.hyperledger.fabric.configuration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Component;
import ru.inside.commands.hyperledger.fabric.ca.RegistCAClient;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component(value="HlfConfiguration")
@Slf4j
public class HlfConfiguration {
//    private String HLF_USER_NAME = "managerUser106";
//    private String HLF_PART_OF_PATH_RELATIVE_ORG = "org1.example.com";
//    private String HLF_PART_OF_PATH_RELATIVE_YAML = "connection-org1.yaml";
//    private String HLF_CHANNEL_NAME = "mychannel";
//    private String HLF_CHAINCODE_NAME = "ppesmart";

    @Getter
    private Contract contract;

    @Getter
    private Network network;

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

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
        log.info("wallet found. {}", wallet.list());
        // load a CCP
        Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations",
                "peerOrganizations", "org1.example.com", "connection-org1.yaml");

        Gateway.Builder builder = Gateway.createBuilder();
        log.info("Gateway builder created");
        builder.identity(wallet, "managerUser106").networkConfig(networkConfigPath).discovery(true);
        log.info("Gateway identity accepted");
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

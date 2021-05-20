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
//    private String HLF_USER_NAME = "managerUser109";
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
        log.info("wallet found. list of identity: {}", wallet.list());
        // load a CCP
        Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations",
                "peerOrganizations", "org1.example.com", "connection-org1.yaml");

        Gateway.Builder builder = Gateway.createBuilder();
        log.info("Gateway builder created");
        builder.identity(wallet.get("managerUser109"));
        log.info("Set gateway identity as managerUser109");
        builder.networkConfig(networkConfigPath);
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
            String exception = Arrays.toString(e.getStackTrace());
            try {
                File file = File.createTempFile("exception", ".txt", new File(System.getProperty("user.dir")));
                OutputStream os = new FileOutputStream(file);
                os.write(exception.getBytes());
                os.flush();
                os.close();
            } catch (IOException ignored) {
            }
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

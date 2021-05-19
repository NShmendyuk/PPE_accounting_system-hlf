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
    private Gateway gateway;
    private String HLF_USER_NAME = "appUser10";
    private String HLF_PART_OF_PATH_RELATIVE_ORG = "org1.example.com";
    private String HLF_PART_OF_PATH_RELATIVE_YAML = "connection-org1.yaml";
    private String HLF_CHANNEL_NAME = "mychannel";
    private String HLF_CHAINCODE_NAME = "ppesmart";

    @Getter
    private Contract contract;

    @Getter
    private Network network;

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    public HlfConfiguration () {
        try {
            RegistCAClient.initializeUsersCA(HLF_USER_NAME); // проинициализируем пользователя в Fabric CA
            initConnect(); // подключимся к сети
        } catch (Exception ex) {
            log.error("Cannot init connection to hyperledger instances");
        }
    }

    // helper function for getting connected to the gateway
    private static Gateway connect(String HLF_USER_NAME,
                                   String HLF_PART_OF_PATH_RELATIVE_ORG,
                                   String HLF_PART_OF_PATH_RELATIVE_YAML) throws Exception {
        // Load a file system based wallet for managing identities.
        Path walletPath = Paths.get("wallet");
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);
        // load a CCP
        log.info("connection via configuration: ../../test-network/organizations/peerOrganizations/"
                + HLF_PART_OF_PATH_RELATIVE_ORG + "/" + HLF_PART_OF_PATH_RELATIVE_YAML);
        Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations", "peerOrganizations",
                HLF_PART_OF_PATH_RELATIVE_ORG, HLF_PART_OF_PATH_RELATIVE_YAML);

        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, HLF_USER_NAME).networkConfig(networkConfigPath).discovery(true);
        return builder.connect();
    }

    private void initConnect() {
        try {
            gateway = connect(HLF_USER_NAME, HLF_PART_OF_PATH_RELATIVE_ORG, HLF_PART_OF_PATH_RELATIVE_YAML);
            network = gateway.getNetwork(HLF_CHANNEL_NAME);
            contract = network.getContract(HLF_CHAINCODE_NAME);
        } catch (Exception e) {
            log.error("Cannot init connection to gateway");
        }
    }
}

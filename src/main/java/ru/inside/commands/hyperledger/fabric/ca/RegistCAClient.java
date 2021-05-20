package ru.inside.commands.hyperledger.fabric.ca;

import lombok.extern.slf4j.Slf4j;
import ru.inside.commands.hyperledger.fabric.ca.admin.CAAuthAdmin;
import ru.inside.commands.hyperledger.fabric.ca.user.CAAuthUser;

@Slf4j
public class RegistCAClient {
    private final String HLF_PATH_ORG2_CONNECTION =
            "../../test-network/organizations/peerOrganizations/org2.example.com/ca/ca.org2.example.com-cert.pem";
    private final String HLF_PEER_GRPC_URL = "https://localhost:8054";
    private final String HLF_ADMIN_NAME = "admin";
    private final String HLF_ADMIN_PASS = "adminpw";
    private final String HLF_ORG_AFFILATION = "org2.department1";
    private final String HLF_ORG_MSP_ID = "Org2MSP";
    private final String HLF_HOST_PEER_IP = "localhost";

    public void initializeUsersCA(String HLF_USER_NAME) {
        // enrolls the admin and registers the user
        try {
            log.info("trying enroll users");
            CAAuthAdmin.enrollAdmin(HLF_PATH_ORG2_CONNECTION, HLF_PEER_GRPC_URL, HLF_ADMIN_NAME, HLF_ADMIN_PASS,
                    HLF_ORG_MSP_ID, HLF_HOST_PEER_IP);
            log.info("admin enrolled");
            CAAuthUser.enrollUser(HLF_PATH_ORG2_CONNECTION, HLF_PEER_GRPC_URL, HLF_USER_NAME, HLF_ADMIN_NAME,
                    HLF_ORG_AFFILATION, HLF_ORG_MSP_ID);
            log.info("user enrolled");
        } catch (Exception e) {
            log.error("Cannot enroll users", e);
        }
    }
}

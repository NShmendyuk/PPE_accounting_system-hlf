package ru.inside.commands.hyperledger.fabric.ca;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.inside.commands.hyperledger.fabric.ca.admin.CAAuthAdmin;
import ru.inside.commands.hyperledger.fabric.ca.user.CAAuthUser;

@Slf4j
@Component
@NoArgsConstructor
public class RegistCAClient {
    static String HLF_CA_URL = "https://localhost:8054";
    static String HLF_CA_PEM_PATH = "../../test-network/organizations/peerOrganizations/org2.example.com/ca/ca.org2.example.com-cert.pem";
    static String HLF_ADMIN_USER = "admin";
    static String HLF_ADMIN_PASS = "adminpw";
    static String HLF_ORG_MSP_DEFINITION = "Org2MSP";
    static String HLF_ORG_AFFILIATION = "org2.department1";

    public static void initializeUsersCA(String HLF_USER_NAME) {
        // enrolls the admin and registers the user
        try {
            CAAuthAdmin.enrollAdmin(HLF_CA_URL, HLF_CA_PEM_PATH,
                    HLF_ADMIN_USER, HLF_ADMIN_PASS,
                    HLF_ORG_MSP_DEFINITION);
            CAAuthUser.enrollUser(HLF_CA_URL, HLF_CA_PEM_PATH, HLF_USER_NAME,
                    HLF_ORG_MSP_DEFINITION, HLF_ADMIN_USER, HLF_ORG_AFFILIATION);
        } catch (Exception e) {
            log.error("Cannot enroll users");
        }
    }
}

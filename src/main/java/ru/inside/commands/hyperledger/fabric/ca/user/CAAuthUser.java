package ru.inside.commands.hyperledger.fabric.ca.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import ru.inside.commands.hyperledger.fabric.ca.HlfUser;
import ru.inside.commands.hyperledger.fabric.ca.RegistCAClient;


@Slf4j
public class CAAuthUser {
    @Getter
    private HlfUser commonUser;
    private RegistCAClient registCAClient;
    private RegistrationRequest registrationRequest;
    private Enrollment userEnrollment;

    private String userSecret;

    private String HLF_MANAGER_USER_NAME = "managerUser";
    private String ORG_NAME = "org1";
    private String ORG_MSP_ID = "Org1MSP";

    CAAuthUser(RegistCAClient registCAClient) {
        this.registCAClient = registCAClient;
    }

    public void enrollUser(HlfUser admin) throws Exception {
        registrationRequest = new RegistrationRequest(HLF_MANAGER_USER_NAME, ORG_NAME);
        userSecret = registCAClient.getCaClient().register(registrationRequest, admin);
        userEnrollment = registCAClient.getCaClient().enroll(HLF_MANAGER_USER_NAME, userSecret);
        commonUser = new HlfUser(HLF_MANAGER_USER_NAME, ORG_NAME,ORG_MSP_ID, userEnrollment);
    }
}

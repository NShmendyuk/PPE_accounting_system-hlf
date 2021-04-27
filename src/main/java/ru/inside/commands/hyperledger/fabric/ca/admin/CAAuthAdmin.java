package ru.inside.commands.hyperledger.fabric.ca.admin;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import ru.inside.commands.hyperledger.fabric.ca.HlfUser;

@Slf4j
public class CAAuthAdmin {
    @Getter
    private HlfUser admin;

    private String ADMIN_HLF_USER_NAME = "admin";
    private String ADMIN_HLF_USER_PASS = "adminpw";
    private String ORG_NAME = "org1";
    private String ORG_MSP_ID = "Org1MSP";

    public HlfUser enrollAdmin(HFCAClient caClient) throws EnrollmentException, InvalidArgumentException {
        Enrollment adminEnrollment = caClient.enroll(ADMIN_HLF_USER_NAME, ADMIN_HLF_USER_PASS);
        admin = new HlfUser(ADMIN_HLF_USER_NAME,ORG_NAME, ORG_MSP_ID, adminEnrollment);
        return admin;
    }
}

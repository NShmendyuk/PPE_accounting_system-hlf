package ru.inside.commands.hyperledger.fabric.ca.user;

import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;


@Slf4j
public class CAAuthUser {

    public static void enrollUser(String HLF_CA_URL, String HLF_CA_PEM_PATH,
                                  String HLF_USER_NAME, String HLF_ORG_MSP_DEFINITION,
                                  String HLF_ADMIN_USER, String HLF_ORG_AFFILIATION) throws Exception {
        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile",
                HLF_CA_PEM_PATH);
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance(HLF_CA_URL, props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        // Create a wallet for managing identities
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

        // Check to see if we've already enrolled the user.
        if (wallet.get(HLF_USER_NAME) != null) {
            System.out.println("An identity for the user \"" + HLF_USER_NAME + "\" already exists in the wallet");
            return;
        }

        X509Identity adminIdentity = (X509Identity)wallet.get(HLF_ADMIN_USER);
        if (adminIdentity == null) {
            System.out.println("\"" + HLF_ADMIN_USER + "\" needs to be enrolled and added to the wallet first");
            return;
        }
        User admin = new User() {

            @Override
            public String getName() {
                return HLF_ADMIN_USER;
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return HLF_ORG_AFFILIATION;
            }

            @Override
            public Enrollment getEnrollment() {
                return new Enrollment() {

                    @Override
                    public PrivateKey getKey() {
                        return adminIdentity.getPrivateKey();
                    }

                    @Override
                    public String getCert() {
                        return Identities.toPemString(adminIdentity.getCertificate());
                    }
                };
            }

            @Override
            public String getMspId() {
                return HLF_ORG_MSP_DEFINITION;
            }

        };

        // Register the user, enroll the user, and import the new identity into the wallet.
        RegistrationRequest registrationRequest = new RegistrationRequest(HLF_USER_NAME);
        registrationRequest.setAffiliation(HLF_ORG_AFFILIATION);
        registrationRequest.setEnrollmentID(HLF_USER_NAME);
        String enrollmentSecret = caClient.register(registrationRequest, admin);
        Enrollment enrollment = caClient.enroll(HLF_USER_NAME, enrollmentSecret);
        Identity user = Identities.newX509Identity(HLF_ORG_MSP_DEFINITION, enrollment);
        wallet.put(HLF_USER_NAME, user);
        System.out.println("Successfully enrolled user \"" + HLF_USER_NAME + "\" and imported it into the wallet");
    }
}

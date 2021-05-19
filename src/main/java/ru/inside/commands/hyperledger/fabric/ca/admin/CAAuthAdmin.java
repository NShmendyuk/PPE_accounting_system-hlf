package ru.inside.commands.hyperledger.fabric.ca.admin;

import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
public class CAAuthAdmin {

    public static void main(String[] args) throws Exception {
        String HLF_CA_URL = "https://localhost:7054";
        String HLF_CA_PEM_PATH = "../../test-network/organizations/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem";
        String HLF_ADMIN_USER = "admin";
        String HLF_ADMIN_PASS = "adminpw";
        String HLF_ORG_MSP_DEFINITION = "Org1MSP";

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

        // Check to see if we've already enrolled the admin user.
        if (wallet.get(HLF_ADMIN_USER) != null) {
            System.out.println("An identity for the admin user \"" + HLF_ADMIN_USER + "\" already exists in the wallet");
            return;
        }

        // Enroll the admin user, and import the new identity into the wallet.
        final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
        enrollmentRequestTLS.addHost("localhost");
        enrollmentRequestTLS.setProfile("tls");
        Enrollment enrollment = caClient.enroll(HLF_ADMIN_USER, HLF_ADMIN_PASS, enrollmentRequestTLS);
        Identity user = Identities.newX509Identity(HLF_ORG_MSP_DEFINITION, enrollment);
        wallet.put(HLF_ADMIN_USER, user);
        System.out.println("Successfully enrolled user \"" + HLF_ADMIN_USER + "\" and imported it into the wallet");
    }
}

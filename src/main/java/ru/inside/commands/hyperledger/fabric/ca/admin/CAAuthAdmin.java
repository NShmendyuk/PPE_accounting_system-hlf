package ru.inside.commands.hyperledger.fabric.ca.admin;

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

public class CAAuthAdmin {
    public static void enrollAdmin(String HLF_PATH_ORG_CONNECTION, String HLF_PEER_GRPC_URL,
                                   String HLF_ADMIN_NAME, String HLF_ADMIN_PASS,
                                   String HLF_ORG_MSP_ID, String HLF_HOST_PEER_IP) throws Exception {

        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile",
                HLF_PATH_ORG_CONNECTION);
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance(HLF_PEER_GRPC_URL, props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        // Create a wallet for managing identities
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

        // Check to see if we've already enrolled the admin user.
        if (wallet.get(HLF_ADMIN_NAME) != null) {
            System.out.println("An identity for the admin user \"" + HLF_ADMIN_NAME + "\" already exists in the wallet");
            return;
        }

        // Enroll the admin user, and import the new identity into the wallet.
        final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
        enrollmentRequestTLS.addHost(HLF_HOST_PEER_IP);
        enrollmentRequestTLS.setProfile("tls");
        Enrollment enrollment = caClient.enroll(HLF_ADMIN_NAME, HLF_ADMIN_PASS, enrollmentRequestTLS);
        Identity user = Identities.newX509Identity(HLF_ORG_MSP_ID, enrollment);
        wallet.put(HLF_ADMIN_NAME, user);
        System.out.println("Successfully enrolled user \"" + HLF_ADMIN_NAME + "\" and imported it into the wallet");
    }
}
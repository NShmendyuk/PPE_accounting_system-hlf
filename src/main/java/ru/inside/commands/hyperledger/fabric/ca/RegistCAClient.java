package ru.inside.commands.hyperledger.fabric.ca;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

@Slf4j
@NoArgsConstructor
public class RegistCAClient {
    @Getter
    private HFCAClient caClient;

    private CryptoSuite cryptoSuite;

    private String HLF_DOCKER_INSTANCE_OF_HlFCA_GRPC_LINK = "grpc://localhost:7054";

    public HFCAClient initializeCryptoSuite() {
        log.info("start connect to fabric CA instance");
        try {
            cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        } catch (IllegalAccessException |
                InstantiationException |
                ClassNotFoundException |
                CryptoException |
                InvalidArgumentException |
                NoSuchMethodException |
                InvocationTargetException e) {
            log.error("Cannot get crypto suite from Factory");
        }
        try {
            caClient = HFCAClient.createNewInstance(HLF_DOCKER_INSTANCE_OF_HlFCA_GRPC_LINK, null);
        } catch (MalformedURLException e) {
            log.error("No instance of Fabric CA");
        }
        caClient.setCryptoSuite(cryptoSuite);
        return caClient;
    }
}

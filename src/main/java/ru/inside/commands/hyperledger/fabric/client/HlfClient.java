package ru.inside.commands.hyperledger.fabric.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import ru.inside.commands.hyperledger.fabric.ca.HlfUser;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public class HlfClient {
    @Getter
    private HFClient client;

    private CryptoSuite cryptoSuite;

    /**
     *
     * @param hlfUser - admin or user Hlf Client
     */
    public HFClient initClient(HlfUser hlfUser) {
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
        client = HFClient.createNewInstance();
        try {
            client.setCryptoSuite(cryptoSuite);
        } catch (CryptoException | InvalidArgumentException e) {
            log.error("Creating HlF client with crypto suite were failed!");
        }
        client.setUserContext(hlfUser);
        return client;
    }

}

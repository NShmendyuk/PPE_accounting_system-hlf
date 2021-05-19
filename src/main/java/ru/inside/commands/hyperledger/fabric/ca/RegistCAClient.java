package ru.inside.commands.hyperledger.fabric.ca;

import lombok.extern.slf4j.Slf4j;
import ru.inside.commands.hyperledger.fabric.ca.admin.CAAuthAdmin;
import ru.inside.commands.hyperledger.fabric.ca.user.CAAuthUser;

@Slf4j
public class RegistCAClient {
    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    public void initializeUsersCA() {
        // enrolls the admin and registers the user
        try {
            log.info("trying enroll users");
            CAAuthAdmin.main(null);
            log.info("admin enrolled");
            CAAuthUser.main(null);
            log.info("user enrolled");
        } catch (Exception e) {
            log.error("Cannot enroll users", e);
        }
    }
}

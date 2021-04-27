package ru.inside.commands.hyperledger.fabric.ca;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@ToString
public class HlfUser implements User, Serializable {
    private static final long serializationId = 1L;

    private String name;
    private String affiliation;
    private Enrollment enrollment;
    private String mspId;
    private String account;
    private Set<String> roles;

    public HlfUser(String name, String affiliation, String mspId, Enrollment enrollment) {
        this.name = name;
        this.affiliation = affiliation;
        this.enrollment = enrollment;
        this.mspId = mspId;
    }
}

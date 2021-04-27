package ru.inside.commands.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubsidiaryDto implements Serializable {
    private static final long serialVersionUID = -1737977154481998189L;
    private Long id;
    private String name;
    private String peerName;
}

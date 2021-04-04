package ru.inside.commands.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class PPEDto implements Serializable {
    private static final long serialVersionUID = 408625807175925555L;
    private Long id;
    private Long ownerId;
    private String ppeStatus;
    private String name;
    private Float price;
    private String inventoryNumber;
    private LocalDateTime startUseDate;
    private Duration lifeTime;
}

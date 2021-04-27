package ru.inside.commands.entity.forms;

import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PPEForm {
    private String ppeName;
    private String ownerName;
    private String ownerPersonnelNumber;
    private String inventoryNumber;
    private LocalDateTime startUseDate;
    private Duration lifeTime;
    private String ppeStatus;
    private String subsidiaryName;
    private Float price;
}

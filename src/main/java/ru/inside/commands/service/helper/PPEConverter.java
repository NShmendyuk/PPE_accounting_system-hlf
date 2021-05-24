package ru.inside.commands.service.helper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.hyperledger.entity.PPEContract;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
@Slf4j
public class PPEConverter {

    public PPE ppeContractToPPE(PPEContract ppeContract) {
        PPE ppe = new PPE();
        try {
            ppe.setName(ppeContract.getName());
            ppe.setInventoryNumber(ppeContract.getInventoryNumber());
            ppe.setLifeTime(Duration.ofDays(ppeContract.getLifeTime()));
            ppe.setStartUseDate(LocalDateTime.parse(ppeContract.getStartUseDate(), DateTimeFormatter.ofPattern("yyyy-MM-DD'T'HH:mm")));
            ppe.setPpeStatus(PPEStatus.valueOf(ppeContract.getStatus()));
            ppe.setPrice(ppeContract.getPrice());
        } catch (Exception ex) {
            log.error("Cannot convert contract to ppe");
        }
        return ppe;
    }
}

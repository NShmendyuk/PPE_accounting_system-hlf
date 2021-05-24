package ru.inside.commands.service.helper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.hyperledger.entity.PPEContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            try {
                LocalDateTime dateTime = LocalDateTime.parse(ppeContract.getStartUseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                ppe.setStartUseDate(dateTime);
            } catch (Exception ex) {
                log.error("Cannot convert date time from contract ppe");
                ppe.setStartUseDate(LocalDateTime.now());
            }
            try {
                ppe.setPpeStatus(PPEStatus.valueOfName(ppeContract.getStatus()));
            } catch (Exception ex) {
                log.error("Cannot convert status from contract ppe");
                log.error("Status error: {}", ppeContract.getStatus());
                ppe.setPpeStatus(PPEStatus.COMMISSIONED);
            }
            try {
                ppe.setPrice(ppeContract.getPrice());
            } catch (Exception ex) {
                log.error("Cannot convert price from contract ppe");
                ppe.setPrice(0F);
            }
        } catch (Exception ex) {
            log.error("Cannot convert contract to ppe");
        }
        return ppe;
    }
}

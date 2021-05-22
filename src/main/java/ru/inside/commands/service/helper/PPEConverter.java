package ru.inside.commands.service.helper;

import lombok.experimental.UtilityClass;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.hyperledger.entity.PPEContract;

import java.time.Duration;
import java.time.LocalDateTime;

@UtilityClass
public class PPEConverter {

    public PPE ppeContractToPPE(PPEContract ppeContract) {
        PPE ppe = new PPE();
        ppe.setName(ppeContract.getName());
        ppe.setInventoryNumber(ppeContract.getInventoryNumber());
        ppe.setLifeTime(Duration.ofDays(ppeContract.getLifeTime()));
        ppe.setStartUseDate(LocalDateTime.parse(ppeContract.getStartUseDate()));
        ppe.setPpeStatus(PPEStatus.valueOf(ppeContract.getStatus()));
        ppe.setPrice(ppeContract.getPrice());
        return ppe;
    }
}

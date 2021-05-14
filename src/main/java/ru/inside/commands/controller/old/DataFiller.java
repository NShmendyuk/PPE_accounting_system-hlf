package ru.inside.commands.controller.old;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.Subsidiary;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.service.EmployeeService;
import ru.inside.commands.service.SubsidiaryService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/old/datagen")
@RequiredArgsConstructor
public class DataFiller {
    private final EmployeeService employeeService;
    private final SubsidiaryService subsidiaryService;
    @GetMapping("")
    public void generateDataset() throws NoEntityException {
        Subsidiary subsidiary = new Subsidiary();
        Employee employee = new Employee();
        PPE ppe = new PPE();
        subsidiary.setName("gpn-Org1");
        subsidiary.setPeerName("Org1");
        ppe.setPpeStatus(PPEStatus.COMMISSIONED);
        ppe.setInventoryNumber("0000001");
        ppe.setLifeTime(Duration.ofDays(3L));
        ppe.setName("Спецодежда");
        ppe.setPrice(100F);
        ppe.setStartUseDate(LocalDateTime.now());
        employee.setEmployeeName("Иванов Иван Иванович");
        employee.setOccupation("Фрезировщик");
        employee.setPersonnelNumber("100000000");
        employee.setSubsidiary(subsidiary);
        employee.setPpe(List.of(ppe));
        employeeService.addEmployee(employee);

        subsidiary.setName("gpn-Org2");
        subsidiary.setPeerName("Org2");
        subsidiaryService.add(subsidiary);
    }
}

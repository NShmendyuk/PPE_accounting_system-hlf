package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.dto.EmployeeDto;
import ru.inside.commands.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public List<EmployeeDto> getAllEmployee() {
        return employeeService.getAllEmployee();
    }

    @GetMapping("/{id}")
    public void getEmployee(@PathVariable Long id) throws NoEntityException {
        employeeService.getEmployee(id);
    }

    @PatchMapping("/transfer/{employeeId}/{subsidiaryId}")
    public void transferEmployeeToSubsidiary(@PathVariable Long employeeId, @PathVariable Long subsidiaryId) throws NoEntityException {
        employeeService.transferToSubsidiary(employeeId, subsidiaryId);
    }

    @PostMapping("/transfer/recruit")
    public EmployeeDto addEmployee(@RequestBody EmployeeDto employeeDto) throws NoEntityException {
        return employeeService.addEmployee(employeeDto);
    }

    @PatchMapping("/transfer/{employeeId}/dismiss")
    public void dismissEmployee(@PathVariable Long employeeId) throws NoEntityException {
        employeeService.dismissEmployee(employeeId);
    }
}

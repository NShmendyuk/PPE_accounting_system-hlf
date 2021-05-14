package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.inside.commands.entity.forms.EmployeeForm;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.entity.forms.SubsidiaryForm;
import ru.inside.commands.service.controller.EmployeeControllerService;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Slf4j
public class EmployeePageController {
    private final EmployeeControllerService employeeControllerService;

    @GetMapping("")
    public ModelAndView getAllEmployee() {
        ModelAndView modelAndView = new ModelAndView("employeePage");
        List<EmployeeForm> employeeFormList = employeeControllerService.getAllEmployee();

        modelAndView.addObject("employees", employeeFormList);

        return modelAndView;
    }

    @GetMapping("/ppe")
    public ModelAndView getAllEmployeePPE(@RequestParam String personnelNumber) {
        log.info("get all employee's ({}) PPE page", personnelNumber);
        ModelAndView modelAndView = new ModelAndView("employeePPEsPage");
        List<PPEForm> employeePPEsForm = employeeControllerService.getAllPPEOfEmployee(personnelNumber);
        EmployeeForm employeeForm = employeeControllerService.getEmployeeByPersonnelNumber(personnelNumber);

        modelAndView.addObject("ppeList", employeePPEsForm);
        modelAndView.addObject("employee", employeeForm);
        return modelAndView;
    }

    @GetMapping("/transfer_init")
    public ModelAndView getTransferEmployeePage(@RequestParam String personnelNumber) {
        log.info("transfer page request. employee with personnelNumber: \"{}\"", personnelNumber);
        ModelAndView modelAndView = new ModelAndView("transferEmployeePage");
        EmployeeForm employeeForm = employeeControllerService.getEmployeeByPersonnelNumber(personnelNumber);
        List<SubsidiaryForm> subsidiaryFormList = employeeControllerService.getAllSubsidiary();
        log.info("transfer page request. show select options with: {}", subsidiaryFormList);
        modelAndView.addObject("employee", employeeForm);
        modelAndView.addObject("subsidiaryList", subsidiaryFormList);
        return modelAndView;
    }

    @PostMapping("/add")
    public void addEmployee(@RequestParam String name, @RequestParam String occupation, @RequestParam String personnelNumber) {
        log.info("POST request, add new employee with name: {}, occupation: {}, personnelNumber: {}", name, occupation, personnelNumber);
        employeeControllerService.addEmployee(name, occupation, personnelNumber);
    }

    @PostMapping("/transfer")
    public ModelAndView transferEmployeeToSubsidiaryWithPPEs(@RequestParam String personnelNumber, @RequestParam String name) {
//        log.info("{}", subsidiary);
        log.info("transfer employee with personnelNumber ({}) to another subsidiary ({})", personnelNumber, name);
//        employeeControllerService.transferEmployeeToSubsidiary(personnelNumber, subsidaryName);
        return new ModelAndView("redirect:/employee");
    }
}

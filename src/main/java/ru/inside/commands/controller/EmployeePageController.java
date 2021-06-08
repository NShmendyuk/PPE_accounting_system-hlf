package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.inside.commands.controller.exception.BadRequestBodyException;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.forms.EmployeeForm;
import ru.inside.commands.entity.forms.PPEForm;
import ru.inside.commands.entity.forms.SubsidiaryForm;
import ru.inside.commands.service.controller.EmployeeControllerService;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Slf4j
public class EmployeePageController {
    private final EmployeeControllerService employeeControllerService;

    @GetMapping("")
    public ModelAndView getAllEmployeePage() {
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

    @GetMapping("/transfer")
    public ModelAndView getTransferEmployeePage(@RequestParam String personnelNumber) {
        log.info("transfer page request. employee with personnelNumber: \"{}\"", personnelNumber);

        ModelAndView modelAndView = new ModelAndView("transferEmployeePage");

        EmployeeForm employeeForm = employeeControllerService.getEmployeeByPersonnelNumber(personnelNumber);
        List<SubsidiaryForm> subsidiaryFormList = employeeControllerService.getAllOtherSubsidiary();

        log.info("transfer page request. show select options with: {}", subsidiaryFormList);

        modelAndView.addObject("employee", employeeForm);
        modelAndView.addObject("subsidiaryList", subsidiaryFormList);
        return modelAndView;
    }

    @PostMapping("/add")
    public ModelAndView addEmployee(@RequestParam String name, @RequestParam String occupation,
                            @RequestParam String personnelNumber) {
        log.info("POST request, add new employee with name: {}, occupation: {}, personnelNumber: {}", name, occupation, personnelNumber);
        employeeControllerService.addEmployee(name, occupation, personnelNumber);

        return new ModelAndView("redirect:/employee");
    }

    @PostMapping("/transfer")
    public ResponseEntity<byte[]> transferEmployeeToSubsidiaryWithPPEs(
            @RequestParam String personnelNumber, @RequestParam String name) throws BadRequestBodyException, NoEntityException {
        log.info("transfer employee with personnelNumber ({}) to another subsidiary ({})", personnelNumber, name);
        byte[] contents = employeeControllerService.transferEmployeeToSubsidiary(personnelNumber, name);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        String filename = "transfer employee (" + personnelNumber + ") to (" + name + ") generated" + ".pdf";
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(filename, StandardCharsets.UTF_8)
                .build();
        headers.setContentDisposition(contentDisposition);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }
}

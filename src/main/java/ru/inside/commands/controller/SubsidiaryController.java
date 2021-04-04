package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.dto.EmployeeDto;
import ru.inside.commands.entity.dto.SubsidiaryDto;
import ru.inside.commands.service.SubsidiaryService;

import java.util.List;

@RestController
@RequestMapping("/subsidiary")
@RequiredArgsConstructor
public class SubsidiaryController {
    private final SubsidiaryService subsidiaryService;

    @GetMapping
    public List<SubsidiaryDto> getAllSubsidiary() {
        return subsidiaryService.getAll();
    }

    @GetMapping("/{subsidiaryId}")
    public SubsidiaryDto getSubsidiary(@PathVariable Long subsidiaryId) throws NoEntityException {
        return subsidiaryService.get(subsidiaryId);
    }

    @GetMapping("/{subsidiaryId}/employee")
    public List<EmployeeDto> getAllEmployeeFromSubsidiary(@PathVariable Long subsidiaryId) {
        return subsidiaryService.getAllEmployeeFromSubsidiary(subsidiaryId);
    }

    @PostMapping("/append")
    public SubsidiaryDto registrySubsidiary(@RequestBody SubsidiaryDto subsidiary) {
        return subsidiaryService.add(subsidiary);
    }

}

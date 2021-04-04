package ru.inside.commands.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.inside.commands.controller.exception.NoEntityException;
import ru.inside.commands.entity.dto.PPEDto;
import ru.inside.commands.entity.enums.PPEStatus;
import ru.inside.commands.service.PPEService;

@RestController
@RequestMapping("/ppe")
@RequiredArgsConstructor
public class PPEController {
    private final PPEService ppeService;

    @GetMapping("/{ppeId}")
    public PPEDto getPPE(@PathVariable Long ppeId) throws NoEntityException {
        return ppeService.getPPE(ppeId);
    }

    @PatchMapping("/{ppeId}/{ppeStatus}")
    public PPEDto changePPEStatus(@PathVariable Long ppeId, @PathVariable PPEStatus ppeStatus) throws NoEntityException {
        return ppeService.updateStatus(ppeId, ppeStatus);
    }

    @PostMapping("/add")
    private PPEDto addPPE(@RequestBody PPEDto ppeDto) throws NoEntityException {
        return ppeService.addPPE(ppeDto);
    }
}

package ru.inside.commands.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PPEStatusScheduler {
    private final PPEService ppeService;

//    @Scheduled(cron = "${ppe_lifecycle_accounting_system.task.scheduling.rate.cron}", zone = "EAT")
    @Scheduled(fixedDelay = 60000) //TODO: DEV;
    public void onScheduleUpdatePPEStatus() {
        log.info("SCHEDULER! start process updating status.");
        ppeService.updateAllStatus();
    }
}

package ru.inside.commands.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.enums.PPEStatus;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Component
@RequiredArgsConstructor
@Slf4j
public class PPEStatusScheduler {
    private final PPEService ppeService;

    private int maxThreadCount = 3;

    private ExecutorService ppeLifecycleStatusThreadPool;
    private final ThreadFactory ppeLifecycleStatusThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("ppeLifeTimer-%d")
            .build();

    private ExecutorService initPool(int maxThreadCount, ThreadFactory threadFactory) {
        return Executors.newFixedThreadPool(maxThreadCount, threadFactory);
    }

    private void renewPools() {
        if (ppeLifecycleStatusThreadPool.isShutdown()) {
            ppeLifecycleStatusThreadPool = initPool(maxThreadCount, ppeLifecycleStatusThreadFactory);
        }
    }

    @PostConstruct
    private void initPools() {
        ppeLifecycleStatusThreadPool = initPool(maxThreadCount, ppeLifecycleStatusThreadFactory);
    }

//    @Scheduled(cron = "${ppe_lifecycle_accounting_system.task.scheduling.rate.cron}", zone = "EAT")
    @Scheduled(fixedDelay = 60000) //DEV;
    public void onScheduleUpdatePPEStatus() {
        log.info("SCHEDULER! start process updating status.");
        renewPools();
        updateAllStatus();
    }

    /**
     * function for decommissing PPE, which have used up their service life
     */
    private void updateAllStatus() {
        List<PPE> ppeList = ppeService.getAllPPE();
        try {
            ppeList.parallelStream().filter(ppe -> ppe.getEmployee() != null)
                    .forEach(ppe -> {
                        ppeLifecycleStatusThreadPool.execute(new PPELifecycleStatusUpdater(ppe));
                    });
        } finally {
            ppeLifecycleStatusThreadPool.shutdown();
        }
    }

    private class PPELifecycleStatusUpdater implements Runnable {
        private PPE ppe;

        PPELifecycleStatusUpdater (PPE ppe) {
            this.ppe = ppe;
        }

        @Override
        public void run() {
            LocalDateTime startUseDate = ppe.getStartUseDate();
            if (startUseDate == null) {
                startUseDate = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
                ppe.setStartUseDate(startUseDate);
            }
            LocalDateTime nowTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
            Duration lifeTimeSpent = Duration.between(startUseDate, nowTime);
            Duration lifeTimeDuration = ppe.getLifeTime();

            log.info("ppe {}: days for use: {}", ppe.getInventoryNumber(), lifeTimeDuration.minus(lifeTimeSpent).toDays());

            if (ppe.getPpeStatus() != PPEStatus.DECOMMISSIONED
                    && ppe.getPpeStatus() != PPEStatus.SPOILED
                    && lifeTimeDuration.minus(lifeTimeSpent).toDays() < 0) {
                ppe.setPpeStatus(PPEStatus.SPOILED);
                log.info("ppe {} were spoiled by time! Replacement required!", ppe.getInventoryNumber());
                ppeService.addPPE(ppe);
            }
        }
    }
}

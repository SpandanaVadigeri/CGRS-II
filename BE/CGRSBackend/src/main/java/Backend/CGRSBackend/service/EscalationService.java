package Backend.CGRSBackend.service;

import Backend.CGRSBackend.entity.Grievance;
import Backend.CGRSBackend.entity.GrievanceStatus;
import Backend.CGRSBackend.entity.StatusUpdate;
import Backend.CGRSBackend.repository.GrievanceRepository;
import Backend.CGRSBackend.repository.StatusUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Phase 3 — Escalation System.
 *
 * Runs on a scheduled basis and automatically escalates grievances that
 * remain unresolved (PENDING or IN_PROGRESS) past a configurable threshold.
 *
 * Configuration:
 *   cgrs.escalation.threshold-days=7   (default: 7 days)
 *   cgrs.escalation.cron=0 0 2 * * *   (default: every day at 02:00 AM)
 *
 * Both values can be overridden in application.properties without code changes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EscalationService {

    private final GrievanceRepository grievanceRepository;
    private final StatusUpdateRepository statusUpdateRepository;
    private final NotificationService notificationService;

    /**
     * Number of days after which an unresolved grievance is escalated.
     * Defaults to 7 if not set in application.properties.
     */
    @Value("${cgrs.escalation.threshold-days:7}")
    private int thresholdDays;

    /**
     * Scheduled escalation job.
     *
     * Cron default: "0 0 2 * * *" → runs every day at 02:00 AM server time.
     * Override via cgrs.escalation.cron in application.properties.
     *
     * Steps:
     *  1. Compute cutoff = now - thresholdDays
     *  2. Find all PENDING/IN_PROGRESS grievances created before cutoff
     *  3. Set their status to ESCALATED
     *  4. Append a StatusUpdate timeline entry
     *  5. Fire notification to the citizen
     */
    @Scheduled(cron = "${cgrs.escalation.cron:0 0 2 * * *}")
    @Transactional
    public void checkAndEscalate() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(thresholdDays);

        List<GrievanceStatus> openStatuses = List.of(
                GrievanceStatus.PENDING,
                GrievanceStatus.IN_PROGRESS
        );

        List<Grievance> staleGrievances =
                grievanceRepository.findOpenGrievancesOlderThan(openStatuses, cutoff);

        if (staleGrievances.isEmpty()) {
            log.info("[ESCALATION] No grievances to escalate at this run.");
            return;
        }

        log.warn("[ESCALATION] Found {} grievance(s) to escalate (threshold: {} days).",
                staleGrievances.size(), thresholdDays);

        for (Grievance grievance : staleGrievances) {
            long daysOpen = ChronoUnit.DAYS.between(grievance.getCreatedAt(), LocalDateTime.now());

            // ── 1. Update status ─────────────────────────────────────────────
            grievance.setStatus(GrievanceStatus.ESCALATED);
            grievanceRepository.save(grievance);

            // ── 2. Append timeline entry ─────────────────────────────────────
            String remark = String.format(
                    "Auto-escalated after %d days without resolution (threshold: %d days).",
                    daysOpen, thresholdDays);

            statusUpdateRepository.save(StatusUpdate.builder()
                    .grievance(grievance)
                    .status(GrievanceStatus.ESCALATED)
                    .remark(remark)
                    .build());

            // ── 3. Notify citizen ────────────────────────────────────────────
            notificationService.notifyEscalated(grievance, daysOpen);

            log.warn("[ESCALATION] Grievance #{} escalated after {} days (citizen: {}).",
                    grievance.getId(), daysOpen,
                    grievance.getCitizen().getUser().getEmail());
        }

        log.info("[ESCALATION] Run complete. {} grievance(s) escalated.", staleGrievances.size());
    }
}

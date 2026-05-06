package Backend.CGRSBackend.service;

import Backend.CGRSBackend.entity.Grievance;
import Backend.CGRSBackend.entity.GrievanceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Phase 3 — Notification System (structural implementation).
 *
 * This service acts as the notification hub for the CGRS system.
 * Currently it logs to the console; the methods are intentionally designed
 * with the same signatures that a real FCM / email integration would use,
 * so swapping in a real provider later requires only adding the call inside
 * each method — no callers need to change.
 *
 * Integration points wired in:
 *   • GrievanceService.submitGrievance()   → notifyGrievanceSubmitted()
 *   • GrievanceService.updateGrievance()   → notifyStatusChanged()
 *   • EscalationService.checkAndEscalate() → notifyEscalated()
 */
@Slf4j
@Service
public class NotificationService {

    /**
     * Fires when a citizen successfully submits a new grievance.
     *
     * @param grievance the newly created grievance
     */
    public void notifyGrievanceSubmitted(Grievance grievance) {
        String citizenEmail = grievance.getCitizen().getUser().getEmail();
        log.info("[NOTIFICATION] Grievance #{} submitted by {}. Status: {}.",
                grievance.getId(), citizenEmail, grievance.getStatus());

        // TODO: send FCM push / email to citizen confirming submission
        notifyUser(citizenEmail,
                "Grievance Submitted",
                String.format("Your grievance '%s' (ID: %d) has been received and is under review.",
                        grievance.getTitle(), grievance.getId()));
    }

    /**
     * Fires when the status of a grievance changes (ADMIN / AUTHORITY update).
     *
     * @param grievance  updated grievance entity
     * @param newStatus  the new status that was applied
     * @param remark     optional remark from the officer
     */
    public void notifyStatusChanged(Grievance grievance, GrievanceStatus newStatus, String remark) {
        String citizenEmail = grievance.getCitizen().getUser().getEmail();
        log.info("[NOTIFICATION] Grievance #{} status changed to {} for citizen {}. Remark: {}",
                grievance.getId(), newStatus, citizenEmail, remark);

        notifyUser(citizenEmail,
                "Grievance Status Updated",
                String.format("Grievance '%s' (ID: %d) is now %s. Remark: %s",
                        grievance.getTitle(), grievance.getId(), newStatus,
                        remark != null ? remark : "N/A"));

        // Also notify the assigned authority (if any)
        if (grievance.getAuthority() != null) {
            String authorityEmail = grievance.getAuthority().getUser().getEmail();
            notifyAuthority(authorityEmail,
                    "Grievance Updated",
                    String.format("Grievance #%d ('%s') is now %s.",
                            grievance.getId(), grievance.getTitle(), newStatus));
        }
    }

    /**
     * Fires when the EscalationService auto-escalates an unresolved grievance.
     *
     * @param grievance the grievance being escalated
     * @param daysOpen  how many days it has been open
     */
    public void notifyEscalated(Grievance grievance, long daysOpen) {
        String citizenEmail = grievance.getCitizen().getUser().getEmail();
        log.warn("[NOTIFICATION][ESCALATION] Grievance #{} for {} has been ESCALATED after {} days unresolved.",
                grievance.getId(), citizenEmail, daysOpen);

        notifyUser(citizenEmail,
                "Grievance Escalated",
                String.format("Your grievance '%s' (ID: %d) has been escalated after %d days " +
                        "without resolution. A senior officer has been notified.",
                        grievance.getTitle(), grievance.getId(), daysOpen));

        // TODO: also notify department head / admin via FCM or email
    }

    // ── Internal dispatch helpers ───────────────────────────────────────────

    /**
     * Send a notification to a citizen.
     * Currently logs; replace body with FCM / email SDK call.
     */
    public void notifyUser(String recipientEmail, String subject, String body) {
        log.info("[NOTIFY → USER] To: {} | Subject: {} | Body: {}", recipientEmail, subject, body);
        // e.g. emailService.send(recipientEmail, subject, body);
        // e.g. fcmService.sendToUser(userId, subject, body);
    }

    /**
     * Send a notification to an authority officer.
     * Currently logs; replace body with FCM / email SDK call.
     */
    public void notifyAuthority(String authorityEmail, String subject, String body) {
        log.info("[NOTIFY → AUTHORITY] To: {} | Subject: {} | Body: {}", authorityEmail, subject, body);
        // e.g. emailService.send(authorityEmail, subject, body);
    }
}

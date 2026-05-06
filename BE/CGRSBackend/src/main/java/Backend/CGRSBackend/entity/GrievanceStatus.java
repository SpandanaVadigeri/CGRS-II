package Backend.CGRSBackend.entity;

/**
 * Lifecycle states of a Grievance.
 * ESCALATED is auto-set by EscalationService when unresolved past the threshold.
 */
public enum GrievanceStatus {
    PENDING,
    IN_PROGRESS,
    RESOLVED,
    ESCALATED
}

package me.clementino.holiday.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Sealed interface representing holiday status. Following DOP v1.1 Principle 3: Make Illegal States
 * Unrepresentable.
 *
 * <p>A holiday can only be in one of these states - no other combinations are possible. This
 * prevents bugs by making invalid states impossible to represent.
 */
public sealed interface HolidayStatus
    permits HolidayStatus.Active, HolidayStatus.Cancelled, HolidayStatus.Proposed {

  /** Holiday is active and confirmed. */
  record Active(LocalDateTime confirmedAt) implements HolidayStatus {
    public Active {
      Objects.requireNonNull(confirmedAt, "Confirmation date cannot be null");
    }

    public static Active now() {
      return new Active(LocalDateTime.now());
    }
  }

  /** Holiday has been cancelled. */
  record Cancelled(LocalDateTime cancelledAt, String reason) implements HolidayStatus {
    public Cancelled {
      Objects.requireNonNull(cancelledAt, "Cancellation date cannot be null");
      Objects.requireNonNull(reason, "Cancellation reason cannot be null");
      if (reason.isBlank()) {
        throw new IllegalArgumentException("Cancellation reason cannot be blank");
      }
    }

    public static Cancelled now(String reason) {
      return new Cancelled(LocalDateTime.now(), reason);
    }
  }

  /** Holiday is proposed but not yet confirmed. */
  record Proposed(LocalDateTime proposedAt, String proposedBy) implements HolidayStatus {
    public Proposed {
      Objects.requireNonNull(proposedAt, "Proposal date cannot be null");
      Objects.requireNonNull(proposedBy, "Proposer cannot be null");
      if (proposedBy.isBlank()) {
        throw new IllegalArgumentException("Proposer cannot be blank");
      }
    }

    public static Proposed now(String proposedBy) {
      return new Proposed(LocalDateTime.now(), proposedBy);
    }
  }

  // Query methods using pattern matching

  default boolean isActive() {
    return this instanceof Active;
  }

  default boolean isCancelled() {
    return this instanceof Cancelled;
  }

  default boolean isProposed() {
    return this instanceof Proposed;
  }

  default LocalDateTime getTimestamp() {
    return switch (this) {
      case Active(var confirmedAt) -> confirmedAt;
      case Cancelled(var cancelledAt, var reason) -> cancelledAt;
      case Proposed(var proposedAt, var proposedBy) -> proposedAt;
    };
  }
}

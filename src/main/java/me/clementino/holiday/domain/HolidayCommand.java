package me.clementino.holiday.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Sealed interface representing holiday commands.
 * Following DOP v1.1 Principle 3: Make Illegal States Unrepresentable.
 * 
 * Commands are the only way to modify holiday data.
 * Each command represents a specific operation with its required data.
 */
public sealed interface HolidayCommand 
    permits HolidayCommand.Create, HolidayCommand.Update, HolidayCommand.Delete, 
            HolidayCommand.Cancel, HolidayCommand.Activate {
    
    /**
     * Command to create a new holiday.
     */
    record Create(
        String name,
        LocalDate date,
        Location location,
        HolidayType type,
        boolean recurring,
        Optional<String> description,
        Optional<LocalDate> observed
    ) implements HolidayCommand {
        public Create {
            Objects.requireNonNull(name, "Name cannot be null");
            if (name.isBlank()) {
                throw new IllegalArgumentException("Name cannot be blank");
            }
            Objects.requireNonNull(date, "Date cannot be null");
            Objects.requireNonNull(location, "Location cannot be null");
            Objects.requireNonNull(type, "Type cannot be null");
            
            description = Objects.requireNonNullElse(description, Optional.empty());
            observed = Objects.requireNonNullElse(observed, Optional.empty());
        }
        
        public Create(String name, LocalDate date, Location location, HolidayType type, boolean recurring) {
            this(name, date, location, type, recurring, Optional.empty(), Optional.empty());
        }
    }
    
    /**
     * Command to update an existing holiday.
     */
    record Update(
        String id,
        Optional<String> name,
        Optional<LocalDate> date,
        Optional<Location> location,
        Optional<HolidayType> type,
        Optional<Boolean> recurring,
        Optional<String> description,
        Optional<LocalDate> observed
    ) implements HolidayCommand {
        public Update {
            Objects.requireNonNull(id, "ID cannot be null");
            if (id.isBlank()) {
                throw new IllegalArgumentException("ID cannot be blank");
            }
            
            name = Objects.requireNonNullElse(name, Optional.empty());
            date = Objects.requireNonNullElse(date, Optional.empty());
            location = Objects.requireNonNullElse(location, Optional.empty());
            type = Objects.requireNonNullElse(type, Optional.empty());
            recurring = Objects.requireNonNullElse(recurring, Optional.empty());
            description = Objects.requireNonNullElse(description, Optional.empty());
            observed = Objects.requireNonNullElse(observed, Optional.empty());
        }
        
        public boolean hasChanges() {
            return name.isPresent() || date.isPresent() || location.isPresent() || 
                   type.isPresent() || recurring.isPresent() || description.isPresent() || 
                   observed.isPresent();
        }
    }
    
    /**
     * Command to delete a holiday.
     */
    record Delete(String id) implements HolidayCommand {
        public Delete {
            Objects.requireNonNull(id, "ID cannot be null");
            if (id.isBlank()) {
                throw new IllegalArgumentException("ID cannot be blank");
            }
        }
    }
    
    /**
     * Command to cancel a holiday.
     */
    record Cancel(String id, String reason) implements HolidayCommand {
        public Cancel {
            Objects.requireNonNull(id, "ID cannot be null");
            if (id.isBlank()) {
                throw new IllegalArgumentException("ID cannot be blank");
            }
            Objects.requireNonNull(reason, "Reason cannot be null");
            if (reason.isBlank()) {
                throw new IllegalArgumentException("Reason cannot be blank");
            }
        }
    }
    
    /**
     * Command to activate a holiday.
     */
    record Activate(String id) implements HolidayCommand {
        public Activate {
            Objects.requireNonNull(id, "ID cannot be null");
            if (id.isBlank()) {
                throw new IllegalArgumentException("ID cannot be blank");
            }
        }
    }
}

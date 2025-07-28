package me.clementino.holiday.domain;

import java.util.List;

/**
 * Sealed interface representing validation results.
 * Following DOP principle: Make illegal states unrepresentable.
 * Can only be Success or Failure - no other states possible.
 */
public sealed interface ValidationResult 
    permits ValidationResult.Success, ValidationResult.Failure {
    
    record Success(String message) implements ValidationResult {}
    
    record Failure(List<String> errors) implements ValidationResult {
        public Failure {
            if (errors == null || errors.isEmpty()) {
                throw new IllegalArgumentException("Failure must have at least one error");
            }
        }
        
        public Failure(String error) {
            this(List.of(error));
        }
    }
    
    /**
     * Returns true if validation was successful.
     */
    default boolean isSuccess() {
        return this instanceof Success;
    }
    
    /**
     * Returns true if validation failed.
     */
    default boolean isFailure() {
        return this instanceof Failure;
    }
}

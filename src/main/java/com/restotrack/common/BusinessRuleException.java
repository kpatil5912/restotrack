package com.restotrack.common;

/** Thrown when a request is well-formed but violates a domain rule
 *  (e.g. insufficient stock, incompatible units). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}

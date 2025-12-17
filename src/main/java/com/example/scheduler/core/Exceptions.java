package com.example.scheduler.core;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}

public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}

public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}

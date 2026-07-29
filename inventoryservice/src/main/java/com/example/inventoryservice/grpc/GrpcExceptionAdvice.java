package com.example.inventoryservice.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
@Slf4j
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public StatusRuntimeException handleInvalidArgument(IllegalArgumentException e) {
        log.error("Invalid argument: {}", e.getMessage());
        return Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(EntityNotFoundException.class)
    public StatusRuntimeException handleNotFound(EntityNotFoundException e) {
        log.error("Entity not found: {}", e.getMessage());

        return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(IllegalStateException.class)
    public StatusRuntimeException handleConflict(IllegalStateException e) {
        log.error("Conflict: {}", e.getMessage());
        return Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException();
    }
}
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
        log.warn("Invalid argument: {}", e.getMessage());
        return Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(EntityNotFoundException.class)
    public StatusRuntimeException handleNotFound(EntityNotFoundException e) {
        log.warn("Entity not found: {}", e.getMessage());
        return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(IllegalStateException.class)
    public StatusRuntimeException handleConflict(IllegalStateException e) {
        log.warn("Conflict: {}", e.getMessage());
        return Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException();
    }

    // Fallback: sem isto qualquer excecao nao mapeada saia como UNKNOWN.
    // A mensagem original fica so' no log, nunca vai para o cliente.
    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleUnexpected(Exception e) {
        log.error("Unexpected error handling gRPC call", e);
        return Status.INTERNAL.withDescription("Internal server error").asRuntimeException();
    }
}
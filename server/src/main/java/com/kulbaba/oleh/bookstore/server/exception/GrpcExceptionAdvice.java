package com.kulbaba.oleh.bookstore.server.exception;

import io.grpc.Status;
import io.grpc.StatusException;
import jakarta.persistence.EntityNotFoundException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(EntityNotFoundException.class)
    public StatusException handleEntityNotFoundException(EntityNotFoundException e){
        Status status = Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e);
        return status.asException();
    }
}

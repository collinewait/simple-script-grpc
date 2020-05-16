package com.wait.simplescript.server.infrastructure.web;

import com.wait.simplescript.server.script.InvalidOperationException;
import com.wait.simplescript.server.script.ScriptNotFoundException;
import com.wait.simplescript.server.user.UserNotFoundException;
import io.grpc.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class GrpcExceptionHandler implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall, Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {
        ServerCall.Listener<ReqT> listener =
                serverCallHandler.startCall(serverCall, metadata);
        return new ExceptionHandlingServerCallListener<>(listener, serverCall
                , metadata);
    }

    private class ExceptionHandlingServerCallListener<ReqT, RespT>
            extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {
        private final ServerCall<ReqT, RespT> serverCall;
        private final Metadata metadata;

        ExceptionHandlingServerCallListener(ServerCall.Listener<ReqT> listener,
                                            ServerCall<ReqT, RespT> serverCall,
                                            Metadata metadata) {
            super(listener);
            this.serverCall = serverCall;
            this.metadata = metadata;
        }

        @Override
        public void onHalfClose() {
            try {
                super.onHalfClose();
            } catch (RuntimeException ex) {
                handleException(ex, serverCall, metadata);
            }
        }

        @Override
        public void onReady() {
            try {
                super.onReady();
            } catch (RuntimeException ex) {
                handleException(ex, serverCall, metadata);
            }
        }

        private void handleException(RuntimeException exception,
                                     ServerCall<ReqT, RespT> serverCall,
                                     Metadata metadata) {
            if (exception instanceof IllegalArgumentException) {
                serverCall.close(Status.INVALID_ARGUMENT.withDescription(exception.getMessage()), metadata);
            } else if (exception instanceof UsernameNotFoundException) {
                serverCall.close(Status.NOT_FOUND.withDescription(exception.getMessage()), metadata);
            } else if (exception instanceof BadCredentialsException) {
                serverCall.close(Status.UNAUTHENTICATED.withDescription(exception.getMessage()), metadata);
            } else if (exception instanceof UserNotFoundException) {
                serverCall.close(Status.NOT_FOUND.withDescription(exception.getMessage()), metadata);
            } else if (exception instanceof InvalidOperationException) {
                serverCall.close(Status.INVALID_ARGUMENT.withDescription(exception.getMessage()), metadata);
            } else if (exception instanceof ScriptNotFoundException) {
                serverCall.close(Status.NOT_FOUND.withDescription(exception.getMessage()), metadata);
            } else {
                serverCall.close(Status.UNKNOWN.withDescription(exception.getMessage()), metadata);
            }
        }
    }
}

package io.github.mauricio_mds.mobile_app_ws.exceptions;

import java.io.Serial;

public class UserServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5845660512120261208L;

    public UserServiceException(String message) {
        super(message);
    }
}

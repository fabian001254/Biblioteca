package com.biblioteca.biblioteca.exceptions.userExceptions;

public class UserInvalidCredentials extends RuntimeException {
    public UserInvalidCredentials() {
        super("Invalid credentials");
    }
}

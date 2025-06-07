package com.biblioteca.biblioteca.exceptions.userExceptions;

public class UserNotExist extends RuntimeException {
    public UserNotExist() {
        super("User not exist");
    }
}

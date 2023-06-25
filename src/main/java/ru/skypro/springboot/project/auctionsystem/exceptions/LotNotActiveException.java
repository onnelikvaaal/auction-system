package ru.skypro.springboot.project.auctionsystem.exceptions;

public class LotNotActiveException extends Exception {

    public LotNotActiveException(String message) {
        super(message);
    }
}

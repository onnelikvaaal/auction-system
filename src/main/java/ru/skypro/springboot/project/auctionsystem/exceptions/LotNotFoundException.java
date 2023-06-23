package ru.skypro.springboot.project.auctionsystem.exceptions;

public class LotNotFoundException extends Exception {

    public LotNotFoundException(String message) {
        super(message);
    }
}

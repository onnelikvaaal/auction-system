package ru.skypro.springboot.project.auctionsystem.exceptions;

public class BidNotFoundException extends Exception {

    public BidNotFoundException(String message) {
        super(message);
    }
}

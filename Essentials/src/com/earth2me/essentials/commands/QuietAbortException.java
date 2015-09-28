package com.earth2me.essentials.commands;


public class QuietAbortException extends Exception {
    public QuietAbortException() {
    }

    public QuietAbortException(String message) {
        super(message);
    }
}

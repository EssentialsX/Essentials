package com.earth2me.essentials.commands;

public class QuietAbortException extends Exception {
    public QuietAbortException() {
        super();
    }

    public QuietAbortException(final String message) {
        super(message);
    }
}

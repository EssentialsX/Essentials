package com.earth2me.essentials.commands;

public class TooManyArgumentsException extends Exception {
    public TooManyArgumentsException() {
        super("");
    }

    public TooManyArgumentsException(final String string) {
        super(string);
    }

    public TooManyArgumentsException(final Throwable ex) {
        super("", ex);
    }
}

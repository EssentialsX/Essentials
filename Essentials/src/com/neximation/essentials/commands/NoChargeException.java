package com.neximation.essentials.commands;


public class NoChargeException extends Exception {
    public NoChargeException() {
        super("Will charge later");
    }
}

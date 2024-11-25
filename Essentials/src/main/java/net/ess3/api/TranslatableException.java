package net.ess3.api;

import static com.earth2me.essentials.I18n.tlLiteral;

/**
 * This exception should be thrown during commands with messages that should be translated.
 */
public class TranslatableException extends Exception {
    private final String tlKey;
    private final Object[] args;

    public TranslatableException(String tlKey, Object... args) {
        this(null, tlKey, args);
    }

    public TranslatableException(Throwable cause, String tlKey, Object... args) {
        this.tlKey = tlKey;
        this.args = args;
        if (cause != null) {
            initCause(cause);
        }
    }

    /**
     * Sets a cause.
     */
    public TranslatableException setCause(Throwable cause) {
        initCause(cause);
        return this;
    }

    public String getTlKey() {
        return tlKey;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String getMessage() {
        return tlLiteral(tlKey, args);
    }
}

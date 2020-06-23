package net.ess3.provider;

public interface ServerStateProvider extends Provider {
    boolean isStopping();

    @Override
    default boolean tryProvider() {
        return false;
    }
}

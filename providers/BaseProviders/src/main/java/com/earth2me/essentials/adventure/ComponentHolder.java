package com.earth2me.essentials.adventure;

public class ComponentHolder {
    private final Object component;

    protected ComponentHolder(final Object component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }

        this.component = component;
    }

    public Object getComponent() {
        return component;
    }
}

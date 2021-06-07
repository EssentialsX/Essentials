package com.earth2me.essentials.config.entities;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.regex.Pattern;

@ConfigSerializable
public class CommandCooldown {
    private Pattern pattern;

    public Pattern pattern() {
        return this.pattern;
    }

    public void pattern(final Pattern value) {
        this.pattern = value;
    }

    private Long value;

    public Long value() {
        return this.value;
    }

    public void value(final Long value) {
        this.value = value;
    }
}

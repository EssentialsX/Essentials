package com.earth2me.essentials.config;

import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EssentialsConfiguration {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private final File configFile;
    private final YamlConfigurationLoader loader;

    public EssentialsConfiguration(final File configFile) {
        this.configFile = configFile;
        loader = YamlConfigurationLoader.builder().file(configFile).build();

    }

    public synchronized void load() {

    }
}

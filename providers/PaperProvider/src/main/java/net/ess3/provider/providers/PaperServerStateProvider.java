package net.ess3.provider.providers;

import net.ess3.provider.ServerStateProvider;
import org.bukkit.Bukkit;

public class PaperServerStateProvider implements ServerStateProvider {
    @Override
    public boolean isStopping() {
        return Bukkit.isStopping();
    }

    @Override
    public String getDescription() {
        return "Paper Server State Provider";
    }
}

package com.earth2me.essentials.perm.impl;

import com.earth2me.essentials.perm.context.luckperms.AfkContextCalculator;
import com.earth2me.essentials.perm.context.luckperms.MuteContextCalculator;
import com.earth2me.essentials.perm.context.luckperms.VanishContextCalculator;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsHandler extends ModernVaultHandler {
    private LuckPerms luckPerms;

    @Override
    public void registerContexts() {
        ContextManager contextManager = luckPerms.getContextManager();
        contextManager.registerCalculator(new AfkContextCalculator(ess));
        contextManager.registerCalculator(new MuteContextCalculator(ess));
        contextManager.registerCalculator(new VanishContextCalculator(ess));
    }

    @Override
    public boolean tryProvider() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
        return luckPerms != null && super.tryProvider();
    }
}

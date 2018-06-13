package com.earth2me.essentials.vault;

import net.ess3.api.IEssentials;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.plugins.Economy_Essentials;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

/**
 * Responsible for registering EssentialsX's own Vault hook, instead of relying on
 * the outdated Essentials Economy hook built into Vault.
 */
public class VaultProviderManager {

    IEssentials ess;

    public VaultProviderManager(IEssentials ess) {
        this.ess = ess;

        if (!ess.getSettings().isEcoDisabled() && ess.getSettings().useNewVaultHook()) {
            registerNew();
            deregisterLegacy();
        }
    }

    /**
     * Deregister Vault's default legacy EssentialsEco hook.
     */
    public boolean deregisterLegacy() {
        final Plugin vaultPlugin = ess.getServer().getPluginManager().getPlugin("Vault");
        final ServicesManager sm = ess.getServer().getServicesManager();

        for (RegisteredServiceProvider rsp : sm.getRegistrations(vaultPlugin)) {
            if (rsp.getProvider() instanceof Economy_Essentials) {
                sm.unregister(rsp.getProvider());
                ess.getLogger().info("Removed Vault's legacy Economy_Essentials handler.");
                return true;
            }
        }

        return false;
    }

    /**
     * Register EssentialsX's own Vault hook.
     */
    public void registerNew() {
        ess.getServer().getServicesManager().register(Economy.class, new EconomyProvider(ess), ess, ServicePriority.Low);
        ess.getLogger().info("Enabled UUID-friendly economy support for Vault!");
    }
}

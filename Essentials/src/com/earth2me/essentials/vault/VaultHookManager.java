package com.earth2me.essentials.vault;

import com.earth2me.essentials.Essentials;
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
public class VaultHookManager {

    /**
     * Deregister Vault's default legacy EssentialsEco hook.
     */
    public boolean unhookLegacy(Essentials ess) {
        final Plugin vaultPlugin = ess.getServer().getPluginManager().getPlugin("Vault");
        final ServicesManager sm = ess.getServer().getServicesManager();

        for (RegisteredServiceProvider rsp : sm.getRegistrations(vaultPlugin)) {
            if (rsp.getProvider() instanceof Economy_Essentials) {
                sm.unregister(rsp.getProvider());
                return true;
            }
        }

        return false;
    }

    /**
     * Register EssentialsX's own Vault hook.
     */
    public void hookEssXEco(Essentials ess) {
        ess.getServer().getServicesManager().register(Economy.class, new EconomyHook(ess), ess, ServicePriority.High);
    }
}

package com.earth2me.essentials.vault;

import com.earth2me.essentials.Essentials;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

/**
 * Responsible for registering EssentialsX's own Vault hook, instead of relying on
 * the outdated Essentials Economy hook built into Vault.
 */
public class VaultHookManager {

    /**
     * Deregister Vault's default hooks, including the legacy EssentialsEco hook.
     * This may not always be desired behaviour.
     */
    public void unhookLegacy(Essentials ess) {
        Plugin vaultPlugin = ess.getServer().getPluginManager().getPlugin("Vault");
        ess.getServer().getServicesManager().unregister(Economy.class, vaultPlugin);
    }

    /**
     * Register EssentialsX's own Vault hook.
     */
    public void hookEssXEco(Essentials ess) {
        ess.getServer().getServicesManager().register(Economy.class, new EconomyHook(ess), ess, ServicePriority.High);
    }
}

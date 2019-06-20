package com.earth2me.essentials.protect;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;


/**
 * <p>IProtect interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IProtect extends Plugin {
    /**
     * <p>getSettingBool.</p>
     *
     * @param protectConfig a {@link com.earth2me.essentials.protect.ProtectConfig} object.
     * @return a boolean.
     */
    boolean getSettingBool(final ProtectConfig protectConfig);

    /**
     * <p>getSettingString.</p>
     *
     * @param protectConfig a {@link com.earth2me.essentials.protect.ProtectConfig} object.
     * @return a {@link java.lang.String} object.
     */
    String getSettingString(final ProtectConfig protectConfig);

    /**
     * <p>getEssentialsConnect.</p>
     *
     * @return a {@link com.earth2me.essentials.protect.EssentialsConnect} object.
     */
    EssentialsConnect getEssentialsConnect();

    /**
     * <p>getSettingsBoolean.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<ProtectConfig, Boolean> getSettingsBoolean();

    /**
     * <p>getSettingsString.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<ProtectConfig, String> getSettingsString();

    /**
     * <p>getSettingsList.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<ProtectConfig, List<Material>> getSettingsList();
}

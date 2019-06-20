package com.earth2me.essentials.antibuild;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;


/**
 * <p>IAntiBuild interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IAntiBuild extends Plugin {
    /**
     * <p>checkProtectionItems.</p>
     *
     * @param list a {@link com.earth2me.essentials.antibuild.AntiBuildConfig} object.
     * @param mat a {@link org.bukkit.Material} object.
     * @return a boolean.
     */
    boolean checkProtectionItems(final AntiBuildConfig list, final Material mat);

    /**
     * <p>getSettingBool.</p>
     *
     * @param protectConfig a {@link com.earth2me.essentials.antibuild.AntiBuildConfig} object.
     * @return a boolean.
     */
    boolean getSettingBool(final AntiBuildConfig protectConfig);

    /**
     * <p>getEssentialsConnect.</p>
     *
     * @return a {@link com.earth2me.essentials.antibuild.EssentialsConnect} object.
     */
    EssentialsConnect getEssentialsConnect();

    /**
     * <p>getSettingsBoolean.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<AntiBuildConfig, Boolean> getSettingsBoolean();

    /**
     * <p>getSettingsList.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<AntiBuildConfig, List<Material>> getSettingsList();
}

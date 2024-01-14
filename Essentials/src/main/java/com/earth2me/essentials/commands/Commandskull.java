package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.MaterialUtil;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;

public class Commandskull extends EssentialsCommand {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");
    private static final Pattern URL_VALUE_PATTERN = Pattern.compile("^[0-9a-fA-F]{64}$");
    private static final Pattern BASE_64_PATTERN = Pattern.compile("^[A-Za-z0-9+/=]{180}$");

    private static final Material SKULL_ITEM = EnumUtil.getMaterial("PLAYER_HEAD", "SKULL_ITEM");

    private final boolean playerProfileSupported;

    public Commandskull() {
        super("skull");

        // The player profile API is only available in newer versions of Spigot 1.18.1 and above
        boolean playerProfileSupported = true;
        try {
            Class.forName("org.bukkit.profile.PlayerProfile");
        } catch (final ClassNotFoundException e) {
            playerProfileSupported = false;
        }
        this.playerProfileSupported = playerProfileSupported;
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final String owner;
        if (args.length > 0 && user.isAuthorized("essentials.skull.others")) {
            if (BASE_64_PATTERN.matcher(args[0]).matches()) {
                try {
                    final String decoded = new String(Base64.getDecoder().decode(args[0]));
                    final JsonObject jsonObject = JsonParser.parseString(decoded).getAsJsonObject();
                    final String url = jsonObject
                            .getAsJsonObject("textures")
                            .getAsJsonObject("SKIN")
                            .get("url")
                            .getAsString();
                    owner = url.substring(url.lastIndexOf("/") + 1);
                } catch (final Exception e) {
                    // Any exception that can realistically happen here is caused by an invalid texture value
                    throw new IllegalArgumentException(tl("skullInvalidBase64"));
                }

                if (!URL_VALUE_PATTERN.matcher(owner).matches()) {
                    throw new IllegalArgumentException(tl("skullInvalidBase64"));
                }
            } else if (!NAME_PATTERN.matcher(args[0]).matches()) {
                throw new IllegalArgumentException(tl("alphaNames"));
            } else {
                owner = args[0];
            }
        } else {
            owner = user.getName();
        }

        ItemStack itemSkull = user.getItemInHand();
        final SkullMeta metaSkull;
        boolean spawn = false;

        if (itemSkull != null && MaterialUtil.isPlayerHead(itemSkull)) {
            metaSkull = (SkullMeta) itemSkull.getItemMeta();
        } else if (user.isAuthorized("essentials.skull.spawn")) {
            itemSkull = new ItemStack(SKULL_ITEM, 1, (byte) 3);
            metaSkull = (SkullMeta) itemSkull.getItemMeta();
            spawn = true;
        } else {
            throw new Exception(tl("invalidSkull"));
        }

        if (metaSkull.hasOwner() && !user.isAuthorized("essentials.skull.modify")) {
            throw new Exception(tl("noPermissionSkull"));
        }

        editSkull(user, itemSkull, metaSkull, owner, spawn);
    }

    private void editSkull(final User user, final ItemStack stack, final SkullMeta skullMeta, final String owner, final boolean spawn) {
        ess.runTaskAsynchronously(() -> {
            // Run this stuff async because it causes an HTTP request

            final String shortOwnerName;
            if (URL_VALUE_PATTERN.matcher(owner).matches()) {
                if (!playerProfileSupported) {
                    user.sendMessage(tl("unsupportedFeature"));
                    return;
                }

                final URL url;
                try {
                    url = new URL("https://textures.minecraft.net/texture/" + owner);
                } catch (final MalformedURLException e) {
                    // The URL should never be malformed
                    throw new RuntimeException(e);
                }

                final PlayerProfile profile = ess.getServer().createPlayerProfile(UUID.randomUUID());
                profile.getTextures().setSkin(url);
                skullMeta.setOwnerProfile(profile);

                shortOwnerName = owner.substring(0, 7);
            } else {
                //noinspection deprecation
                skullMeta.setOwner(owner);
                shortOwnerName = owner;
            }
            skullMeta.setDisplayName("§fSkull of " + shortOwnerName);

            ess.scheduleEntityDelayedTask(user.getBase(), () -> {
                stack.setItemMeta(skullMeta);
                if (spawn) {
                    Inventories.addItem(user.getBase(), stack);
                    user.sendMessage(tl("givenSkull", shortOwnerName));
                    return;
                }
                user.sendMessage(tl("skullChanged", shortOwnerName));
            });
        });
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            if (user.isAuthorized("essentials.skull.others")) {
                return getPlayers(server, user);
            } else {
                return Lists.newArrayList(user.getName());
            }
        } else {
            return Collections.emptyList();
        }
    }

}

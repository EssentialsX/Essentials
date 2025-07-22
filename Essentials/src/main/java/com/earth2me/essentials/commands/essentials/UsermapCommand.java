package com.earth2me.essentials.commands.essentials;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.EssentialsUpgrade;
import com.earth2me.essentials.commands.EssentialsTreeNode;
import com.earth2me.essentials.userstorage.ModernUserMap;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class UsermapCommand extends EssentialsTreeNode {

    public UsermapCommand() {
        super("usermap");
    }

    @Override
    protected void run(final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (!sender.isAuthorized("essentials.usermap")) {
            return;
        }

        final ModernUserMap userMap = (ModernUserMap) ess.getUsers();
        sender.sendTl("usermapSize", userMap.getCachedCount(), userMap.getUserCount(), ess.getSettings().getMaxUserCacheCount());
        if (args.length > 0) {
            if (args[0].equals("full")) {
                for (final Map.Entry<String, UUID> entry : userMap.getNameCache().entrySet()) {
                    sender.sendTl("usermapEntry", entry.getKey(), entry.getValue().toString());
                }
            } else if (args[0].equals("purge")) {
                final boolean seppuku = args.length > 1 && args[1].equals("iknowwhatimdoing");

                sender.sendTl("usermapPurge", String.valueOf(seppuku));

                final Set<UUID> uuids = new HashSet<>(ess.getUsers().getAllUserUUIDs());
                ess.runTaskAsynchronously(() -> {
                    final File userdataFolder = new File(ess.getDataFolder(), "userdata");
                    final File backupFolder = new File(ess.getDataFolder(), "userdata-npc-backup-boogaloo-" + System.currentTimeMillis());

                    if (!userdataFolder.isDirectory()) {
                        ess.getLogger().warning("Missing userdata folder, aborting usermap purge.");
                        return;
                    }

                    if (seppuku && !backupFolder.mkdir()) {
                        ess.getLogger().warning("Unable to create backup folder, aborting usermap purge.");
                        return;
                    }

                    int total = 0;
                    final File[] files = userdataFolder.listFiles(EssentialsUpgrade.YML_FILTER);
                    if (files != null) {
                        for (final File file : files) {
                            try {
                                final String fileName = file.getName();
                                final UUID uuid = UUID.fromString(fileName.substring(0, fileName.length() - 4));
                                if (!uuids.contains(uuid)) {
                                    total++;
                                    ess.getLogger().warning("Found orphaned userdata file: " + file.getName());
                                    if (seppuku) {
                                        try {
                                            com.google.common.io.Files.move(file, new File(backupFolder, file.getName()));
                                        } catch (IOException e) {
                                            ess.getLogger().log(Level.WARNING, "Unable to move orphaned userdata file: " + file.getName(), e);
                                        }
                                    }
                                }
                            } catch (IllegalArgumentException ignored) {
                            }
                        }
                    }
                    ess.getLogger().info("Found " + total + " orphaned userdata files.");
                });
            } else if (args[0].equalsIgnoreCase("cache")) {
                sender.sendTl("usermapKnown", ess.getUsers().getAllUserUUIDs().size(), ess.getUsers().getNameCache().size());
            } else {
                try {
                    final UUID uuid = UUID.fromString(args[0]);
                    for (final Map.Entry<String, UUID> entry : userMap.getNameCache().entrySet()) {
                        if (entry.getValue().equals(uuid)) {
                            sender.sendTl("usermapEntry", entry.getKey(), args[0]);
                        }
                    }
                } catch (IllegalArgumentException ignored) {
                    final String sanitizedName = userMap.getSanitizedName(args[0]);
                    sender.sendTl("usermapEntry", sanitizedName, userMap.getNameCache().get(sanitizedName).toString());
                }
            }
        }
    }

    @Override
    protected List<String> tabComplete(CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("full", "purge", "cache");
        }
        return Collections.emptyList();
    }
}

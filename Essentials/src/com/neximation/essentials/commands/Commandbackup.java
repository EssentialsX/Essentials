package com.neximation.essentials.commands;

import com.neximation.essentials.Backup;
import com.neximation.essentials.CommandSource;
import org.bukkit.Server;

import static com.neximation.essentials.I18n.tl;


public class Commandbackup extends EssentialsCommand {
    public Commandbackup() {
        super("backup");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        final Backup backup = ess.getBackup();
        if (backup == null) {
            throw new Exception(tl("backupDisabled"));
        }
        final String command = ess.getSettings().getBackupCommand();
        if (command == null || "".equals(command) || "save-all".equalsIgnoreCase(command)) {
            throw new Exception(tl("backupDisabled"));
        }
        backup.run();
        sender.sendMessage(tl("backupStarted"));
    }
}

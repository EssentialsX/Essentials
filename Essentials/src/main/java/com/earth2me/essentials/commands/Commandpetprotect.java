package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import net.ess3.api.events.PetProtectionStatusChangeEvent;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandpetprotect extends EssentialsToggleCommand {
    public Commandpetprotect() {
        super("petprotect", "essentials.petprotect.others");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        toggleOtherPlayers(server, sender, args);
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        handleToggleWithArgs(server, user, args);
    }

    @Override
    protected void togglePlayer(CommandSource sender, User user, Boolean enabled) throws NotEnoughArgumentsException {
        if (enabled == null) {
            enabled = !user.isPetProtectionEnabled();
        }
        final PetProtectionStatusChangeEvent petEvent = new PetProtectionStatusChangeEvent(user, sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null, enabled);
        ess.getServer().getPluginManager().callEvent(petEvent);
        if (!petEvent.isCancelled()) {
            user.setPetProtectionEnabled(enabled);

            user.sendMessage(tl("petprotect", enabled ? tl("enabled") : tl("disabled")));
            if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
                sender.sendMessage(tl("petprotect", tl(enabled ? "petprotectnEnabledFor" : "petprotectDisabledFor", user.getDisplayName())));
            }
        }
    }
}

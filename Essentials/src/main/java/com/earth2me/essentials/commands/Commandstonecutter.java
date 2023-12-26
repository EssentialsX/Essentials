package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import net.ess3.provider.ContainerProvider;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandstonecutter extends EssentialsCommand {

    public Commandstonecutter() {
        super("stonecutter");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ContainerProvider containerProvider = ess.provider(ContainerProvider.class);
        
        if (containerProvider == null) {
            user.sendMessage(tl("unsupportedBrand"));
            return;
        }

        containerProvider.openStonecutter(user.getBase());
    }
}

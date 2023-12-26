package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import net.ess3.provider.ContainerProvider;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandanvil extends EssentialsCommand {

    public Commandanvil() {
        super("anvil");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        final ContainerProvider containerProvider = ess.provider(ContainerProvider.class);

        if (containerProvider == null) {
            user.sendMessage(tl("unsupportedBrand"));
            return;
        }

        containerProvider.openAnvil(user.getBase());
    }
}

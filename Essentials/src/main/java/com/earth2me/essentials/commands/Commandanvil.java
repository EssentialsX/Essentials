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
        if (ess.getProviders().get(ContainerProvider.class) == null) {
            user.sendMessage(tl("unsupportedBrand"));
            return;
        }

        ess.getProviders().get(ContainerProvider.class).openAnvil(user.getBase());
    }
}

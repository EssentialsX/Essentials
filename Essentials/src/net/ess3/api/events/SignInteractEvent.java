package net.ess3.api.events;

import com.earth2me.essentials.signs.EssentialsSign;
import net.ess3.api.IUser;

public class SignInteractEvent extends SignEvent {
    public SignInteractEvent(final EssentialsSign.ISign sign, final EssentialsSign essSign, final IUser user) {
        super(sign, essSign, user);
    }
}

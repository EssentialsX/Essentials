package net.ess3.api.events;

import com.earth2me.essentials.signs.EssentialsSign;
import net.ess3.api.IUser;


/**
 * <p>SignBreakEvent class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class SignBreakEvent extends SignEvent {
    /**
     * <p>Constructor for SignBreakEvent.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param essSign a {@link com.earth2me.essentials.signs.EssentialsSign} object.
     * @param user a {@link net.ess3.api.IUser} object.
     */
    public SignBreakEvent(EssentialsSign.ISign sign, EssentialsSign essSign, IUser user) {
        super(sign, essSign, user);
    }
}

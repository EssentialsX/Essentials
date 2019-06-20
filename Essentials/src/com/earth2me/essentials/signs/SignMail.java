package com.earth2me.essentials.signs;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;

import java.util.List;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>SignMail class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class SignMail extends EssentialsSign {
    /**
     * <p>Constructor for SignMail.</p>
     */
    public SignMail() {
        super("Mail");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        final List<String> mail = player.getMails();
        if (mail.isEmpty()) {
            player.sendMessage(tl("noNewMail"));
            return false;
        }
        for (String s : mail) {
            player.sendMessage(s);
        }
        player.sendMessage(tl("markMailAsRead"));
        return true;
    }
}

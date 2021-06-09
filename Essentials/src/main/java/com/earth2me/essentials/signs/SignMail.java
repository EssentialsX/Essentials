package com.earth2me.essentials.signs;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.essentialsx.api.v2.services.mail.MailMessage;

import java.util.ArrayList;
import java.util.ListIterator;

import static com.earth2me.essentials.I18n.tl;

public class SignMail extends EssentialsSign {
    public SignMail() {
        super("Mail");
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        final ArrayList<MailMessage> mail = player.getMailMessages();

        final ListIterator<MailMessage> iterator = mail.listIterator();
        boolean hadMail = false;
        while (iterator.hasNext()) {
            final MailMessage mailObj = iterator.next();
            if (mailObj.isExpired()) {
                iterator.remove();
                continue;
            }
            hadMail = true;
            player.sendMessage(ess.getMail().getMailLine(mailObj));
            iterator.set(new MailMessage(true, mailObj.isLegacy(), mailObj.getSenderUsername(),
                    mailObj.getSenderUUID(), mailObj.getTimeSent(), mailObj.getTimeExpire(), mailObj.getMessage()));
        }

        if (!hadMail) {
            player.sendMessage(tl("noNewMail"));
            return false;
        }
        player.setMailList(mail);

        player.sendMessage(tl("markMailAsRead"));
        return true;
    }
}

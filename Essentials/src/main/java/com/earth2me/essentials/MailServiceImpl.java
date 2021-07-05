package com.earth2me.essentials;

import net.ess3.api.IUser;
import net.essentialsx.api.v2.services.mail.MailService;
import net.essentialsx.api.v2.services.mail.MailMessage;
import net.essentialsx.api.v2.services.mail.MailSender;
import org.bukkit.plugin.ServicePriority;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.earth2me.essentials.I18n.tl;

public class MailServiceImpl implements MailService {
    private final transient ThreadLocal<SimpleDateFormat> df = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy/MM/dd HH:mm"));

    public MailServiceImpl(IEssentials ess) {
        ess.getServer().getServicesManager().register(MailService.class, this, ess, ServicePriority.Normal);
    }

    @Override
    public void sendMail(IUser recipient, MailSender sender, String message) {
        sendMail(recipient, sender, message, 0L);
    }

    @Override
    public void sendMail(IUser recipient, MailSender sender, String message, long expireAt) {
        sendMail(recipient, new MailMessage(false, false, sender.getName(), sender.getUUID(), System.currentTimeMillis(), expireAt, message));
    }

    @Override
    public void sendLegacyMail(IUser recipient, String message) {
        sendMail(recipient, new MailMessage(false, true, null, null, 0L, 0L, message));
    }

    private void sendMail(IUser recipient, MailMessage message) {
        final ArrayList<MailMessage> messages = recipient.getMailMessages();
        messages.add(0, message);
        recipient.setMailList(messages);
    }

    @Override
    public String getMailLine(MailMessage mail) {
        final String message = mail.getMessage();
        if (mail.isLegacy()) {
            return tl("mailMessage", message);
        }

        final String expire = mail.getTimeExpire() != 0 ? "Timed" : "";
        return tl((mail.isRead() ? "mailFormatNewRead" : "mailFormatNew") + expire, df.get().format(new Date(mail.getTimeSent())), mail.getSenderUsername(), message);
    }
}

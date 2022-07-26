package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.textreader.SimpleTextPager;
import com.earth2me.essentials.textreader.SimpleTranslatableText;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.collect.Lists;
import net.ess3.api.TranslatableException;
import net.essentialsx.api.v2.services.mail.MailMessage;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class Commandmail extends EssentialsCommand {
    private static int mailsPerMinute = 0;
    private static long timestamp = 0;

    public Commandmail() {
        super("mail");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length >= 1 && "read".equalsIgnoreCase(args[0])) {
            final ArrayList<MailMessage> mail = user.getMailMessages();
            if (mail == null || mail.size() == 0) {
                user.sendTl("noMail");
                throw new NoChargeException();
            }

            final SimpleTranslatableText input = new SimpleTranslatableText();
            final ListIterator<MailMessage> iterator = mail.listIterator();
            while (iterator.hasNext()) {
                final MailMessage mailObj = iterator.next();
                if (mailObj.isExpired()) {
                    iterator.remove();
                    continue;
                }
                input.addLine(ess.getMail().getMailTlKey(mailObj), ess.getMail().getMailTlArgs(mailObj));
                iterator.set(new MailMessage(true, mailObj.isLegacy(), mailObj.getSenderUsername(),
                        mailObj.getSenderUUID(), mailObj.getTimeSent(), mailObj.getTimeExpire(), mailObj.getMessage()));
            }

            if (input.getLines().isEmpty()) {
                user.sendTl("noMail");
                throw new NoChargeException();
            }

            final SimpleTextPager pager = new SimpleTextPager(input);
            pager.showPage(user.getSource(), args.length > 1 ? args[1] : null, commandLabel + " " + args[0]);

            user.sendTl("mailClear");
            user.setMailList(mail);
            return;
        }
        if (args.length >= 3 && "send".equalsIgnoreCase(args[0])) {
            if (!user.isAuthorized("essentials.mail.send")) {
                throw new TranslatableException("noPerm", "essentials.mail.send");
            }

            if (user.isMuted()) {
                final String dateDiff = user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : null;
                if (dateDiff == null) {
                    throw new TranslatableException(user.hasMuteReason() ? "voiceSilencedReason" : "voiceSilenced", user.getMuteReason());
                }
                throw new TranslatableException(user.hasMuteReason() ? "voiceSilencedReasonTime" : "voiceSilencedTime", dateDiff, user.getMuteReason());
            }

            final User u;
            try {
                u = getPlayer(server, args[1], true, true);
            } catch (final PlayerNotFoundException e) {
                throw new TranslatableException("playerNeverOnServer", args[1]);
            }

            final String msg = FormatUtil.formatMessage(user, "essentials.mail", StringUtil.sanitizeString(FormatUtil.stripFormat(getFinalArg(args, 2))));
            if (msg.length() > 1000) {
                throw new TranslatableException("mailTooLong");
            }

            if (!u.isIgnoredPlayer(user)) {
                if (Math.abs(System.currentTimeMillis() - timestamp) > 60000) {
                    timestamp = System.currentTimeMillis();
                    mailsPerMinute = 0;
                }
                mailsPerMinute++;
                if (mailsPerMinute > ess.getSettings().getMailsPerMinute()) {
                    throw new TranslatableException("mailDelay", ess.getSettings().getMailsPerMinute());
                }
                u.sendMail(user, msg);
            }

            user.sendTl("mailSentTo", u.getDisplayName(), u.getName());
            user.sendMessage(msg);
            return;
        }
        if (args.length >= 4 && "sendtemp".equalsIgnoreCase(args[0])) {
            if (!user.isAuthorized("essentials.mail.sendtemp")) {
                throw new TranslatableException("noPerm", "essentials.mail.sendtemp");
            }

            if (user.isMuted()) {
                final String dateDiff = user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : null;
                if (dateDiff == null) {
                    throw new TranslatableException(user.hasMuteReason() ? "voiceSilencedReason" : "voiceSilenced", user.getMuteReason());
                }
                throw new TranslatableException(user.hasMuteReason() ? "voiceSilencedReasonTime" : "voiceSilencedTime", dateDiff, user.getMuteReason());
            }

            final User u;
            try {
                u = getPlayer(server, args[1], true, true);
            } catch (final PlayerNotFoundException e) {
                throw new TranslatableException("playerNeverOnServer", args[1]);
            }

            final long dateDiff = DateUtil.parseDateDiff(args[2], true);

            final String msg = FormatUtil.formatMessage(user, "essentials.mail", StringUtil.sanitizeString(FormatUtil.stripFormat(getFinalArg(args, 3))));
            if (msg.length() > 1000) {
                throw new TranslatableException("mailTooLong");
            }

            if (!u.isIgnoredPlayer(user)) {
                if (Math.abs(System.currentTimeMillis() - timestamp) > 60000) {
                    timestamp = System.currentTimeMillis();
                    mailsPerMinute = 0;
                }
                mailsPerMinute++;
                if (mailsPerMinute > ess.getSettings().getMailsPerMinute()) {
                    throw new TranslatableException("mailDelay", ess.getSettings().getMailsPerMinute());
                }
                u.sendMail(user, msg, dateDiff);
            }

            user.sendTl("mailSentToExpire", u.getDisplayName(), DateUtil.formatDateDiff(dateDiff), u.getName());
            user.sendMessage(msg);
            return;
        }
        if (args.length > 1 && "sendall".equalsIgnoreCase(args[0])) {
            if (!user.isAuthorized("essentials.mail.sendall")) {
                throw new TranslatableException("noPerm", "essentials.mail.sendall");
            }
            ess.runTaskAsynchronously(new SendAll(user,
                    FormatUtil.formatMessage(user, "essentials.mail",
                            StringUtil.sanitizeString(FormatUtil.stripFormat(getFinalArg(args, 1)))), 0));
            user.sendTl("mailSent");
            return;
        }
        if (args.length >= 3 && "sendtempall".equalsIgnoreCase(args[0])) {
            if (!user.isAuthorized("essentials.mail.sendtempall")) {
                throw new TranslatableException("noPerm", "essentials.mail.sendtempall");
            }
            ess.runTaskAsynchronously(new SendAll(user,
                    FormatUtil.formatMessage(user, "essentials.mail",
                            StringUtil.sanitizeString(FormatUtil.stripFormat(getFinalArg(args, 2)))), DateUtil.parseDateDiff(args[1], true)));
            user.sendTl("mailSent");
            return;
        }
        if (args.length >= 1 && "clear".equalsIgnoreCase(args[0])) {
            final ArrayList<MailMessage> mails = user.getMailMessages();
            if (mails == null || mails.size() == 0) {
                user.sendTl("noMail");
                throw new NoChargeException();
            }

            if (args.length > 1) {
                if (!NumberUtil.isPositiveInt(args[1])) {
                    throw new NotEnoughArgumentsException();
                }

                final int toRemove = Integer.parseInt(args[1]);
                if (toRemove > mails.size()) {
                    user.sendTl("mailClearIndex", mails.size());
                    return;
                }
                mails.remove(toRemove - 1);
                user.setMailList(mails);
            } else {
                user.setMailList(null);
            }

            user.sendTl("mailCleared");
            return;
        }
        throw new NotEnoughArgumentsException();
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length >= 1 && "read".equalsIgnoreCase(args[0])) {
            throw new TranslatableException("onlyPlayers", commandLabel + " read");
        } else if (args.length >= 1 && "clear".equalsIgnoreCase(args[0])) {
            throw new TranslatableException("onlyPlayers", commandLabel + " clear");
        } else if (args.length >= 3 && "send".equalsIgnoreCase(args[0])) {
            final User u;
            try {
                u = getPlayer(server, args[1], true, true);
            } catch (final PlayerNotFoundException e) {
                throw new TranslatableException("playerNeverOnServer", args[1]);
            }
            u.sendMail(Console.getInstance(), FormatUtil.replaceFormat(getFinalArg(args, 2)));
            sender.sendTl("mailSent");
            return;
        } else if (args.length >= 4 && "sendtemp".equalsIgnoreCase(args[0])) {
            final User u;
            try {
                u = getPlayer(server, args[1], true, true);
            } catch (final PlayerNotFoundException e) {
                throw new TranslatableException("playerNeverOnServer", args[1]);
            }
            final long dateDiff = DateUtil.parseDateDiff(args[2], true);
            u.sendMail(Console.getInstance(), FormatUtil.replaceFormat(getFinalArg(args, 3)), dateDiff);
            sender.sendTl("mailSent");
            return;
        } else if (args.length >= 2 && "sendall".equalsIgnoreCase(args[0])) {
            ess.runTaskAsynchronously(new SendAll(Console.getInstance(), FormatUtil.replaceFormat(getFinalArg(args, 1)), 0));
            sender.sendTl("mailSent");
            return;
        } else if (args.length >= 3 && "sendtempall".equalsIgnoreCase(args[0])) {
            final long dateDiff = DateUtil.parseDateDiff(args[1], true);
            ess.runTaskAsynchronously(new SendAll(Console.getInstance(), FormatUtil.replaceFormat(getFinalArg(args, 2)), dateDiff));
            sender.sendTl("mailSent");
            return;
        } else if (args.length >= 2) {
            //allow sending from console without "send" argument, since it's the only thing the console can do
            final User u;
            try {
                u = getPlayer(server, args[0], true, true);
            } catch (final PlayerNotFoundException e) {
                throw new TranslatableException("playerNeverOnServer", args[0]);
            }
            u.sendMail(Console.getInstance(), FormatUtil.replaceFormat(getFinalArg(args, 1)));
            sender.sendTl("mailSent");
            return;
        }
        throw new NotEnoughArgumentsException();
    }

    private class SendAll implements Runnable {
        private final IMessageRecipient messageRecipient;
        private final String message;
        private final long dateDiff;

        SendAll(IMessageRecipient messageRecipient, String message, long dateDiff) {
            this.messageRecipient = messageRecipient;
            this.message = message;
            this.dateDiff = dateDiff;
        }

        @Override
        public void run() {
            for (UUID userid : ess.getUserMap().getAllUniqueUsers()) {
                final User user = ess.getUserMap().getUser(userid);
                if (user != null) {
                    user.sendMail(messageRecipient, message, dateDiff);
                }
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final List<String> options = Lists.newArrayList("read", "clear");
            if (user.isAuthorized("essentials.mail.send")) {
                options.add("send");
            }
            if (user.isAuthorized("essentials.mail.sendtemp")) {
                options.add("sendtemp");
            }
            if (user.isAuthorized("essentials.mail.sendall")) {
                options.add("sendall");
            }
            if (user.isAuthorized("essentials.mail.sendtempall")) {
                options.add("sendtempall");
            }
            return options;
        } else if (args.length == 2) {
            if ((args[0].equalsIgnoreCase("send") && user.isAuthorized("essentials.mail.send")) || (args[0].equalsIgnoreCase("sendtemp") && user.isAuthorized("essentials.mail.sendtemp"))) {
                return getPlayers(server, user);
            } else if (args[0].equalsIgnoreCase("sendtempall") && user.isAuthorized("essentials.mail.sendtempall")) {
                return COMMON_DATE_DIFFS;
            } else if (args[0].equalsIgnoreCase("read")) {
                final ArrayList<MailMessage> mail = user.getMailMessages();
                final int pages = mail != null ? (mail.size() / 9 + (mail.size() % 9 > 0 ? 1 : 0)) : 0;
                if (pages == 0) {
                    return Lists.newArrayList("0");
                } else {
                    final List<String> options = Lists.newArrayList("1");
                    if (pages > 1) {
                        options.add(String.valueOf(pages));
                    }
                    return options;
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("sendtemp") && user.isAuthorized("essentials.mail.sendtemp")) {
            return COMMON_DATE_DIFFS;
        }
        return Collections.emptyList();
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("send", "sendall", "sendtemp", "sendtempall");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("send") || args[0].equalsIgnoreCase("sendtemp")) {
                return getPlayers(server, sender);
            } else if (args[0].equalsIgnoreCase("sendtempall")) {
                return COMMON_DATE_DIFFS;
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("sendtemp")) {
            return COMMON_DATE_DIFFS;
        }
        return Collections.emptyList();
    }
}

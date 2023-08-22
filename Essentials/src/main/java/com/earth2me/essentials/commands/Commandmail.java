package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.textreader.TextPager;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.collect.Lists;
import net.essentialsx.api.v2.services.mail.MailMessage;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import static com.earth2me.essentials.I18n.tl;

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
                user.sendMessage(tl("noMail"));
                throw new NoChargeException();
            }

            final SimpleTextInput input = new SimpleTextInput();
            final ListIterator<MailMessage> iterator = mail.listIterator();
            while (iterator.hasNext()) {
                final MailMessage mailObj = iterator.next();
                if (mailObj.isExpired()) {
                    iterator.remove();
                    continue;
                }
                input.addLine(ess.getMail().getMailLine(mailObj));
                iterator.set(new MailMessage(true, mailObj.isLegacy(), mailObj.getSenderUsername(),
                        mailObj.getSenderUUID(), mailObj.getTimeSent(), mailObj.getTimeExpire(), mailObj.getMessage()));
            }

            if (input.getLines().isEmpty()) {
                user.sendMessage(tl("noMail"));
                throw new NoChargeException();
            }

            final TextPager pager = new TextPager(input);
            pager.showPage(args.length > 1 ? args[1] : null, null, commandLabel + " " + args[0], user.getSource());

            user.sendMessage(tl("mailClear"));
            user.setMailList(mail);
            return;
        }
        if (args.length >= 3 && "send".equalsIgnoreCase(args[0])) {
            if (!user.isAuthorized("essentials.mail.send")) {
                throw new Exception(tl("noPerm", "essentials.mail.send"));
            }

            if (user.isMuted()) {
                final String dateDiff = user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : null;
                if (dateDiff == null) {
                    throw new Exception(user.hasMuteReason() ? tl("voiceSilencedReason", user.getMuteReason()) : tl("voiceSilenced"));
                }
                throw new Exception(user.hasMuteReason() ? tl("voiceSilencedReasonTime", dateDiff, user.getMuteReason()) : tl("voiceSilencedTime", dateDiff));
            }

            final User u;
            try {
                u = getPlayer(server, args[1], true, true);
            } catch (final PlayerNotFoundException e) {
                throw new Exception(tl("playerNeverOnServer", args[1]));
            }

            final String msg = FormatUtil.formatMessage(user, "essentials.mail", StringUtil.sanitizeString(FormatUtil.stripFormat(getFinalArg(args, 2))));
            if (msg.length() > 1000) {
                throw new Exception(tl("mailTooLong"));
            }

            if (!u.isIgnoredPlayer(user)) {
                if (Math.abs(System.currentTimeMillis() - timestamp) > 60000) {
                    timestamp = System.currentTimeMillis();
                    mailsPerMinute = 0;
                }
                mailsPerMinute++;
                if (mailsPerMinute > ess.getSettings().getMailsPerMinute()) {
                    throw new Exception(tl("mailDelay", ess.getSettings().getMailsPerMinute()));
                }
                u.sendMail(user, msg);
            }

            user.sendMessage(tl("mailSentTo", u.getDisplayName(), u.getName()));
            user.sendMessage(msg);
            return;
        }
        if (args.length >= 4 && "sendtemp".equalsIgnoreCase(args[0])) {
            if (!user.isAuthorized("essentials.mail.sendtemp")) {
                throw new Exception(tl("noPerm", "essentials.mail.sendtemp"));
            }

            if (user.isMuted()) {
                final String dateDiff = user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : null;
                if (dateDiff == null) {
                    throw new Exception(user.hasMuteReason() ? tl("voiceSilencedReason", user.getMuteReason()) : tl("voiceSilenced"));
                }
                throw new Exception(user.hasMuteReason() ? tl("voiceSilencedReasonTime", dateDiff, user.getMuteReason()) : tl("voiceSilencedTime", dateDiff));
            }

            final User u;
            try {
                u = getPlayer(server, args[1], true, true);
            } catch (final PlayerNotFoundException e) {
                throw new Exception(tl("playerNeverOnServer", args[1]));
            }

            final long dateDiff = DateUtil.parseDateDiff(args[2], true);

            final String msg = FormatUtil.formatMessage(user, "essentials.mail", StringUtil.sanitizeString(FormatUtil.stripFormat(getFinalArg(args, 3))));
            if (msg.length() > 1000) {
                throw new Exception(tl("mailTooLong"));
            }

            if (!u.isIgnoredPlayer(user)) {
                if (Math.abs(System.currentTimeMillis() - timestamp) > 60000) {
                    timestamp = System.currentTimeMillis();
                    mailsPerMinute = 0;
                }
                mailsPerMinute++;
                if (mailsPerMinute > ess.getSettings().getMailsPerMinute()) {
                    throw new Exception(tl("mailDelay", ess.getSettings().getMailsPerMinute()));
                }
                u.sendMail(user, msg, dateDiff);
            }

            user.sendMessage(tl("mailSentToExpire", u.getDisplayName(), DateUtil.formatDateDiff(dateDiff), u.getName()));
            user.sendMessage(msg);
            return;
        }
        if (args.length > 1 && "sendall".equalsIgnoreCase(args[0])) {
            if (!user.isAuthorized("essentials.mail.sendall")) {
                throw new Exception(tl("noPerm", "essentials.mail.sendall"));
            }
            ess.runTaskAsynchronously(new SendAll(user,
                    FormatUtil.formatMessage(user, "essentials.mail",
                            StringUtil.sanitizeString(FormatUtil.stripFormat(getFinalArg(args, 1)))), 0));
            user.sendMessage(tl("mailSent"));
            return;
        }
        if (args.length >= 3 && "sendtempall".equalsIgnoreCase(args[0])) {
            if (!user.isAuthorized("essentials.mail.sendtempall")) {
                throw new Exception(tl("noPerm", "essentials.mail.sendtempall"));
            }
            ess.runTaskAsynchronously(new SendAll(user,
                    FormatUtil.formatMessage(user, "essentials.mail",
                            StringUtil.sanitizeString(FormatUtil.stripFormat(getFinalArg(args, 2)))), DateUtil.parseDateDiff(args[1], true)));
            user.sendMessage(tl("mailSent"));
            return;
        }
        if (args.length >= 1 && "clear".equalsIgnoreCase(args[0])) {
            User mailUser = user;
            int toRemove = -1;
            if (args.length > 1) {
                if (NumberUtil.isPositiveInt(args[1])) {
                    toRemove = Integer.parseInt(args[1]);
                } else if (!user.isAuthorized("essentials.mail.clear.others")) {
                    throw new Exception(tl("noPerm", "essentials.mail.clear.others"));
                } else {
                    mailUser = getPlayer(ess.getServer(), user, args, 1, true);
                    if (args.length > 2 && NumberUtil.isPositiveInt(args[2])) {
                        toRemove = Integer.parseInt(args[2]);
                    }
                }
            }

            final ArrayList<MailMessage> mails = mailUser.getMailMessages();
            if (mails == null || mails.isEmpty()) {
                user.sendMessage(tl(mailUser == user ? "noMail" : "noMailOther", mailUser.getDisplayName()));
                throw new NoChargeException();
            }

            if (toRemove > 0) {
                if (toRemove > mails.size()) {
                    user.sendMessage(tl("mailClearIndex", mails.size()));
                    throw new NoChargeException();
                }
                mails.remove(toRemove - 1);
                mailUser.setMailList(mails);
            } else {
                mailUser.setMailList(null);
            }
            user.sendMessage(tl("mailCleared"));
            return;
        }
        if (args.length >= 1 && "clearall".equalsIgnoreCase(args[0])){
            if (!user.isAuthorized("essentials.mail.clearall")) {
                throw new Exception(tl("noPerm", "essentials.mail.clearall"));
            }

            ess.runTaskAsynchronously(new ClearAll());
            user.sendMessage(tl("mailClearedAll"));
            return;

        }
        throw new NotEnoughArgumentsException();
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length >= 1 && "read".equalsIgnoreCase(args[0])) {
            throw new Exception(tl("onlyPlayers", commandLabel + " read"));
        } else if (args.length > 1 && "clear".equalsIgnoreCase(args[0])) {
            final User mailUser = getPlayer(server, args[1], true, true);
            final int toRemove = args.length > 2 ? NumberUtil.isPositiveInt(args[2]) ? Integer.parseInt(args[2]) : -1 : -1;

            final ArrayList<MailMessage> mails = mailUser.getMailMessages();
            if (mails == null || mails.isEmpty()) {
                sender.sendMessage(tl("noMailOther", mailUser.getDisplayName()));
                throw new NoChargeException();
            }

            if (toRemove > 0) {
                if (toRemove > mails.size()) {
                    sender.sendMessage(tl("mailClearIndex", mails.size()));
                    throw new NoChargeException();
                }
                mails.remove(toRemove - 1);
                mailUser.setMailList(mails);
            } else {
                mailUser.setMailList(null);
            }
            sender.sendMessage(tl("mailCleared"));
            return;
        } else if (args.length >= 1 && "clearall".equalsIgnoreCase(args[0])){
            ess.runTaskAsynchronously(new ClearAll());
            sender.sendMessage(tl("mailClearedAll"));
            return;
        } else if (args.length >= 3 && "send".equalsIgnoreCase(args[0])) {
            final User u;
            try {
                u = getPlayer(server, args[1], true, true);
            } catch (final PlayerNotFoundException e) {
                throw new Exception(tl("playerNeverOnServer", args[1]));
            }
            u.sendMail(Console.getInstance(), FormatUtil.replaceFormat(getFinalArg(args, 2)));
            sender.sendMessage(tl("mailSent"));
            return;
        } else if (args.length >= 4 && "sendtemp".equalsIgnoreCase(args[0])) {
            final User u;
            try {
                u = getPlayer(server, args[1], true, true);
            } catch (final PlayerNotFoundException e) {
                throw new Exception(tl("playerNeverOnServer", args[1]));
            }
            final long dateDiff = DateUtil.parseDateDiff(args[2], true);
            u.sendMail(Console.getInstance(), FormatUtil.replaceFormat(getFinalArg(args, 3)), dateDiff);
            sender.sendMessage(tl("mailSent"));
            return;
        } else if (args.length >= 2 && "sendall".equalsIgnoreCase(args[0])) {
            ess.runTaskAsynchronously(new SendAll(Console.getInstance(), FormatUtil.replaceFormat(getFinalArg(args, 1)), 0));
            sender.sendMessage(tl("mailSent"));
            return;
        } else if (args.length >= 3 && "sendtempall".equalsIgnoreCase(args[0])) {
            final long dateDiff = DateUtil.parseDateDiff(args[1], true);
            ess.runTaskAsynchronously(new SendAll(Console.getInstance(), FormatUtil.replaceFormat(getFinalArg(args, 2)), dateDiff));
            sender.sendMessage(tl("mailSent"));
            return;
        } else if (args.length >= 2) {
            //allow sending from console without "send" argument, since it's the only thing the console can do
            final User u;
            try {
                u = getPlayer(server, args[0], true, true);
            } catch (final PlayerNotFoundException e) {
                throw new Exception(tl("playerNeverOnServer", args[0]));
            }
            u.sendMail(Console.getInstance(), FormatUtil.replaceFormat(getFinalArg(args, 1)));
            sender.sendMessage(tl("mailSent"));
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
            for (final UUID u : ess.getUsers().getAllUserUUIDs()) {
                final User user = ess.getUsers().loadUncachedUser(u);
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
            if (user.isAuthorized("essentials.mail.clearall")){
                options.add("clearall");
            }
            return options;
        } else if (args.length == 2) {
            if ((args[0].equalsIgnoreCase("send") && user.isAuthorized("essentials.mail.send")) || (args[0].equalsIgnoreCase("sendtemp") && user.isAuthorized("essentials.mail.sendtemp")) || ((args[0].equalsIgnoreCase("clear"))&& user.isAuthorized("essentials.mail.clear.others"))) {
                return getPlayers(server, user);
            } else if (args[0].equalsIgnoreCase("sendtempall") && user.isAuthorized("essentials.mail.sendtempall")) {
                return COMMON_DATE_DIFFS;
            } else if (args[0].equalsIgnoreCase("read")) {
                final ArrayList<MailMessage> mail = user.getMailMessages();
                final int pages = mail != null ? (mail.size() / 9 + (mail.size() % 9 > 0 ? 1 : 0)) : 0;
                if (pages == 0) {
                    return Lists.newArrayList("0");
                } else {
                    final List<String> options = new ArrayList<>();
                    for (int i = 0; i < pages; i++) {
                        options.add(String.valueOf(i + 1));
                    }
                    return options;
                }
            } else if (args[0].equalsIgnoreCase("clear")) {
                final ArrayList<MailMessage> mail = user.getMailMessages();
                // We show up to 9 mails on a page, we don't need to autocomplete more than that...
                if (mail.size() >= 9) {
                    return Lists.newArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9");
                } else {
                    final List<String> options = new ArrayList<>();
                    for (int i = 0; i < mail.size(); i++) {
                        options.add(String.valueOf(i + 1));
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
            return Lists.newArrayList("send", "sendall", "sendtemp", "sendtempall", "clearall", "clear");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("send") || args[0].equalsIgnoreCase("sendtemp") || args[0].equalsIgnoreCase("clear")) {
                return getPlayers(server, sender);
            } else if (args[0].equalsIgnoreCase("sendtempall")) {
                return COMMON_DATE_DIFFS;
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("sendtemp")) {
            return COMMON_DATE_DIFFS;
        }
        return Collections.emptyList();
    }

    private class ClearAll implements Runnable {
        @Override
        public void run() {
            for (UUID u : ess.getUsers().getAllUserUUIDs()) {
                final User user = ess.getUsers().loadUncachedUser(u);
                if (user != null) {
                    user.setMailList(null);
                }
            }
        }
    }
}

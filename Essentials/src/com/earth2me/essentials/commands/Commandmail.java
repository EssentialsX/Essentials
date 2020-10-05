package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.textreader.TextPager;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.collect.Lists;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;
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
            final List<String> mail = user.getMails();
            if (mail.isEmpty()) {
                user.sendMessage(tl("noMail"));
                throw new NoChargeException();
            }

            final IText input = new SimpleTextInput(mail);
            final TextPager pager = new TextPager(input);
            pager.showPage(args.length > 1 ? args[1] : null, null, commandLabel + " " + args[0], user.getSource());

            user.sendMessage(tl("mailClear"));
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

            final String mail = tl("mailFormat", user.getName(), FormatUtil.formatMessage(user, "essentials.mail", StringUtil.sanitizeString(FormatUtil.stripFormat(getFinalArg(args, 2)))));
            if (mail.length() > 1000) {
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
                u.addMail(tl("mailMessage", mail));
            }

            user.sendMessage(tl("mailSentTo", u.getDisplayName(), u.getName()));
            user.sendMessage(mail);
            return;
        }
        if (args.length > 1 && "sendall".equalsIgnoreCase(args[0])) {
            if (!user.isAuthorized("essentials.mail.sendall")) {
                throw new Exception(tl("noPerm", "essentials.mail.sendall"));
            }
            ess.runTaskAsynchronously(new SendAll(tl("mailFormat", user.getName(),
                FormatUtil.formatMessage(user, "essentials.mail", StringUtil.sanitizeString(FormatUtil.stripFormat(getFinalArg(args, 1)))))));
            user.sendMessage(tl("mailSent"));
            return;
        }
        if (args.length >= 1 && "clear".equalsIgnoreCase(args[0])) {
            if (user.getMails() == null || user.getMails().isEmpty()) {
                user.sendMessage(tl("noMail"));
                throw new NoChargeException();
            }

            user.setMails(null);
            user.sendMessage(tl("mailCleared"));
            return;
        }
        throw new NotEnoughArgumentsException();
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length >= 1 && "read".equalsIgnoreCase(args[0])) {
            throw new Exception(tl("onlyPlayers", commandLabel + " read"));
        } else if (args.length >= 1 && "clear".equalsIgnoreCase(args[0])) {
            throw new Exception(tl("onlyPlayers", commandLabel + " clear"));
        } else if (args.length >= 3 && "send".equalsIgnoreCase(args[0])) {
            final User u;
            try {
                u = getPlayer(server, args[1], true, true);
            } catch (final PlayerNotFoundException e) {
                throw new Exception(tl("playerNeverOnServer", args[1]));
            }
            u.addMail(tl("mailFormat", "Server", FormatUtil.replaceFormat(getFinalArg(args, 2))));
            sender.sendMessage(tl("mailSent"));
            return;
        } else if (args.length >= 2 && "sendall".equalsIgnoreCase(args[0])) {
            ess.runTaskAsynchronously(new SendAll(tl("mailFormat", "Server", FormatUtil.replaceFormat(getFinalArg(args, 1)))));
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
            u.addMail(tl("mailFormat", "Server", FormatUtil.replaceFormat(getFinalArg(args, 1))));
            sender.sendMessage(tl("mailSent"));
            return;
        }
        throw new NotEnoughArgumentsException();
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final List<String> options = Lists.newArrayList("read", "clear");
            if (user.isAuthorized("essentials.mail.send")) {
                options.add("send");
            }
            if (user.isAuthorized("essentials.mail.sendall")) {
                options.add("sendall");
            }
            return options;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("send") && user.isAuthorized("essentials.mail.send")) {
            return getPlayers(server, user);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("read")) {
            final List<String> mail = user.getMails();
            final int pages = mail.size() / 9 + (mail.size() % 9 > 0 ? 1 : 0);
            if (pages == 0) {
                return Lists.newArrayList("0");
            } else {
                final List<String> options = Lists.newArrayList("1");
                if (pages > 1) {
                    options.add(String.valueOf(pages));
                }
                return options;
            }
        } else if ((args.length > 2 && args[0].equalsIgnoreCase("send") && user.isAuthorized("essentials.mail.send")) || (args.length > 1 && args[0].equalsIgnoreCase("sendall") && user.isAuthorized("essentials.mail.sendall"))) {
            return null; // Use vanilla handler
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("send", "sendall");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            return getPlayers(server, sender);
        } else if ((args.length > 2 && args[0].equalsIgnoreCase("send")) || (args.length > 1 && args[0].equalsIgnoreCase("sendall"))) {
            return null; // Use vanilla handler
        } else {
            return Collections.emptyList();
        }
    }

    private class SendAll implements Runnable {
        final String message;

        SendAll(final String message) {
            this.message = message;
        }

        @Override
        public void run() {
            for (final UUID userid : ess.getUserMap().getAllUniqueUsers()) {
                final User user = ess.getUserMap().getUser(userid);
                if (user != null) {
                    user.addMail(message);
                }
            }
        }
    }
}

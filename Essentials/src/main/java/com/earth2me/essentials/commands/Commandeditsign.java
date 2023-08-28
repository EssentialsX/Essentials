package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.VersionUtil;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.HangingSign;
import org.bukkit.block.data.type.WallHangingSign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandeditsign extends EssentialsCommand {
    public Commandeditsign() {
        super("editsign");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0 || (args.length > 1 && !NumberUtil.isInt(args[1]))) {
            throw new NotEnoughArgumentsException();
        }

        final Block target = user.getTargetBlock(5); //5 is a good number
        if (!(target.getState() instanceof Sign)) {
            throw new Exception(tl("editsignCommandTarget"));
        }
        final ModifiableSign sign = wrapSign((Sign) target.getState(), user);
        try {
            if (args[0].equalsIgnoreCase("set") && args.length > 2) {
                final String[] existingLines = sign.getLines();
                final int line = Integer.parseInt(args[1]) - 1;
                final String text = FormatUtil.formatString(user, "essentials.editsign", getFinalArg(args, 2)).trim();
                if (ChatColor.stripColor(text).length() > 15 && !user.isAuthorized("essentials.editsign.unlimited")) {
                    throw new Exception(tl("editsignCommandLimit"));
                }
                existingLines[line] = text;
                if (callSignEvent(sign, user.getBase(), existingLines)) {
                    return;
                }

                user.sendMessage(tl("editsignCommandSetSuccess", line + 1, text));
            } else if (args[0].equalsIgnoreCase("clear")) {
                if (args.length == 1) {
                    final String[] existingLines = sign.getLines();
                    for (int i = 0; i < 4; i++) { // A whole one line of line savings!
                        existingLines[i] = "";
                    }

                    if (callSignEvent(sign, user.getBase(), existingLines)) {
                        return;
                    }

                    user.sendMessage(tl("editsignCommandClear"));
                } else {
                    final String[] existingLines = sign.getLines();
                    final int line = Integer.parseInt(args[1]) - 1;
                    existingLines[line] = "";

                    if (callSignEvent(sign, user.getBase(), existingLines)) {
                        return;
                    }

                    user.sendMessage(tl("editsignCommandClearLine", line + 1));
                }
            } else if (args[0].equalsIgnoreCase("copy")) {
                final int line = args.length == 1 ? -1 : Integer.parseInt(args[1]) - 1;

                if (line == -1) {
                    for (int i = 0; i < 4; i++) {
                        // We use unformat here to prevent players from copying signs with colors that they do not have permission to use.
                        user.getSignCopy().set(i, FormatUtil.unformatString(user, "essentials.editsign", sign.getLine(i)));
                    }
                    user.sendMessage(tl("editsignCopy", commandLabel));
                } else {
                    // We use unformat here to prevent players from copying signs with colors that they do not have permission to use.
                    user.getSignCopy().set(line, FormatUtil.unformatString(user, "essentials.editsign", sign.getLine(line)));
                    user.sendMessage(tl("editsignCopyLine", line + 1, commandLabel));
                }

            } else if (args[0].equalsIgnoreCase("paste")) {
                final int line = args.length == 1 ? -1 : Integer.parseInt(args[1]) - 1;

                final String[] existingLines = sign.getLines();
                if (line == -1) {
                    for (int i = 0; i < 4; i++) {
                        existingLines[i] = FormatUtil.formatString(user, "essentials.editsign", user.getSignCopy().get(i));
                    }
                    user.sendMessage(tl("editsignPaste", commandLabel));
                } else {
                    existingLines[line] = FormatUtil.formatString(user, "essentials.editsign", user.getSignCopy().get(line));
                    user.sendMessage(tl("editsignPasteLine", line + 1, commandLabel));
                }

                callSignEvent(sign, user.getBase(), existingLines);
            } else {
                throw new NotEnoughArgumentsException();
            }
        } catch (final IndexOutOfBoundsException e) {
            throw new Exception(tl("editsignCommandNoLine"), e);
        }
    }

    private boolean callSignEvent(final ModifiableSign sign, final Player player, final String[] lines) {
        final SignChangeEvent event;
        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_20_1_R01)) {
            if (sign.isWaxed() && !player.hasPermission("essentials.editsign.waxed.exempt")) {
                return true;
            }
            event = new SignChangeEvent(sign.getBlock(), player, lines, sign.isFront() ? Side.FRONT : Side.BACK);
        } else {
            //noinspection deprecation
            event = new SignChangeEvent(sign.getBlock(), player, lines);
        }

        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info("SignChangeEvent canceled for /editsign execution by " + player.getName());
            }
            return true;
        }

        for (int i = 0; i < 4; i++) {
            sign.setLine(i, lines[i]);
        }
        sign.update();
        return false;
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("set", "clear", "copy", "paste");
        } else if (args.length == 2) {
            return Lists.newArrayList("1", "2", "3", "4");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set") && NumberUtil.isPositiveInt(args[1])) {
            final int line = Integer.parseInt(args[1]);
            final Block target = user.getTargetBlock(5);
            if (target.getState() instanceof Sign && line <= 4) {
                final ModifiableSign sign = wrapSign((Sign) target.getState(), user);
                return Lists.newArrayList(FormatUtil.unformatString(user, "essentials.editsign", sign.getLine(line - 1)));
            }
            return Collections.emptyList();
        } else {
            return Collections.emptyList();
        }
    }

    private ModifiableSign wrapSign(final Sign sign, final User user) {
        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_20_1_R01)) {
            final Vector eyeLocLessSign = user.getBase().getEyeLocation().toVector().subtract(sign.getLocation().add(.5, .5, .5).toVector());
            final BlockData signBlockData = sign.getBlockData();

            final BlockFace signDirection;
            if (signBlockData instanceof org.bukkit.block.data.type.Sign) {
                signDirection = ((org.bukkit.block.data.type.Sign) signBlockData).getRotation();
            } else if (signBlockData instanceof WallSign) {
                signDirection = ((WallSign) signBlockData).getFacing();
            } else if (signBlockData instanceof HangingSign) {
                signDirection = ((HangingSign) signBlockData).getRotation();
            } else if (signBlockData instanceof WallHangingSign) {
                signDirection = ((WallHangingSign) signBlockData).getFacing();
            } else {
                throw new IllegalStateException("Unknown block data for sign: " + signBlockData.getClass());
            }

            final Side side = eyeLocLessSign.dot(signDirection.getDirection()) > 0 ? Side.FRONT : Side.BACK;

            return new ModifiableSign(sign) {
                @Override
                String[] getLines() {
                    return sign.getSide(side).getLines();
                }

                @Override
                String getLine(int line) {
                    return sign.getSide(side).getLine(line);
                }

                @Override
                void setLine(int line, String text) {
                    sign.getSide(side).setLine(line, text);
                }

                @Override
                boolean isFront() {
                    return side == Side.FRONT;
                }

                @Override
                boolean isWaxed() {
                    return sign.isWaxed();
                }
            };
        }
        return new ModifiableSign(sign) {
            @Override
            String[] getLines() {
                return sign.getLines();
            }

            @Override
            String getLine(int line) {
                return sign.getLine(line);
            }

            @Override
            void setLine(int line, String text) {
                sign.setLine(line, text);
            }

            @Override
            boolean isFront() {
                return true;
            }

            @Override
            boolean isWaxed() {
                return false;
            }
        };
    }

    private abstract static class ModifiableSign {
        protected final Sign sign;

        protected ModifiableSign(final Sign sign) {
            this.sign = sign;
        }

        abstract String getLine(int line);

        abstract String[] getLines();

        abstract void setLine(int line, String text);

        abstract boolean isFront();

        abstract boolean isWaxed();

        Block getBlock() {
            return sign.getBlock();
        }

        void update() {
            sign.update();
        }
    }
}

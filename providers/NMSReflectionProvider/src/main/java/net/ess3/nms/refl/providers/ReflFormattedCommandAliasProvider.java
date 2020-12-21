package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.FormattedCommandAliasProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.FormattedCommandAlias;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflFormattedCommandAliasProvider implements FormattedCommandAliasProvider {

    private final boolean paper;
    private final Field formatStringsField;
    private final Method buildCommandMethod;

    public ReflFormattedCommandAliasProvider(boolean paper) {
        this.paper = paper;
        Field formatStringsField = null;
        Method buildCommandMethod = null;
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends FormattedCommandAlias> formattedCommandAliasClass = (Class<? extends FormattedCommandAlias>) ReflUtil.getOBClass("command.FormattedCommandAlias");
            if (formattedCommandAliasClass != null) {
                formatStringsField = ReflUtil.getFieldCached(formattedCommandAliasClass, "formatStrings");
                if (paper) {
                    buildCommandMethod = ReflUtil.getMethodCached(formattedCommandAliasClass, "buildCommand", CommandSender.class, String.class, String[].class);
                } else {
                    buildCommandMethod = ReflUtil.getMethodCached(formattedCommandAliasClass, "buildCommand", String.class, String[].class);
                }
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            this.formatStringsField = formatStringsField;
            this.buildCommandMethod = buildCommandMethod;
        }
    }

    @Override
    public List<String> createCommands(FormattedCommandAlias command, CommandSender sender, String[] args) {
        final List<String> commands = new ArrayList<>();
        if (buildCommandMethod == null || formatStringsField == null) return commands;

        final String[] formatStrings;
        try {
            formatStrings = (String[]) formatStringsField.get(command);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
            return commands;
        }

        for (String formatString : formatStrings) {
            final String cmd;
            try {
                cmd = buildCommand(command, sender, formatString, args);
            } catch (Throwable th) {
                continue; // Ignore, let server handle this.
            }

            if (cmd == null) continue;
            commands.add(cmd.trim());
        }
        return commands;
    }

    private String buildCommand(FormattedCommandAlias command, CommandSender sender, String formatString, String[] args) {
        try {
            if (paper) {
                return (String) buildCommandMethod.invoke(command, sender, formatString, args);
            } else {
                return (String) buildCommandMethod.invoke(command, formatString, args);
            }
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

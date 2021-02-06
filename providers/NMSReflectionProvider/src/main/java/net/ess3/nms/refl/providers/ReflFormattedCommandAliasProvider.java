package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.FormattedCommandAliasProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.FormattedCommandAlias;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflFormattedCommandAliasProvider extends FormattedCommandAliasProvider {

    private final boolean paper;
    private final Field formatStringsField;
    private final Method buildCommandMethod;

    public ReflFormattedCommandAliasProvider(boolean paper) {
        this.paper = paper;

        final Class<? extends FormattedCommandAlias> formattedCommandAliasClass;
        Field formatStringsField = null;
        Method buildCommandMethod = null;
        try {
            formattedCommandAliasClass = FormattedCommandAlias.class;
            formatStringsField = ReflUtil.getFieldCached(formattedCommandAliasClass, "formatStrings");
            if (paper) {
                buildCommandMethod = ReflUtil.getMethodCached(formattedCommandAliasClass, "buildCommand", CommandSender.class, String.class, String[].class);
            } else {
                buildCommandMethod = ReflUtil.getMethodCached(formattedCommandAliasClass, "buildCommand", String.class, String[].class);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            this.formatStringsField = formatStringsField;
            this.buildCommandMethod = buildCommandMethod;
        }
    }

    @Override
    public String[] getFormatStrings(FormattedCommandAlias command) {
        try {
            return (String[]) formatStringsField.get(command);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex); // If this happens we have bigger problems...
        }
    }

    @Override
    public String buildCommand(FormattedCommandAlias command, CommandSender sender, String formatString, String[] args) {
        try {
            if (paper) {
                return (String) buildCommandMethod.invoke(command, sender, formatString, args);
            } else {
                return (String) buildCommandMethod.invoke(command, formatString, args);
            }
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex); // If this happens we have bigger problems...
        }
    }
}

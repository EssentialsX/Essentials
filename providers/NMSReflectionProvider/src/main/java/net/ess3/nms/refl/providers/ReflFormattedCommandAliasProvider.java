package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.FormattedCommandAliasProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.FormattedCommandAlias;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflFormattedCommandAliasProvider extends FormattedCommandAliasProvider {

    protected final Class<? extends FormattedCommandAlias> formattedCommandAliasClass;
    private final Field formatStringsField;

    @SuppressWarnings("unchecked")
    public ReflFormattedCommandAliasProvider() {
        Class<? extends FormattedCommandAlias> formattedCommandAliasClass = null;
        Field formatStringsField = null;
        try {
            formattedCommandAliasClass = FormattedCommandAlias.class;
            formatStringsField = ReflUtil.getFieldCached(formattedCommandAliasClass, "formatStrings");
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            this.formattedCommandAliasClass = formattedCommandAliasClass;
            this.formatStringsField = formatStringsField;
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
            final Method buildCommandMethod = ReflUtil.getMethodCached(formattedCommandAliasClass, "buildCommand", String.class, String[].class);
            if (buildCommandMethod == null) throw new ReflectiveOperationException("Method FormattedCommandAlias#buildCommand() not found");
            return (String) buildCommandMethod.invoke(command, formatString, args);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex); // If this happens we have bigger problems...
        }
    }
}

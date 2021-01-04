package net.ess3.provider.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.nms.refl.providers.ReflFormattedCommandAliasProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.FormattedCommandAlias;

import java.lang.reflect.Method;

public class PaperReflFormattedCommandAliasProvider extends ReflFormattedCommandAliasProvider {

    @Override
    public String buildCommand(FormattedCommandAlias command, CommandSender sender, String formatString, String[] args) {
        try {
            final Method buildCommandMethod = ReflUtil.getMethodCached(formattedCommandAliasClass, "buildCommand", CommandSender.class, String.class, String[].class);
            if (buildCommandMethod == null) throw new ReflectiveOperationException("Method FormattedCommandAlias#buildCommand() not found");
            return (String) buildCommandMethod.invoke(command, sender, formatString, args);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex); // If this happens we have bigger problems...
        }
    }
}

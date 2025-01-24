package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.FormattedCommandAliasProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.command.CommandSender;
import org.bukkit.command.FormattedCommandAlias;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@ProviderData(description = "Reflection Formatted Command Alias Provider")
public class ReflFormattedCommandAliasProvider implements FormattedCommandAliasProvider {
    private final boolean senderArg;
    private final Field formatStringsField;
    private final MethodHandle buildCommandMethodHandle;

    public ReflFormattedCommandAliasProvider() {
        boolean senderArg = true;
        final Class<? extends FormattedCommandAlias> formattedCommandAliasClass;
        Field formatStringsField = null;
        MethodHandle buildCommandMethodHandle = null;
        try {
            formattedCommandAliasClass = FormattedCommandAlias.class;
            formatStringsField = ReflUtil.getFieldCached(formattedCommandAliasClass, "formatStrings");

            Method buildCommandMethod = ReflUtil.getMethodCached(formattedCommandAliasClass, "buildCommand", CommandSender.class, String.class, String[].class);
            if (buildCommandMethod == null) {
                senderArg = false;
                buildCommandMethod = ReflUtil.getMethodCached(formattedCommandAliasClass, "buildCommand", String.class, String[].class);
            }

            if (buildCommandMethod == null) {
                throw new NoSuchMethodException("Could not find buildCommand method in FormattedCommandAlias");
            }

            buildCommandMethod.setAccessible(true);
            buildCommandMethodHandle = MethodHandles.lookup().unreflect(buildCommandMethod);
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            this.formatStringsField = formatStringsField;
            this.buildCommandMethodHandle = buildCommandMethodHandle;
            this.senderArg = senderArg;
        }
    }

    @Override
    public List<String> createCommands(FormattedCommandAlias command, CommandSender sender, String[] args) {
        final List<String> commands = new ArrayList<>();
        for (String formatString : getFormatStrings(command)) {
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
            if (senderArg) {
                return (String) buildCommandMethodHandle.invoke(command, sender, formatString, args);
            } else {
                return (String) buildCommandMethodHandle.invoke(command, formatString, args);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex); // If this happens we have bigger problems...
        }
    }

}

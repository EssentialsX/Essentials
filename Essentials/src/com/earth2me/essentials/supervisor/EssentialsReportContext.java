package com.earth2me.essentials.supervisor;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import com.supaham.supervisor.bukkit.SupervisorPlugin;
import com.supaham.supervisor.report.AbstractReportContextEntry;
import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportSpecifications;
import com.supaham.supervisor.report.ReportSpecifications.ReportLevel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.Nonnull;

public class EssentialsReportContext extends ReportContext {

    private static final Method getNamesMethod;
    
    private final Essentials ess;
    
    static {
        try {
            getNamesMethod = UserMap.class.getDeclaredMethod("getNames");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void load(Essentials ess) {
        SupervisorPlugin.get().registerContext(ess, new EssentialsReportContext(ess));
    }

    public EssentialsReportContext(Essentials ess) {
        super("essentialsx", "EssentialsX", "1");
        this.ess = ess;
    }

    @Override
    public ReportContextEntry createEntry(@Nonnull ReportSpecifications specs) {
        return new EssentialsContext(this, specs);
    }

    private final class EssentialsContext extends AbstractReportContextEntry {

        public EssentialsContext(@Nonnull ReportContext parentContext, @Nonnull ReportSpecifications reportSpecifications) {
            super(parentContext, reportSpecifications);
        }

        @Override
        public void run() {
            append("users_count", ess.getUserMap().getUniqueUsers());
            uuidMapCount();

            if (getReportLevel() >= ReportLevel.NORMAL) {
                userdata();
            }

            if (getReportLevel() > ReportLevel.BRIEF) {
                config();
            }
        }

        private void uuidMapCount() {
            try {
                getNamesMethod.setAccessible(true);
                append("uuidmap_count", ((Map) getNamesMethod.invoke(ess.getUserMap())).size());
                getNamesMethod.setAccessible(false);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        private void userdata() {
            for (User user : ess.getOnlineUsers()) {
                File file = user.getConfig().getFile();
                try {
                    createPlainTextFile("userdata/" + file.getName(), user.getName() + " data").appendFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void config() {
            try {
                createPlainTextFile("config.yml", "Essentials Configuration file").appendFile(new File(ess.getDataFolder(), "config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

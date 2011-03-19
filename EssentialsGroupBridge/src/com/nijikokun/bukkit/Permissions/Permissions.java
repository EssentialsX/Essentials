package com.nijikokun.bukkit.Permissions;

import com.nijiko.Misc;
import com.nijiko.configuration.DefaultConfiguration;
import com.nijiko.permissions.PermissionHandler;
import java.util.logging.Logger;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.NijikoPermissionsProxy;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Permissions extends JavaPlugin {

    private class Listener extends PlayerListener {

        private Permissions plugin;

        public Listener(Permissions plugin) {
            //compiled code
            throw new RuntimeException("Compiled Code");
        }

        public void onPlayerCommand(PlayerChatEvent event) {
            //compiled code
            throw new RuntimeException("Compiled Code");
        }
    }
    public static final Logger log = Logger.getLogger("Fake Permissions");
    public static String name = "Permissions";
    public static String codename = "Hacked Permissions by AnjoCaido";
    public static String version = "2.0";
    public static PermissionHandler Security = null;
    public static Misc Misc = new Misc();
    public static Server Server;
    private Listener Listener = null;
    private DefaultConfiguration config = null;
    private GroupManager groupManager;

    @Override
    public void onDisable() {
        //compiled code
        //throw new RuntimeException("Compiled Code");
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("Fake " + pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
    }

    @Override
    public void onEnable() {
        Server = this.getServer();
        PluginDescriptionFile pdfFile = this.getDescription();

        if (Security == null) {//make sure we have only one instance
            Plugin p = (Plugin)(this.getServer() == null ? new GroupManager() : this.getServer().getPluginManager().getPlugin("GroupManager"));
            if (p != null) {
                if (!p.isEnabled()) {
					if (this.getServer() == null) {
						p.onEnable();
					} else {
						this.getServer().getPluginManager().enablePlugin(p);
					}
                }
                GroupManager gm = (GroupManager) p;
                groupManager = gm;
                Security = new NijikoPermissionsProxy(gm);
            } else {
                System.err.println("OOOPS! Fake " + pdfFile.getName() + " version " + pdfFile.getVersion() + " couldn't find GroupManager!");
                this.getPluginLoader().disablePlugin(this);
            }
        }
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
		if (pdfFile != null)
			System.out.println("Fake " + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    private void registerEvents() {
        //compiled code
        //throw new RuntimeException("Compiled Code");
    }

    public PermissionHandler getHandler() {
        //compiled code
        //throw new RuntimeException("Compiled Code");
        //System.out.println("Alguem chamou o handler");
        checkEnable();
        return Security;
    }

    public void setupPermissions() {
        checkEnable();
    }

    private void checkEnable() {
        if (!this.isEnabled() && Security == null && this.getServer() != null) {
            this.getServer().getPluginManager().enablePlugin(this);
        }
    }
}

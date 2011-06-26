package com.nijikokun.bukkit.Permissions;

import com.nijiko.Misc;
import com.nijiko.configuration.DefaultConfiguration;
import com.nijiko.permissions.PermissionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.NijikoPermissionsProxy;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
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
		if (!Thread.currentThread().getStackTrace()[5].getMethodName().equals("loadPlugin")) {
			Logger.getLogger("Minecraft").log(Level.SEVERE, "Another plugin is trying to enable Permissions manually. Don't do this! It's probably "
					+ Thread.currentThread().getStackTrace()[5].getClassName());
		}
        Server = this.getServer();
        PluginDescriptionFile pdfFile = this.getDescription();

        if (Security == null) {//make sure we have only one instance
			Security = new NijikoPermissionsProxy(null);	
		}
		
		Plugin p = (this.getServer() == null) ? null : this.getServer().getPluginManager().getPlugin("GroupManager");
		if (p != null) {
			if (p.isEnabled()) {
				setGM(p);
			} else {
				if (this.getServer() != null) {
					this.getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, new ServerListener() {

						@Override
						public void onPluginEnable(PluginEnableEvent event)
						{
							if (event.getPlugin().getDescription().getName().equals("GroupManager")) {
								Permissions.this.setGM(event.getPlugin());
							}
						}
						
					}, Priority.Normal, this);
				}
			}
		} else {
			System.err.println("OOOPS! Fake " + pdfFile.getName() + " version " + pdfFile.getVersion() + " couldn't find GroupManager!");
			this.getPluginLoader().disablePlugin(this);
		}
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
		if (pdfFile != null)
			System.out.println("Fake " + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    private void registerEvents() {
        //compiled code
        //throw new RuntimeException("Compiled Code");
    }
	
	private void setGM(Plugin p) {
		groupManager = (GroupManager)p;
		((NijikoPermissionsProxy)Security).setGM(p);
	}

    public PermissionHandler getHandler() {
        //compiled code
        //throw new RuntimeException("Compiled Code");
        //System.out.println("Alguem chamou o handler");
		if (Security == null)
		{
			Security = new NijikoPermissionsProxy(null);
		}
        //checkEnable();
        return Security;
    }

    public void setupPermissions() {
		if (Security == null)
		{
			Security = new NijikoPermissionsProxy(null);
		}
        //checkEnable();
    }

    private void checkEnable() {
        if (!this.isEnabled() && Security == null && this.getServer() != null) {
            this.getServer().getPluginManager().enablePlugin(this);
        }
    }
}

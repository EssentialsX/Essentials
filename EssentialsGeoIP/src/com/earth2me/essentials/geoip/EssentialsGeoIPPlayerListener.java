package com.earth2me.essentials.geoip;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import com.earth2me.essentials.User;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.exception.*;
import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import com.ice.tar.TarInputStream;
import com.ice.tar.TarEntry;

import static com.earth2me.essentials.I18n.tl;


public class EssentialsGeoIPPlayerListener implements Listener, IConf {
    DatabaseReader mmreader = null; // initialize maxmind geoip2 reader
    private static final Logger logger = Logger.getLogger("Minecraft");
    File databaseFile;
    File dataFolder;
    final EssentialsConf config;
    private final transient IEssentials ess;

    public EssentialsGeoIPPlayerListener(File dataFolder, IEssentials ess) {
        this.ess = ess;
        this.dataFolder = dataFolder;
        this.config = new EssentialsConf(new File(dataFolder, "config.yml"));
        config.setTemplateName("/config.yml", EssentialsGeoIP.class);
        reloadConfig();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        ess.runTaskAsynchronously(new Runnable() {
            @Override
            public void run() {
                delayedJoin(event.getPlayer());
            }
        });
    }

    public void delayedJoin(Player player) {
        User u = ess.getUser(player);
        if (u.isAuthorized("essentials.geoip.hide") || player.getAddress() == null) {
            return;
        }
        InetAddress address = player.getAddress().getAddress();
        StringBuilder sb = new StringBuilder();
        String locale = ess.getI18n().getCurrentLocale().toString().replace('_', '-'); // get locale setting from Essentials
            try {
                if (config.getBoolean("database.show-cities", false)) {
                    CityResponse response = mmreader.city(address);
                    if (response == null) {
                        return;
                    }
                    String city;
                    String region;
                    String country;
                    if (config.getBoolean("enable-locale", true)) {
                        // Get geolocation based on locale. If not avaliable in specific language, get the default one.
                        city = ((city=response.getCity().getNames().get(locale))!=null) ? city : response.getCity().getName();
                        region = ((region=response.getMostSpecificSubdivision().getNames().get(locale))!=null) ? region : response.getMostSpecificSubdivision().getName();
                        country = ((country=response.getCountry().getNames().get(locale))!=null) ? country : response.getCountry().getName();
                    } else {
                        // Get geolocation regarding locale setting.
                        city = response.getCity().getName();
                        region = response.getMostSpecificSubdivision().getName();
                        country = response.getCountry().getName();
                    }
                    if (city != null) {
                        sb.append(city).append(", ");
                    }
                    if (region != null) {
                        sb.append(region).append(", ");
                    }
                    sb.append(country);
                } else {
                    CountryResponse response = mmreader.country(address);
                    sb.append(response.getCountry().getNames().get(locale));
                }
            } catch (AddressNotFoundException ex) {
                // GeoIP2 API forced this when address not found in their DB. jar will not complied without this.
                // TODO: Maybe, we can set a new custom msg about addr-not-found in messages.properties.
                logger.log(Level.INFO, tl("cantReadGeoIpDB") + " " + ex.getLocalizedMessage());
                //logger.log(Level.INFO, tl("cantReadGeoIpDB") + " " + ex.getMessage());
            } catch (IOException | GeoIp2Exception ex) {
                // GeoIP2 API forced this when address not found in their DB. jar will not complied without this.
                logger.log(Level.SEVERE, tl("cantReadGeoIpDB") + " " + ex.getLocalizedMessage());
                //logger.log(Level.SEVERE, tl("cantReadGeoIpDB") + " " + ex.getMessage());
            }
        if (config.getBoolean("show-on-whois", true)) {
            u.setGeoLocation(sb.toString());
        }
        if (config.getBoolean("show-on-login", true) && !u.isHidden()) {
            for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
                User user = ess.getUser(onlinePlayer);
                if (user.isAuthorized("essentials.geoip.show")) {
                    user.sendMessage(tl("geoipJoinFormat", u.getDisplayName(), sb.toString()));
                }
            }
        }
    }

    @Override
    public final void reloadConfig() {
        config.load();

        if (config.getBoolean("database.show-cities", false)) {
            databaseFile = new File(dataFolder, "Geo2-City.mmdb");
        } else {
            databaseFile = new File(dataFolder, "Geo2-Country.mmdb");
        }
        if (!databaseFile.exists()) {
            if (config.getBoolean("database.download-if-missing", true)) {
                downloadDatabase();
            } else {
                logger.log(Level.SEVERE, tl("cantFindGeoIpDB"));
                return;
            }
        } else if (config.getBoolean("database.update.enable", true)) {
            // try to update expired mmdb files
            long diff = new Date().getTime() - databaseFile.lastModified();
            if (diff/24/3600/1000>config.getLong("database.update.by-every-x-days")) {
                downloadDatabase();
            }
        }
        try {
            mmreader = new DatabaseReader.Builder(databaseFile).build();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, tl("cantReadGeoIpDB"), ex);
        }
    }

    private void downloadDatabase() {
        try {
            String url;
            if (config.getBoolean("database.show-cities", false)) {
                url = config.getString("database.download-url-city");
            } else {
                url = config.getString("database.download-url");
            }
            if (url == null || url.isEmpty()) {
                logger.log(Level.SEVERE, tl("geoIpUrlEmpty"));
                return;
            }
            logger.log(Level.INFO, tl("downloadingGeoIp"));
            URL downloadUrl = new URL(url);
            URLConnection conn = downloadUrl.openConnection();
            conn.setConnectTimeout(10000);
            conn.connect();
            InputStream input = conn.getInputStream();
            OutputStream output = new FileOutputStream(databaseFile);
            byte[] buffer = new byte[2048];
            if (url.endsWith(".gz")) {
                input = new GZIPInputStream(input);
                if (url.endsWith(".tar.gz")) {
                    // The new GeoIP2 uses tar.gz to pack the db file along with some other txt. So it makes things a bit complicated here.
                    String filename;
                    TarInputStream tarInputStream = new TarInputStream(input);
                    TarEntry entry;
                    while ((entry = (TarEntry) tarInputStream.getNextEntry()) != null) {
                        if (!entry.isDirectory()) {
                            filename = entry.getName();
                            if (filename.substring(filename.length() - 5).equalsIgnoreCase(".mmdb")) {
                                input = tarInputStream;
                                break;
                            }
                        }
                    }
                }
            }
            int length = input.read(buffer);
            while (length >= 0) {
                output.write(buffer, 0, length);
                length = input.read(buffer);
            }
            output.close();
            input.close();
        } catch (MalformedURLException ex) {
            logger.log(Level.SEVERE, tl("geoIpUrlInvalid"), ex);
            return;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, tl("connectionFailed"), ex);
        }
    }
}

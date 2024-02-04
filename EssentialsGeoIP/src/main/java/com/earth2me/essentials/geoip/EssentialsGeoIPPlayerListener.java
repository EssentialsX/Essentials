package com.earth2me.essentials.geoip;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.User;
import com.earth2me.essentials.config.EssentialsConfiguration;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.CommonPlaceholders;
import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import net.essentialsx.api.v2.events.AsyncUserDataLoadEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

import static com.earth2me.essentials.I18n.tlLiteral;

public class EssentialsGeoIPPlayerListener implements Listener, IConf {
    private final File dataFolder;
    private final EssentialsConfiguration config;
    private final transient IEssentials ess;
    private final transient EssentialsGeoIP essGeo;
    private DatabaseReader mmreader = null; // initialize maxmind geoip2 reader
    private File databaseFile;

    EssentialsGeoIPPlayerListener(final File dataFolder, final IEssentials ess, final EssentialsGeoIP essGeo) {
        this.ess = ess;
        this.essGeo = essGeo;
        this.dataFolder = dataFolder;
        this.config = new EssentialsConfiguration(new File(dataFolder, "config.yml"), "/config.yml", EssentialsGeoIP.class);
        reloadConfig();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDataLoad(final AsyncUserDataLoadEvent event) {
        // Already async so just jump right into it.
        delayedJoin(event.getUser().getBase());
    }

    private void delayedJoin(final Player player) {
        final User u = ess.getUser(player);
        if (u.isAuthorized("essentials.geoip.hide") || player.getAddress() == null) {
            return;
        }
        final InetAddress address = player.getAddress().getAddress();
        final StringBuilder sb = new StringBuilder();

        if (mmreader == null) {
            essGeo.getLogger().log(Level.WARNING, AdventureUtil.miniToLegacy(tlLiteral("geoIpErrorOnJoin", u.getName())));
            return;
        }

        try {
            if (config.getBoolean("database.show-cities", false)) {
                final CityResponse response = mmreader.city(address);
                if (response == null) {
                    return;
                }
                final String city;
                final String region;
                final String country;
                city = response.getCity().getName();
                region = response.getMostSpecificSubdivision().getName();
                country = response.getCountry().getName();
                if (city != null) {
                    sb.append(city).append(", ");
                }
                if (region != null) {
                    sb.append(region).append(", ");
                }
                sb.append(country);
            } else {
                final CountryResponse response = mmreader.country(address);
                sb.append(response.getCountry().getName());
            }
        } catch (final AddressNotFoundException ex) {

            if (checkIfLocal(address)) {
                for (final Player online : player.getServer().getOnlinePlayers()) {
                    final User user = ess.getUser(online);
                    if (user.isAuthorized("essentials.geoip.show")) {
                        user.sendTl("geoipCantFind", CommonPlaceholders.displayName((IUser) u));
                    }
                }
                return;
            }
            // GeoIP2 API forced this when address not found in their DB. jar will not complied without this.
            // TODO: Maybe, we can set a new custom msg about addr-not-found in messages.properties.
            essGeo.getLogger().log(Level.INFO, AdventureUtil.miniToLegacy(tlLiteral("cantReadGeoIpDB")) + " " + ex.getLocalizedMessage());
        } catch (final IOException | GeoIp2Exception ex) {
            // GeoIP2 API forced this when address not found in their DB. jar will not complied without this.
            essGeo.getLogger().log(Level.SEVERE, AdventureUtil.miniToLegacy(tlLiteral("cantReadGeoIpDB")) + " " + ex.getLocalizedMessage());
        }
        if (config.getBoolean("show-on-whois", true)) {
            u.setGeoLocation(sb.toString());
        }
        if (config.getBoolean("show-on-login", true) && !u.isHidden()) {
            for (final Player onlinePlayer : player.getServer().getOnlinePlayers()) {
                final User user = ess.getUser(onlinePlayer);
                if (user.isAuthorized("essentials.geoip.show")) {
                    user.sendTl("geoipJoinFormat", CommonPlaceholders.displayName((IUser) u), sb.toString());
                }
            }
        }
    }

    @Override
    public final void reloadConfig() {
        config.load();

        // detect and update the old config.yml. migrate from legacy GeoIP to GeoIP2.
        if (!config.hasProperty("enable-locale")) {
            config.setProperty("database.download-url", "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-Country&license_key={LICENSEKEY}&suffix=tar.gz");
            config.setProperty("database.download-url-city", "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key={LICENSEKEY}&suffix=tar.gz");
            config.setProperty("database.license-key", "");
            config.setProperty("database.update.enable", true);
            config.setProperty("database.update.by-every-x-days", 30);
            config.setProperty("enable-locale", true);
            config.save();
            // delete old GeoIP.dat fiiles
            final File oldDatFile = new File(dataFolder, "GeoIP.dat");
            final File oldDatFileCity = new File(dataFolder, "GeoIP-City.dat");
            oldDatFile.delete();
            oldDatFileCity.delete();
        }

        if (config.getBoolean("database.show-cities", false)) {
            databaseFile = new File(dataFolder, "GeoIP2-City.mmdb");
        } else {
            databaseFile = new File(dataFolder, "GeoIP2-Country.mmdb");
        }
        if (!databaseFile.exists()) {
            if (config.getBoolean("database.download-if-missing", true)) {
                downloadDatabase();
            } else {
                essGeo.getLogger().log(Level.SEVERE, AdventureUtil.miniToLegacy(tlLiteral("cantFindGeoIpDB")));
                return;
            }
        } else if (config.getBoolean("database.update.enable", true)) {
            // try to update expired mmdb files
            final long diff = new Date().getTime() - databaseFile.lastModified();
            if (diff / 24 / 3600 / 1000 > config.getLong("database.update.by-every-x-days", 30)) {
                downloadDatabase();
            }
        }
        try {
            // locale setting
            if (config.getBoolean("enable-locale", false)) {
                // Get geolocation based on Essentials' locale. If the locale is not avaliable, use "en".
                String locale = ess.getI18n().getCurrentLocale().toString().replace('_', '-');
                // This fixes an inconsistency where Essentials uses "zh" but MaxMind expects "zh-CN".
                if ("zh".equalsIgnoreCase(locale)) {
                    locale = "zh-CN";
                }
                mmreader = new DatabaseReader.Builder(databaseFile).locales(Arrays.asList(locale, "en")).build();
            } else {
                mmreader = new DatabaseReader.Builder(databaseFile).build();
            }
        } catch (final IOException ex) {
            essGeo.getLogger().log(Level.SEVERE, AdventureUtil.miniToLegacy(tlLiteral("cantReadGeoIpDB")), ex);
        }
    }

    private void downloadDatabase() {
        try {
            String url;
            if (config.getBoolean("database.show-cities", false)) {
                url = config.getString("database.download-url-city", null);
            } else {
                url = config.getString("database.download-url", null);
            }
            if (url == null || url.isEmpty()) {
                essGeo.getLogger().log(Level.SEVERE, AdventureUtil.miniToLegacy(tlLiteral("geoIpUrlEmpty")));
                return;
            }
            final String licenseKey = config.getString("database.license-key", "");
            if (licenseKey == null || licenseKey.isEmpty()) {
                essGeo.getLogger().log(Level.SEVERE, AdventureUtil.miniToLegacy(tlLiteral("geoIpLicenseMissing")));
                return;
            }
            url = url.replace("{LICENSEKEY}", licenseKey);
            essGeo.getLogger().log(Level.INFO, AdventureUtil.miniToLegacy(tlLiteral("downloadingGeoIp")));
            final URL downloadUrl = new URL(url);
            final URLConnection conn = downloadUrl.openConnection();
            conn.setConnectTimeout(10000);
            conn.connect();
            InputStream input = conn.getInputStream();
            final OutputStream output = new FileOutputStream(databaseFile);
            final byte[] buffer = new byte[2048];
            if (url.contains("gz")) {
                input = new GZIPInputStream(input);
                if (url.contains("tar.gz")) {
                    // The new GeoIP2 uses tar.gz to pack the db file along with some other txt. So it makes things a bit complicated here.
                    String filename;
                    final TarInputStream tarInputStream = new TarInputStream(input);
                    TarEntry entry;
                    while ((entry = tarInputStream.getNextEntry()) != null) {
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
        } catch (final MalformedURLException ex) {
            essGeo.getLogger().log(Level.SEVERE, AdventureUtil.miniToLegacy(tlLiteral("geoIpUrlInvalid")), ex);
        } catch (final IOException ex) {
            essGeo.getLogger().log(Level.SEVERE, AdventureUtil.miniToLegacy(tlLiteral("connectionFailed")), ex);
        }
    }

    private boolean checkIfLocal(final InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress()) {
            return true;
        }

        // Double checks if address is defined on any interface
        try {
            return NetworkInterface.getByInetAddress(address) != null;
        } catch (final SocketException e) {
            return false;
        }
    }
}

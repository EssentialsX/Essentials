package com.earth2me.essentials.config.holders;

import com.earth2me.essentials.config.annotations.DeleteIfIncomplete;
import com.earth2me.essentials.config.annotations.DeleteOnEmpty;
import com.earth2me.essentials.config.entities.CommandCooldown;
import com.earth2me.essentials.config.entities.LazyLocation;
import net.essentialsx.api.v2.services.mail.MailMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@ConfigSerializable
public class UserConfigHolder {
    private @MonotonicNonNull BigDecimal money;

    public BigDecimal money() {
        return money;
    }

    public void money(final BigDecimal value) {
        this.money = value;
    }

    @DeleteOnEmpty
    private @MonotonicNonNull Map<String, LazyLocation> homes;

    public Map<String, LazyLocation> homes() {
        if (this.homes == null) {
            this.homes = new HashMap<>();
        }
        return this.homes;
    }

    public void homes(final Map<String, LazyLocation> value) {
        this.homes = value;
    }

    private @Nullable String nickname;

    public String nickname() {
        return nickname;
    }

    public void nickname(final String value) {
        this.nickname = value;
    }

    @DeleteOnEmpty
    private @MonotonicNonNull Set<Material> unlimited;

    public Set<Material> unlimited() {
        if (this.unlimited == null) {
            this.unlimited = new HashSet<>();
        }
        return this.unlimited;
    }

    @DeleteOnEmpty
    private @MonotonicNonNull Map<String, List<String>> powertools;

    public Map<String, List<String>> powertools() {
        if (this.powertools == null) {
            this.powertools = new HashMap<>();
        }
        return this.powertools;
    }

    private @MonotonicNonNull LazyLocation lastlocation;

    public LazyLocation lastLocation() {
        return this.lastlocation;
    }

    public void lastLocation(final Location value) {
        if (value == null || value.getWorld() == null) {
            return;
        }
        this.lastlocation = LazyLocation.fromLocation(value);
    }

    private @MonotonicNonNull LazyLocation logoutlocation;

    public LazyLocation logoutLocation() {
        return this.logoutlocation;
    }

    public void logoutLocation(final Location value) {
        if (value == null || value.getWorld() == null) {
            return;
        }
        this.logoutlocation = LazyLocation.fromLocation(value);
    }

    private @Nullable String jail;

    public String jail() {
        return this.jail;
    }

    public void jail(final String value) {
        this.jail = value;
    }

    @DeleteOnEmpty
    private @MonotonicNonNull ArrayList<MailMessage> mail;

    public ArrayList<MailMessage> mail() {
        if (this.mail == null) {
            this.mail = new ArrayList<>();
        }
        return this.mail;
    }

    public void mail(final ArrayList<MailMessage> value) {
        this.mail = value;
    }

    private boolean teleportenabled = true;

    public boolean teleportEnabled() {
        return this.teleportenabled;
    }

    public void teleportEnabled(final boolean value) {
        this.teleportenabled = value;
    }

    private boolean teleportauto = false;

    public boolean teleportAuto() {
        return this.teleportauto;
    }

    public void teleportAuto(final boolean value) {
        this.teleportauto = value;
    }

    @DeleteOnEmpty
    private @MonotonicNonNull List<UUID> ignore;

    public List<UUID> ignore() {
        if (this.ignore == null) {
            this.ignore = new ArrayList<>();
        }
        return this.ignore;
    }

    public void ignore(final List<UUID> value) {
        this.ignore = value;
    }

    private boolean godmode = false;

    public boolean godMode() {
        return this.godmode;
    }

    public void godMode(final boolean value) {
        this.godmode = value;
    }

    private boolean muted = false;

    public boolean muted() {
        return this.muted;
    }

    public void muted(final boolean value) {
        this.muted = value;
    }

    private @Nullable String muteReason;

    public String muteReason() {
        return this.muteReason;
    }

    public void muteReason(final String value) {
        this.muteReason = value;
    }

    private boolean jailed = false;

    public boolean jailed() {
        return this.jailed;
    }

    public void jailed(final boolean value) {
        this.jailed = value;
    }

    private @NonNull String ipAddress = "";

    public String ipAddress() {
        return this.ipAddress;
    }

    public void ipAddress(final String value) {
        this.ipAddress = value;
    }

    private boolean afk = false;

    public boolean afk() {
        return this.afk;
    }

    public void afk(final boolean value) {
        this.afk = value;
    }

    @DeleteOnEmpty
    private @Nullable String geolocation;

    public String geolocation() {
        return this.geolocation;
    }

    public void geolocation(final String value) {
        this.geolocation = value;
    }

    private boolean socialspy = false;

    public boolean socialSpy() {
        return this.socialspy;
    }

    public void socialSpy(final boolean value) {
        this.socialspy = value;
    }

    private boolean npc = false;

    public boolean npc() {
        return this.npc;
    }

    public void npc(final boolean value) {
        this.npc = value;
    }

    private @MonotonicNonNull String lastAccountName;

    public String lastAccountName() {
        return this.lastAccountName;
    }

    public void lastAccountName(final String value) {
        this.lastAccountName = value;
    }

    private @MonotonicNonNull String npcName;

    public String npcName() {
        return this.npcName;
    }

    public void npcName(final String value) {
        this.npcName = value;
    }

    private boolean powertoolsenabled = true;

    public boolean powerToolsEnabled() {
        return this.powertoolsenabled;
    }

    public void powerToolsEnabled(final boolean value) {
        this.powertoolsenabled = value;
    }

    private boolean acceptingPay = true;

    public boolean acceptingPay() {
        return this.acceptingPay;
    }

    public void acceptingPay(final boolean value) {
        this.acceptingPay = value;
    }

    private @Nullable Boolean confirmPay;

    public Boolean confirmPay() {
        return this.confirmPay;
    }

    public void confirmPay(final Boolean value) {
        this.confirmPay = value;
    }

    private @Nullable Boolean confirmClear;

    public Boolean confirmClear() {
        return this.confirmClear;
    }

    public void confirmClear(final Boolean value) {
        this.confirmClear = value;
    }

    private @Nullable Boolean lastMessageReplyRecipient;

    public Boolean lastMessageReplyRecipient() {
        return this.lastMessageReplyRecipient;
    }

    public void lastMessageReplyRecipient(final Boolean value) {
        this.lastMessageReplyRecipient = value;
    }

    private boolean baltopExempt = false;

    public boolean baltopExempt() {
        return this.baltopExempt;
    }

    public void baltopExempt(final boolean value) {
        this.baltopExempt = value;
    }

    private @MonotonicNonNull Boolean shouting;

    public Boolean shouting() {
        return shouting;
    }

    public void shouting(final Boolean value) {
        this.shouting = value;
    }

    @DeleteOnEmpty
    private @MonotonicNonNull List<String> pastUsernames;

    public List<String> pastUsernames() {
        if (this.pastUsernames == null) {
            this.pastUsernames = new ArrayList<>();
        }
        return this.pastUsernames;
    }

    public void pastUsernames(List<String> value) {
        this.pastUsernames = value;
    }

    private @NonNull Timestamps timestamps = new Timestamps();

    public Timestamps timestamps() {
        return this.timestamps;
    }

    @ConfigSerializable
    public static class Timestamps {
        private long lastteleport = 0L;

        public long lastTeleport() {
            return this.lastteleport;
        }

        public void lastTeleport(final long value) {
            this.lastteleport = value;
        }

        private long lastheal = 0L;

        public long lastHeal() {
            return this.lastheal;
        }

        public void lastHeal(final long value) {
            this.lastheal = value;
        }

        private long mute = 0L;

        public long mute() {
            return this.mute;
        }

        public void mute(final long value) {
            this.mute = value;
        }

        private long jail = 0L;

        public long jail() {
            return this.jail;
        }

        public void jail(final long value) {
            this.jail = value;
        }

        private long onlinejail = 0L;

        public long onlineJail() {
            return this.onlinejail;
        }

        public void onlineJail(final long value) {
            this.onlinejail = value;
        }

        private long logout = 0L;

        public long logout() {
            return this.logout;
        }

        public void logout(final long value) {
            this.logout = value;
        }

        private long login = 0L;

        public long login() {
            return this.login;
        }

        public void login(final long value) {
            this.login = value;
        }

        @DeleteOnEmpty
        private @MonotonicNonNull Map<String, Long> kits;

        public Map<String, Long> kits() {
            if (this.kits == null) {
                this.kits = new HashMap<>();
            }
            return this.kits;
        }

        public void kits(final Map<String, Long> value) {
            this.kits = value;
        }

        @DeleteOnEmpty
        @DeleteIfIncomplete
        private @MonotonicNonNull List<CommandCooldown> commandCooldowns;

        public List<CommandCooldown> commandCooldowns() {
            if (this.commandCooldowns == null) {
                this.commandCooldowns = new ArrayList<>();
            }
            return this.commandCooldowns;
        }

        public void commandCooldowns(final List<CommandCooldown> value) {
            this.commandCooldowns = value;
        }
    }
}

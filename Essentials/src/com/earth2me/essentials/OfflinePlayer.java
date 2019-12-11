package com.earth2me.essentials;

import net.ess3.nms.refl.ReflUtil;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.*;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;

public class OfflinePlayer implements Player {
    private final transient Server server;
    private transient Location location = new Location(null, 0, 0, 0, 0, 0);
    private transient World world;
    private transient org.bukkit.OfflinePlayer base;
    private boolean allowFlight = false;
    private boolean isFlying = false;
    private String name = null;

    public OfflinePlayer(final UUID uuid, final Server server) {
        this.server = server;
        this.world = server.getWorlds().get(0);
        this.base = server.getOfflinePlayer(uuid);
        this.name = base.getName();
    }

    public OfflinePlayer(final String name, final Server server) {
        this.server = server;
        this.world = server.getWorlds().get(0);
        this.base = server.getOfflinePlayer(name);
        this.name = name;
    }

    @Override
    public void sendMessage(final String string) {
    }

    @Override
    public String getDisplayName() {
        return base.getName();
    }

    @Override
    public void setDisplayName(String string) {
    }

    @Override
    public void setCompassTarget(Location lctn) {
    }

    @Override
    public InetSocketAddress getAddress() {
        return null;
    }

    @Override
    public void kickPlayer(String string) {
    }

    @Override
    public PlayerInventory getInventory() {
        return null;
    }

    @Override
    public ItemStack getItemInHand() {
        return null;
    }

    @Override
    public void setItemInHand(ItemStack is) {
    }

    @Override
    public double getHealth() {
        return 0;
    }

    @Override
    public void setHealth(double d) {
    }

    @Override
    public double getAbsorptionAmount() {
        return 0;
    }

    @Override
    public void setAbsorptionAmount(double v) {
    }

    @Override
    public boolean isInsideVehicle() {
        return false;
    }

    @Override
    public boolean leaveVehicle() {
        return false;
    }

    @Override
    public Vehicle getVehicle() {
        return null;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        location.setYaw(yaw);
        location.setPitch(pitch);
    }

    public void setLocation(Location loc) {
        location = loc;
        world = loc.getWorld();
    }

    public void teleportTo(Location lctn) {
        location = lctn;
        world = location.getWorld();
    }

    public void teleportTo(Entity entity) {
        teleportTo(entity.getLocation());
    }

    @Override
    public int getEntityId() {
        return -1;
    }

    public BlockFace getFacing() {
        return null;
    }

    @Override
    public Pose getPose() {
        return null;
    }

    @Override
    public boolean performCommand(String string) {
        return false;
    }

    @Override
    public int getRemainingAir() {
        return 0;
    }

    @Override
    public void setRemainingAir(int i) {
    }

    @Override
    public int getMaximumAir() {
        return 0;
    }

    @Override
    public void setMaximumAir(int i) {
    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public void setSneaking(boolean bln) {
    }

    @Override
    public void updateInventory() {
    }

    @Override
    public void chat(String string) {
    }

    @Override
    public double getEyeHeight() {
        return 0D;
    }

    @Override
    public double getEyeHeight(boolean bln) {
        return 0D;
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> mat, int i) {
        return Collections.emptyList();
    }

    @Override
    public Block getTargetBlock(Set<Material> mat, int i) {
        return null;
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> mat, int i) {
        return Collections.emptyList();
    }

    @Override
    public Block getTargetBlockExact(int maxDistance) {
        return null;
    }

    @Override
    public Block getTargetBlockExact(int maxDistance, FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    @Override
    public RayTraceResult rayTraceBlocks(double maxDistance) {
        return null;
    }

    @Override
    public RayTraceResult rayTraceBlocks(double maxDistance, FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    @Override
    public int getFireTicks() {
        return 0;
    }

    @Override
    public int getMaxFireTicks() {
        return 0;
    }

    @Override
    public void setFireTicks(int i) {
    }

    @Override
    public void remove() {
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void setPersistent(boolean persistent) {
    }

    public Vector getMomentum() {
        return getVelocity();
    }

    public void setMomentum(Vector vector) {
    }

    @Override
    public void setVelocity(Vector vector) {
    }

    @Override
    public Vector getVelocity() {
        return new Vector(0, 0, 0);
    }

    @Override
    public void damage(double d) {
    }

    @Override
    public void damage(double d, Entity entity) {
    }

    @Override
    public Location getEyeLocation() {
        return null;
    }

    @Override
    public void sendRawMessage(String string) {
    }

    @Override
    public Location getCompassTarget() {
        return null;
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return 0;
    }

    @Override
    public void setMaximumNoDamageTicks(int i) {
    }

    @Override
    public double getLastDamage() {
        return 0D;
    }

    @Override
    public void setLastDamage(double d) {
    }

    @Override
    public int getNoDamageTicks() {
        return 0;
    }

    @Override
    public void setNoDamageTicks(int i) {
    }

    @Override
    public boolean teleport(Location lctn) {
        return false;
    }

    @Override
    public boolean teleport(Entity entity) {
        return false;
    }

    @Override
    public Entity getPassenger() {
        return null;
    }

    @Override
    public boolean setPassenger(Entity entity) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean eject() {
        return false;
    }

    @Override
    public void saveData() {
    }

    @Override
    public void loadData() {
    }

    @Override
    public boolean isSleeping() {
        return false;
    }

    @Override
    public int getSleepTicks() {
        return 0;
    }

    @Override
    public List<Entity> getNearbyEntities(double d, double d1, double d2) {
        return Collections.emptyList();
    }

    @Override
    public boolean isDead() {
        return true;
    }

    @Override
    public float getFallDistance() {
        return 0F;
    }

    @Override
    public void setFallDistance(float f) {
    }

    @Override
    public void setSleepingIgnored(boolean bln) {
    }

    @Override
    public boolean isSleepingIgnored() {
        return true;
    }

    @Override
    public void incrementStatistic(Statistic ststc) {
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
    }

    @Override
    public void incrementStatistic(Statistic ststc, int i) {
    }

    @Override
    public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
    }

    @Override
    public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException {
    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public void incrementStatistic(Statistic ststc, Material mtrl) {
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public void incrementStatistic(Statistic ststc, Material mtrl, int i) {
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException {
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int i) {
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int i) {
    }

    @Override
    public void playNote(Location lctn, byte b, byte b1) {
    }

    @Override
    public void sendBlockChange(Location lctn, Material mtrl, byte b) {
    }

    @Override
    public void sendBlockChange(Location loc, BlockData block) {

    }

    @Override
    public void setLastDamageCause(EntityDamageEvent ede) {
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return null;
    }

    @Override
    public void playEffect(Location lctn, Effect effect, int i) {
    }

    @Override
    public boolean sendChunkChange(Location lctn, int i, int i1, int i2, byte[] bytes) {
        return true;
    }

    @Override
    public void playNote(Location lctn, Instrument i, Note note) {
    }

    @Override
    public void setPlayerTime(long l, boolean bln) {
    }

    @Override
    public long getPlayerTime() {
        return 0;
    }

    @Override
    public long getPlayerTimeOffset() {
        return 0;
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return false;
    }

    @Override
    public void resetPlayerTime() {
    }

    @Override
    public boolean isPermissionSet(String string) {
        return false;
    }

    @Override
    public boolean isPermissionSet(Permission prmsn) {
        return false;
    }

    @Override
    public boolean hasPermission(String string) {
        return false;
    }

    @Override
    public boolean hasPermission(Permission prmsn) {
        return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln, int i) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment pa) {
    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Collections.emptySet();
    }

    @Override
    public void sendMap(MapView mv) {
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.SURVIVAL;
    }

    @Override
    public void setGameMode(GameMode gm) {
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public void setLevel(int i) {
    }

    @Override
    public int getTotalExperience() {
        return 0;
    }

    @Override
    public void setTotalExperience(int i) {
    }

    @Override
    public float getExhaustion() {
        return 0F;
    }

    @Override
    public void setExhaustion(float f) {
    }

    @Override
    public float getSaturation() {
        return 0F;
    }

    @Override
    public void setSaturation(float f) {
    }

    @Override
    public int getFoodLevel() {
        return 0;
    }

    @Override
    public void setFoodLevel(int i) {
    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public void setSprinting(boolean bln) {
    }

    @Override
    public void setPlayerListName(String name) {
    }

    @Override
    public String getPlayerListHeader() {
        return null;
    }

    @Override
    public String getPlayerListFooter() {
        return null;
    }

    @Override
    public void setPlayerListHeader(String header) {

    }

    @Override
    public void setPlayerListFooter(String footer) {

    }

    @Override
    public void setPlayerListHeaderFooter(String header, String footer) {

    }

    @Override
    public String getPlayerListName() {
        return name;
    }

    @Override
    public int getTicksLived() {
        return 0;
    }

    @Override
    public void setTicksLived(int i) {
    }

    @Override
    public double getMaxHealth() {
        return 0D;
    }

    @Override
    public void giveExp(int i) {
    }

    @Override
    public float getExp() {
        return 0F;
    }

    @Override
    public void setExp(float f) {
    }

    @Override
    public boolean teleport(Location lctn, TeleportCause tc) {
        return false;
    }

    @Override
    public boolean teleport(Entity entity, TeleportCause tc) {
        return false;
    }

    @Override
    public Player getKiller() {
        return null;
    }

    void setName(final String name) {
        this.name = base.getName();
        if (this.name == null) {
            this.name = name;
        }
    }

    @Override
    public void sendPluginMessage(Plugin plugin, String string, byte[] bytes) {
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return Collections.emptySet();
    }

    @Override
    public void setAllowFlight(boolean bln) {
        allowFlight = bln;
    }

    @Override
    public boolean getAllowFlight() {
        return allowFlight;
    }

    @Override
    public void setBedSpawnLocation(Location lctn) {
    }

    @Override
    public void setBedSpawnLocation(Location lctn, boolean force) {
    }

    @Override
    public boolean sleep(Location location, boolean force) {
        return false;
    }

    @Override
    public void wakeup(boolean setSpawnLocation) {

    }

    @Override
    public Location getBedLocation() {
        return null;
    }

    @Override
    public void playEffect(EntityEffect ee) {
    }

    @Override
    public void hidePlayer(Player player) {
    }

    @Override
    public void hidePlayer(Plugin plugin, Player player) {

    }

    @Override
    public void showPlayer(Player player) {
    }

    @Override
    public void showPlayer(Plugin plugin, Player player) {

    }

    @Override
    public boolean canSee(Player player) {
        return false;
    }

    @Override
    public boolean addPotionEffect(PotionEffect pe) {
        return false;
    }

    @Override
    public boolean addPotionEffect(PotionEffect pe, boolean bln) {
        return false;
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> clctn) {
        return false;
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType pet) {
        return false;
    }

    @Override
    public PotionEffect getPotionEffect(PotionEffectType potionEffectType) {
        return null;
    }

    @Override
    public void removePotionEffect(PotionEffectType pet) {
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return Collections.emptyList();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) {
        return null;
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public <T> void playEffect(Location lctn, Effect effect, T t) {
    }

    @Override
    public boolean setWindowProperty(Property prprt, int i) {
        return false;
    }

    @Override
    public InventoryView getOpenInventory() {
        return null;
    }

    @Override
    public InventoryView openInventory(Inventory invntr) {
        return null;
    }

    @Override
    public InventoryView openWorkbench(Location lctn, boolean bln) {
        return null;
    }

    @Override
    public InventoryView openEnchanting(Location lctn, boolean bln) {
        return null;
    }

    @Override
    public void openInventory(InventoryView iv) {
    }

    @Override
    public InventoryView openMerchant(Villager villager, boolean b) {
        return null;
    }

    @Override
    public InventoryView openMerchant(Merchant merchant, boolean b) {
        return null;
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public ItemStack getItemOnCursor() {
        return null;
    }

    @Override
    public void setItemOnCursor(ItemStack is) {
    }

    @Override
    public void setMetadata(String string, MetadataValue mv) {
    }

    @Override
    public List<MetadataValue> getMetadata(String string) {
        return Collections.emptyList();
    }

    @Override
    public boolean hasMetadata(String string) {
        return false;
    }

    @Override
    public void removeMetadata(String string, Plugin plugin) {
    }

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public void acceptConversationInput(String string) {
    }

    @Override
    public boolean beginConversation(Conversation c) {
        return false;
    }

    @Override
    public void abandonConversation(Conversation c) {
    }

    @Override
    public void sendMessage(String[] strings) {
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isHandRaised() {
        return false;
    }

    @Override
    public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1) {
    }

    @Override
    public boolean isFlying() {
        return isFlying;
    }

    @Override
    public void setFlying(boolean arg0) {
        isFlying = arg0;
    }

    @Override
    public int getExpToLevel() {
        return 0;
    }

    @Override
    public boolean discoverRecipe(NamespacedKey recipe) {
        return false;
    }

    @Override
    public int discoverRecipes(Collection<NamespacedKey> recipes) {
        return 0;
    }

    @Override
    public boolean undiscoverRecipe(NamespacedKey recipe) {
        return false;
    }

    @Override
    public int undiscoverRecipes(Collection<NamespacedKey> recipes) {
        return 0;
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void setFlySpeed(float value) throws IllegalArgumentException {
    }

    @Override
    public void setWalkSpeed(float value) throws IllegalArgumentException {
    }

    @Override
    public float getFlySpeed() {
        return 0.1f;
    }

    @Override
    public float getWalkSpeed() {
        return 0.2f;
    }

    @Override
    public Inventory getEnderChest() {
        return null;
    }

    @Override
    public MainHand getMainHand() {
        return MainHand.RIGHT;
    }

    @Override
    public void playSound(Location arg0, Sound arg1, float arg2, float arg3) {
    }

    @Override
    public void giveExpLevels(int i) {
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return false;
    }

    @Override
    public void setRemoveWhenFarAway(boolean bln) {
    }

    @Override
    public EntityEquipment getEquipment() {
        return null;
    }

    @Override
    public void setCanPickupItems(boolean bln) {
    }

    @Override
    public boolean getCanPickupItems() {
        return false;
    }

    @Override
    public Location getLocation(Location lctn) {
        return lctn;
    }

    @Override
    public void setTexturePack(String string) {
    }

    @Override
    public void setResourcePack(String s) {
    }

    @Override
    public void setMaxHealth(double i) {
    }

    @Override
    public void resetMaxHealth() {
    }

    @Override
    public void setCustomName(String string) {
    }

    @Override
    public String getCustomName() {
        return null;
    }

    @Override
    public void setCustomNameVisible(boolean bln) {
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public void setGlowing(boolean b) {
    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public void setInvulnerable(boolean b) {
    }

    @Override
    public boolean isInvulnerable() {
        return false;
    }

    @Override
    public boolean isSilent() {
        return false;
    }

    @Override
    public void setSilent(boolean b) {
    }

    @Override
    public boolean hasGravity() {
        return false;
    }

    @Override
    public void setGravity(boolean b) {
    }

    @Override
    public int getPortalCooldown() {
        return 0;
    }

    @Override
    public void setPortalCooldown(int i) {

    }

    @Override
    public Set<String> getScoreboardTags() {
        return null;
    }

    @Override
    public boolean addScoreboardTag(String s) {
        return false;
    }

    @Override
    public boolean removeScoreboardTag(String s) {
        return false;
    }

    @Override
    public void setPlayerWeather(WeatherType arg0) {
    }

    @Override
    public WeatherType getPlayerWeather() {
        return null; // per player weather, null means default anyways
    }

    @Override
    public void resetPlayerWeather() {
    }

    @Override
    public boolean isOnGround() {
        return false;
    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public void setScoreboard(Scoreboard scrbrd) throws IllegalArgumentException, IllegalStateException {
    }

    @Override
    public void playSound(Location arg0, String arg1, float arg2, float arg3) {
    }

    @Override
    public void playSound(Location location, Sound sound, SoundCategory soundCategory, float v, float v1) {
        
    }

    @Override
    public void playSound(Location location, String s, SoundCategory soundCategory, float v, float v1) {

    }

    @Override
    public void stopSound(Sound sound) {
    }

    @Override
    public void stopSound(String s) {
    }

    @Override
    public void stopSound(Sound sound, SoundCategory soundCategory) {

    }

    @Override
    public void stopSound(String s, SoundCategory soundCategory) {

    }

    @Override
    public boolean isHealthScaled() {
        return false;
    }

    @Override
    public void setHealthScaled(boolean arg0) {
    }

    @Override
    public void setHealthScale(double arg0) throws IllegalArgumentException {
        
    }

    @Override
    public double getHealthScale() {
        return 0D;
    }

    @Override
    public void setSpectatorTarget(Entity entity) {
    }

    @Override
    public boolean isLeashed() {
        return false;
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return null;
    }

    @Override
    public boolean setLeashHolder(Entity arg0) {
        return false;
    }

    @Override
    public boolean isGliding() {
        return false;
    }

    @Override
    public void setGliding(boolean b) {
    }

    @Override
    public boolean isSwimming() {
        return false;
    }

    @Override
    public void setSwimming(boolean swimming) {

    }

    @Override
    public boolean isRiptiding() {
        return false;
    }

    @Override
    public void setAI(boolean b) {
    }

    @Override
    public boolean hasAI() {
        return false;
    }

    @Override
    public void setCollidable(boolean b) {
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public <T> T getMemory(MemoryKey<T> memoryKey) {
        return null;
    }

    @Override
    public <T> void setMemory(MemoryKey<T> memoryKey, T t) {

    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> type, Vector vector) {
        return null;
    }

    @Override
    public void sendSignChange(Location arg0, String[] arg1) throws IllegalArgumentException {
    }

    @Override
    public void sendSignChange(Location loc, String[] lines, DyeColor dyeColor) throws IllegalArgumentException {

    }

    @Override
    public Location getBedSpawnLocation() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUniqueId() {
        return base.getUniqueId();
    }

    @Override
    public boolean isOp() {
        return base.isOp();
    }

    @Override
    public void setOp(boolean value) {
        base.setOp(value);
    }

    @Override
    public boolean isOnline() {
        return base.isOnline();
    }

    @Override
    public boolean isBanned() {
        if (base.getName() == null && getName() != null) {
            return server.getBanList(BanList.Type.NAME).isBanned(getName());
        }
        return base.isBanned();
    }

    // Removed in 1.12, retain for backwards compatibility.
    @Deprecated
    public void setBanned(boolean banned) {
        if (ReflUtil.getNmsVersionObject().isHigherThanOrEqualTo(ReflUtil.V1_12_R1)) {
            throw new UnsupportedOperationException("Cannot call setBanned on MC 1.12 and higher.");
        }
        if (base.getName() == null && getName() != null) {
            if (banned) {
                server.getBanList(BanList.Type.NAME).addBan(getName(), null, null, null);
            } else {
                server.getBanList(BanList.Type.NAME).pardon(getName());
            }
        }
        try {
            Method method = base.getClass().getDeclaredMethod("setBanned", boolean.class);
            method.invoke(banned);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // This will never happen in a normal CraftBukkit pre-1.12 instance
            e.printStackTrace();
        }
    }

    @Override
    public boolean isWhitelisted() {
        return base.isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean value) {
        base.setWhitelisted(value);
    }

    @Override
    public Player getPlayer() {
        return base.getPlayer();
    }

    @Override
    public long getFirstPlayed() {
        return base.getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return base.getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return base.hasPlayedBefore();
    }

    @Override
    public Map<String, Object> serialize() {
        return base.serialize();
    }

    @Override
    public Entity getSpectatorTarget() {
        return null;
    }

    @Override
    public void resetTitle() {
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int i) {

    }

    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i) {

    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, T t) {

    }

    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, T t) {

    }

    @Override
    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2) {

    }

    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5) {

    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, T t) {

    }

    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, T t) {

    }

    @Override
    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3) {

    }

    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6) {

    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3, T t) {

    }

    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, T t) {

    }

    @Override
    public void sendTitle(String title, String subtitle) {
        
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        
    }

    @Override
    public AttributeInstance getAttribute(Attribute attribute) {
        // GetAttribute is nullable as per CraftAttributeMap. This might need to be
        // improved to support cases where dummy null instances should be returned.
        return null;
    }

    @Override
    public void setResourcePack(String s, byte[] bytes) {
    }

    @Override
    public AdvancementProgress getAdvancementProgress(Advancement advancement) {
        return null;
    }

    @Override
    public int getClientViewDistance() {
        return 0;
    }

    @Override
    public String getLocale() {
        return null;
    }

    @Override
    public boolean hasCooldown(Material material) {
        return false;
    }

    @Override
    public int getCooldown(Material material) {
        return 0;
    }

    @Override
    public void setCooldown(Material material, int i) {
    }

    @Override
    public Entity getShoulderEntityLeft() {
        return null;
    }

    @Override
    public void setShoulderEntityLeft(Entity entity) {
    }

    @Override
    public Entity getShoulderEntityRight() {
        return null;
    }

    @Override
    public void setShoulderEntityRight(Entity entity) {
    }

    @Override
    public double getHeight() {
        return 0;
    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public List<Entity> getPassengers() {
        return null;
    }

    @Override
    public boolean addPassenger(Entity entity) {
        return false;
    }

    @Override
    public boolean removePassenger(Entity entity) {
        return false;
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return null;
    }

    @Override
	public void updateCommands() {
	}

    @Override
    public void openBook(ItemStack book) {
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return null;
    }
}

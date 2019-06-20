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

/**
 * <p>OfflinePlayer class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class OfflinePlayer implements Player {
    private final transient Server server;
    private transient Location location = new Location(null, 0, 0, 0, 0, 0);
    private transient World world;
    private transient org.bukkit.OfflinePlayer base;
    private boolean allowFlight = false;
    private boolean isFlying = false;
    private String name = null;

    /**
     * <p>Constructor for OfflinePlayer.</p>
     *
     * @param uuid a {@link java.util.UUID} object.
     * @param server a {@link org.bukkit.Server} object.
     */
    public OfflinePlayer(final UUID uuid, final Server server) {
        this.server = server;
        this.world = server.getWorlds().get(0);
        this.base = server.getOfflinePlayer(uuid);
        this.name = base.getName();
    }

    /**
     * <p>Constructor for OfflinePlayer.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param server a {@link org.bukkit.Server} object.
     */
    public OfflinePlayer(final String name, final Server server) {
        this.server = server;
        this.world = server.getWorlds().get(0);
        this.base = server.getOfflinePlayer(name);
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(final String string) {
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return base.getName();
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayName(String string) {
    }

    /** {@inheritDoc} */
    @Override
    public void setCompassTarget(Location lctn) {
    }

    /** {@inheritDoc} */
    @Override
    public InetSocketAddress getAddress() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void kickPlayer(String string) {
    }

    /** {@inheritDoc} */
    @Override
    public PlayerInventory getInventory() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack getItemInHand() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setItemInHand(ItemStack is) {
    }

    /** {@inheritDoc} */
    @Override
    public double getHealth() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setHealth(double d) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInsideVehicle() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean leaveVehicle() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Vehicle getVehicle() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Location getLocation() {
        return location;
    }

    /** {@inheritDoc} */
    @Override
    public World getWorld() {
        return world;
    }

    /** {@inheritDoc} */
    @Override
    public void setRotation(float yaw, float pitch) {
        location.setYaw(yaw);
        location.setPitch(pitch);
    }

    /**
     * <p>Setter for the field <code>location</code>.</p>
     *
     * @param loc a {@link org.bukkit.Location} object.
     */
    public void setLocation(Location loc) {
        location = loc;
        world = loc.getWorld();
    }

    /**
     * <p>teleportTo.</p>
     *
     * @param lctn a {@link org.bukkit.Location} object.
     */
    public void teleportTo(Location lctn) {
        location = lctn;
        world = location.getWorld();
    }

    /**
     * <p>teleportTo.</p>
     *
     * @param entity a {@link org.bukkit.entity.Entity} object.
     */
    public void teleportTo(Entity entity) {
        teleportTo(entity.getLocation());
    }

    /** {@inheritDoc} */
    @Override
    public int getEntityId() {
        return -1;
    }

    /**
     * <p>getFacing.</p>
     *
     * @return a {@link org.bukkit.block.BlockFace} object.
     */
    public BlockFace getFacing() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Pose getPose() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean performCommand(String string) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int getRemainingAir() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setRemainingAir(int i) {
    }

    /** {@inheritDoc} */
    @Override
    public int getMaximumAir() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setMaximumAir(int i) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSneaking() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setSneaking(boolean bln) {
    }

    /** {@inheritDoc} */
    @Override
    public void updateInventory() {
    }

    /** {@inheritDoc} */
    @Override
    public void chat(String string) {
    }

    /** {@inheritDoc} */
    @Override
    public double getEyeHeight() {
        return 0D;
    }

    /** {@inheritDoc} */
    @Override
    public double getEyeHeight(boolean bln) {
        return 0D;
    }

    /** {@inheritDoc} */
    @Override
    public List<Block> getLineOfSight(Set<Material> mat, int i) {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public Block getTargetBlock(Set<Material> mat, int i) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> mat, int i) {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public Block getTargetBlockExact(int maxDistance) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Block getTargetBlockExact(int maxDistance, FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public RayTraceResult rayTraceBlocks(double maxDistance) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public RayTraceResult rayTraceBlocks(double maxDistance, FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getFireTicks() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int getMaxFireTicks() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setFireTicks(int i) {
    }

    /** {@inheritDoc} */
    @Override
    public void remove() {
    }

    /** {@inheritDoc} */
    @Override
    public Server getServer() {
        return server;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPersistent() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setPersistent(boolean persistent) {
    }

    /**
     * <p>getMomentum.</p>
     *
     * @return a {@link org.bukkit.util.Vector} object.
     */
    public Vector getMomentum() {
        return getVelocity();
    }

    /**
     * <p>setMomentum.</p>
     *
     * @param vector a {@link org.bukkit.util.Vector} object.
     */
    public void setMomentum(Vector vector) {
    }

    /** {@inheritDoc} */
    @Override
    public void setVelocity(Vector vector) {
    }

    /** {@inheritDoc} */
    @Override
    public Vector getVelocity() {
        return new Vector(0, 0, 0);
    }

    /** {@inheritDoc} */
    @Override
    public void damage(double d) {
    }

    /** {@inheritDoc} */
    @Override
    public void damage(double d, Entity entity) {
    }

    /** {@inheritDoc} */
    @Override
    public Location getEyeLocation() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void sendRawMessage(String string) {
    }

    /** {@inheritDoc} */
    @Override
    public Location getCompassTarget() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getMaximumNoDamageTicks() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setMaximumNoDamageTicks(int i) {
    }

    /** {@inheritDoc} */
    @Override
    public double getLastDamage() {
        return 0D;
    }

    /** {@inheritDoc} */
    @Override
    public void setLastDamage(double d) {
    }

    /** {@inheritDoc} */
    @Override
    public int getNoDamageTicks() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setNoDamageTicks(int i) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean teleport(Location lctn) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean teleport(Entity entity) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Entity getPassenger() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setPassenger(Entity entity) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean eject() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void saveData() {
    }

    /** {@inheritDoc} */
    @Override
    public void loadData() {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSleeping() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int getSleepTicks() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public List<Entity> getNearbyEntities(double d, double d1, double d2) {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDead() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public float getFallDistance() {
        return 0F;
    }

    /** {@inheritDoc} */
    @Override
    public void setFallDistance(float f) {
    }

    /** {@inheritDoc} */
    @Override
    public void setSleepingIgnored(boolean bln) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSleepingIgnored() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void awardAchievement(Achievement a) {
    }

    /** {@inheritDoc} */
    @Override
    public void removeAchievement(Achievement achievement) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasAchievement(Achievement achievement) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void incrementStatistic(Statistic ststc) {
    }

    /** {@inheritDoc} */
    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public void incrementStatistic(Statistic ststc, int i) {
    }

    /** {@inheritDoc} */
    @Override
    public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void incrementStatistic(Statistic ststc, Material mtrl) {
    }

    /** {@inheritDoc} */
    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void incrementStatistic(Statistic ststc, Material mtrl, int i) {
    }

    /** {@inheritDoc} */
    @Override
    public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int i) {
    }

    /** {@inheritDoc} */
    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int i) {
    }

    /** {@inheritDoc} */
    @Override
    public void playNote(Location lctn, byte b, byte b1) {
    }

    /** {@inheritDoc} */
    @Override
    public void sendBlockChange(Location lctn, Material mtrl, byte b) {
    }

    /** {@inheritDoc} */
    @Override
    public void sendBlockChange(Location loc, BlockData block) {

    }

    /** {@inheritDoc} */
    @Override
    public void setLastDamageCause(EntityDamageEvent ede) {
    }

    /** {@inheritDoc} */
    @Override
    public EntityDamageEvent getLastDamageCause() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void playEffect(Location lctn, Effect effect, int i) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean sendChunkChange(Location lctn, int i, int i1, int i2, byte[] bytes) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void playNote(Location lctn, Instrument i, Note note) {
    }

    /** {@inheritDoc} */
    @Override
    public void setPlayerTime(long l, boolean bln) {
    }

    /** {@inheritDoc} */
    @Override
    public long getPlayerTime() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public long getPlayerTimeOffset() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPlayerTimeRelative() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void resetPlayerTime() {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPermissionSet(String string) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPermissionSet(Permission prmsn) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasPermission(String string) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasPermission(Permission prmsn) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln, int i) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void removeAttachment(PermissionAttachment pa) {
    }

    /** {@inheritDoc} */
    @Override
    public void recalculatePermissions() {
    }

    /** {@inheritDoc} */
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Collections.emptySet();
    }

    /** {@inheritDoc} */
    @Override
    public void sendMap(MapView mv) {
    }

    /** {@inheritDoc} */
    @Override
    public GameMode getGameMode() {
        return GameMode.SURVIVAL;
    }

    /** {@inheritDoc} */
    @Override
    public void setGameMode(GameMode gm) {
    }

    /** {@inheritDoc} */
    @Override
    public int getLevel() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setLevel(int i) {
    }

    /** {@inheritDoc} */
    @Override
    public int getTotalExperience() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setTotalExperience(int i) {
    }

    /** {@inheritDoc} */
    @Override
    public float getExhaustion() {
        return 0F;
    }

    /** {@inheritDoc} */
    @Override
    public void setExhaustion(float f) {
    }

    /** {@inheritDoc} */
    @Override
    public float getSaturation() {
        return 0F;
    }

    /** {@inheritDoc} */
    @Override
    public void setSaturation(float f) {
    }

    /** {@inheritDoc} */
    @Override
    public int getFoodLevel() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setFoodLevel(int i) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSprinting() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setSprinting(boolean bln) {
    }

    /** {@inheritDoc} */
    @Override
    public void setPlayerListName(String name) {
    }

    /** {@inheritDoc} */
    @Override
    public String getPlayerListHeader() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getPlayerListFooter() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setPlayerListHeader(String header) {

    }

    /** {@inheritDoc} */
    @Override
    public void setPlayerListFooter(String footer) {

    }

    /** {@inheritDoc} */
    @Override
    public void setPlayerListHeaderFooter(String header, String footer) {

    }

    /** {@inheritDoc} */
    @Override
    public String getPlayerListName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public int getTicksLived() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setTicksLived(int i) {
    }

    /** {@inheritDoc} */
    @Override
    public double getMaxHealth() {
        return 0D;
    }

    /** {@inheritDoc} */
    @Override
    public void giveExp(int i) {
    }

    /** {@inheritDoc} */
    @Override
    public float getExp() {
        return 0F;
    }

    /** {@inheritDoc} */
    @Override
    public void setExp(float f) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean teleport(Location lctn, TeleportCause tc) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean teleport(Entity entity, TeleportCause tc) {
        return false;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void sendPluginMessage(Plugin plugin, String string, byte[] bytes) {
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getListeningPluginChannels() {
        return Collections.emptySet();
    }

    /** {@inheritDoc} */
    @Override
    public void setAllowFlight(boolean bln) {
        allowFlight = bln;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getAllowFlight() {
        return allowFlight;
    }

    /** {@inheritDoc} */
    @Override
    public void setBedSpawnLocation(Location lctn) {
    }

    /** {@inheritDoc} */
    @Override
    public void setBedSpawnLocation(Location lctn, boolean force) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean sleep(Location location, boolean force) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void wakeup(boolean setSpawnLocation) {

    }

    /** {@inheritDoc} */
    @Override
    public Location getBedLocation() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void playEffect(EntityEffect ee) {
    }

    /** {@inheritDoc} */
    @Override
    public void hidePlayer(Player player) {
    }

    /** {@inheritDoc} */
    @Override
    public void hidePlayer(Plugin plugin, Player player) {

    }

    /** {@inheritDoc} */
    @Override
    public void showPlayer(Player player) {
    }

    /** {@inheritDoc} */
    @Override
    public void showPlayer(Plugin plugin, Player player) {

    }

    /** {@inheritDoc} */
    @Override
    public boolean canSee(Player player) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addPotionEffect(PotionEffect pe) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addPotionEffect(PotionEffect pe, boolean bln) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addPotionEffects(Collection<PotionEffect> clctn) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasPotionEffect(PotionEffectType pet) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public PotionEffect getPotionEffect(PotionEffectType potionEffectType) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void removePotionEffect(PotionEffectType pet) {
    }

    /** {@inheritDoc} */
    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    /** {@inheritDoc} */
    @Override
    public <T> void playEffect(Location lctn, Effect effect, T t) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean setWindowProperty(Property prprt, int i) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public InventoryView getOpenInventory() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public InventoryView openInventory(Inventory invntr) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public InventoryView openWorkbench(Location lctn, boolean bln) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public InventoryView openEnchanting(Location lctn, boolean bln) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void openInventory(InventoryView iv) {
    }

    /** {@inheritDoc} */
    @Override
    public InventoryView openMerchant(Villager villager, boolean b) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public InventoryView openMerchant(Merchant merchant, boolean b) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void closeInventory() {
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack getItemOnCursor() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setItemOnCursor(ItemStack is) {
    }

    /** {@inheritDoc} */
    @Override
    public void setMetadata(String string, MetadataValue mv) {
    }

    /** {@inheritDoc} */
    @Override
    public List<MetadataValue> getMetadata(String string) {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasMetadata(String string) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void removeMetadata(String string, Plugin plugin) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isConversing() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void acceptConversationInput(String string) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean beginConversation(Conversation c) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void abandonConversation(Conversation c) {
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(String[] strings) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBlocking() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHandRaised() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFlying() {
        return isFlying;
    }

    /** {@inheritDoc} */
    @Override
    public void setFlying(boolean arg0) {
        isFlying = arg0;
    }

    /** {@inheritDoc} */
    @Override
    public int getExpToLevel() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean discoverRecipe(NamespacedKey recipe) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int discoverRecipes(Collection<NamespacedKey> recipes) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean undiscoverRecipe(NamespacedKey recipe) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int undiscoverRecipes(Collection<NamespacedKey> recipes) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasLineOfSight(Entity entity) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setFlySpeed(float value) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public void setWalkSpeed(float value) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public float getFlySpeed() {
        return 0.1f;
    }

    /** {@inheritDoc} */
    @Override
    public float getWalkSpeed() {
        return 0.2f;
    }

    /** {@inheritDoc} */
    @Override
    public Inventory getEnderChest() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public MainHand getMainHand() {
        return MainHand.RIGHT;
    }

    /** {@inheritDoc} */
    @Override
    public void playSound(Location arg0, Sound arg1, float arg2, float arg3) {
    }

    /** {@inheritDoc} */
    @Override
    public void giveExpLevels(int i) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean getRemoveWhenFarAway() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setRemoveWhenFarAway(boolean bln) {
    }

    /** {@inheritDoc} */
    @Override
    public EntityEquipment getEquipment() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setCanPickupItems(boolean bln) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean getCanPickupItems() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Location getLocation(Location lctn) {
        return lctn;
    }

    /** {@inheritDoc} */
    @Override
    public void setTexturePack(String string) {
    }

    /** {@inheritDoc} */
    @Override
    public void setResourcePack(String s) {
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxHealth(double i) {
    }

    /** {@inheritDoc} */
    @Override
    public void resetMaxHealth() {
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomName(String string) {
    }

    /** {@inheritDoc} */
    @Override
    public String getCustomName() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomNameVisible(boolean bln) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setGlowing(boolean b) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isGlowing() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setInvulnerable(boolean b) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInvulnerable() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSilent() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setSilent(boolean b) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasGravity() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setGravity(boolean b) {
    }

    /** {@inheritDoc} */
    @Override
    public int getPortalCooldown() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setPortalCooldown(int i) {

    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getScoreboardTags() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addScoreboardTag(String s) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeScoreboardTag(String s) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setPlayerWeather(WeatherType arg0) {
    }

    /** {@inheritDoc} */
    @Override
    public WeatherType getPlayerWeather() {
        return null; // per player weather, null means default anyways
    }

    /** {@inheritDoc} */
    @Override
    public void resetPlayerWeather() {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOnGround() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setScoreboard(Scoreboard scrbrd) throws IllegalArgumentException, IllegalStateException {
    }

    /** {@inheritDoc} */
    @Override
    public void playSound(Location arg0, String arg1, float arg2, float arg3) {
    }

    /** {@inheritDoc} */
    @Override
    public void playSound(Location location, Sound sound, SoundCategory soundCategory, float v, float v1) {
        
    }

    /** {@inheritDoc} */
    @Override
    public void playSound(Location location, String s, SoundCategory soundCategory, float v, float v1) {

    }

    /** {@inheritDoc} */
    @Override
    public void stopSound(Sound sound) {
    }

    /** {@inheritDoc} */
    @Override
    public void stopSound(String s) {
    }

    /** {@inheritDoc} */
    @Override
    public void stopSound(Sound sound, SoundCategory soundCategory) {

    }

    /** {@inheritDoc} */
    @Override
    public void stopSound(String s, SoundCategory soundCategory) {

    }

    /** {@inheritDoc} */
    @Override
    public boolean isHealthScaled() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setHealthScaled(boolean arg0) {
    }

    /** {@inheritDoc} */
    @Override
    public void setHealthScale(double arg0) throws IllegalArgumentException {
        
    }

    /** {@inheritDoc} */
    @Override
    public double getHealthScale() {
        return 0D;
    }

    /** {@inheritDoc} */
    @Override
    public void setSpectatorTarget(Entity entity) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isLeashed() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setLeashHolder(Entity arg0) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isGliding() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setGliding(boolean b) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSwimming() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setSwimming(boolean swimming) {

    }

    /** {@inheritDoc} */
    @Override
    public boolean isRiptiding() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setAI(boolean b) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasAI() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setCollidable(boolean b) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCollidable() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public <T> T getMemory(MemoryKey<T> memoryKey) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public <T> void setMemory(MemoryKey<T> memoryKey, T t) {

    }

    /** {@inheritDoc} */
    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> type, Vector vector) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void sendSignChange(Location arg0, String[] arg1) throws IllegalArgumentException {
    }

    /** {@inheritDoc} */
    @Override
    public Location getBedSpawnLocation() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public UUID getUniqueId() {
        return base.getUniqueId();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOp() {
        return base.isOp();
    }

    /** {@inheritDoc} */
    @Override
    public void setOp(boolean value) {
        base.setOp(value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOnline() {
        return base.isOnline();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBanned() {
        if (base.getName() == null && getName() != null) {
            return server.getBanList(BanList.Type.NAME).isBanned(getName());
        }
        return base.isBanned();
    }

    // Removed in 1.12, retain for backwards compatibility.
    /**
     * <p>setBanned.</p>
     *
     * @param banned a boolean.
     */
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

    /** {@inheritDoc} */
    @Override
    public boolean isWhitelisted() {
        return base.isWhitelisted();
    }

    /** {@inheritDoc} */
    @Override
    public void setWhitelisted(boolean value) {
        base.setWhitelisted(value);
    }

    /** {@inheritDoc} */
    @Override
    public Player getPlayer() {
        return base.getPlayer();
    }

    /** {@inheritDoc} */
    @Override
    public long getFirstPlayed() {
        return base.getFirstPlayed();
    }

    /** {@inheritDoc} */
    @Override
    public long getLastPlayed() {
        return base.getLastPlayed();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasPlayedBefore() {
        return base.hasPlayedBefore();
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> serialize() {
        return base.serialize();
    }

    /** {@inheritDoc} */
    @Override
    public Entity getSpectatorTarget() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void resetTitle() {
    }

    /** {@inheritDoc} */
    @Override
    public void spawnParticle(Particle particle, Location location, int i) {

    }

    /** {@inheritDoc} */
    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i) {

    }

    /** {@inheritDoc} */
    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, T t) {

    }

    /** {@inheritDoc} */
    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, T t) {

    }

    /** {@inheritDoc} */
    @Override
    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2) {

    }

    /** {@inheritDoc} */
    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5) {

    }

    /** {@inheritDoc} */
    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, T t) {

    }

    /** {@inheritDoc} */
    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, T t) {

    }

    /** {@inheritDoc} */
    @Override
    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3) {

    }

    /** {@inheritDoc} */
    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6) {

    }

    /** {@inheritDoc} */
    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3, T t) {

    }

    /** {@inheritDoc} */
    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, T t) {

    }

    /** {@inheritDoc} */
    @Override
    public void sendTitle(String title, String subtitle) {
        
    }

    /** {@inheritDoc} */
    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        
    }

    /** {@inheritDoc} */
    @Override
    public AttributeInstance getAttribute(Attribute attribute) {
        // GetAttribute is nullable as per CraftAttributeMap. This might need to be
        // improved to support cases where dummy null instances should be returned.
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setResourcePack(String s, byte[] bytes) {
    }

    /** {@inheritDoc} */
    @Override
    public AdvancementProgress getAdvancementProgress(Advancement advancement) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getClientViewDistance() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public String getLocale() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasCooldown(Material material) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int getCooldown(Material material) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setCooldown(Material material, int i) {
    }

    /** {@inheritDoc} */
    @Override
    public Entity getShoulderEntityLeft() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setShoulderEntityLeft(Entity entity) {
    }

    /** {@inheritDoc} */
    @Override
    public Entity getShoulderEntityRight() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setShoulderEntityRight(Entity entity) {
    }

    /** {@inheritDoc} */
    @Override
    public double getHeight() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public double getWidth() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<Entity> getPassengers() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addPassenger(Entity entity) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean removePassenger(Entity entity) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
	public void updateCommands() {
	}

    /** {@inheritDoc} */
    @Override
    public void openBook(ItemStack book) {
    }

    /** {@inheritDoc} */
    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return null;
    }
}

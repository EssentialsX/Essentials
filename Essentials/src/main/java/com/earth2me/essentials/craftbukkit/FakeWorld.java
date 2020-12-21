package com.earth2me.essentials.craftbukkit;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameRule;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.StructureType;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class FakeWorld implements World {
    private final String name;
    private final Environment env;

    public FakeWorld(final String string, final Environment environment) {
        this.name = string;
        this.env = environment;
    }

    @Override
    public Block getBlockAt(final int i, final int i1, final int i2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Block getBlockAt(final Location lctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHighestBlockYAt(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHighestBlockYAt(final Location lctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Chunk getChunkAt(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Chunk getChunkAt(final Location lctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Chunk getChunkAt(final Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChunkLoaded(final Chunk chunk) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Chunk[] getLoadedChunks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadChunk(final Chunk chunk) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChunkLoaded(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChunkGenerated(final int x, final int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadChunk(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean loadChunk(final int i, final int i1, final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunk(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunk(final int i, final int i1, final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public boolean unloadChunk(final int i, final int i1, final boolean bln, final boolean bln1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunkRequest(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public boolean unloadChunkRequest(final int i, final int i1, final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean regenerateChunk(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean refreshChunk(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChunkForceLoaded(final int i, final int i1) {
        return false;
    }

    @Override
    public void setChunkForceLoaded(final int i, final int i1, final boolean b) {

    }

    @Override
    public Collection<Chunk> getForceLoadedChunks() {
        return null;
    }

    @Override
    public boolean addPluginChunkTicket(final int i, final int i1, final Plugin plugin) {
        return false;
    }

    @Override
    public boolean removePluginChunkTicket(final int i, final int i1, final Plugin plugin) {
        return false;
    }

    @Override
    public void removePluginChunkTickets(final Plugin plugin) {

    }

    @Override
    public Collection<Plugin> getPluginChunkTickets(final int i, final int i1) {
        return null;
    }

    @Override
    public Map<Plugin, Collection<Chunk>> getPluginChunkTickets() {
        return null;
    }

    @Override
    public Item dropItem(final Location lctn, final ItemStack is) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Item dropItemNaturally(final Location lctn, final ItemStack is) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Arrow spawnArrow(final Location lctn, final Vector vector, final float f, final float f1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends AbstractArrow> T spawnArrow(final Location location, final Vector vector, final float v, final float v1, final Class<T> aClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean generateTree(final Location lctn, final TreeType tt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean generateTree(final Location loc, final TreeType type, final BlockChangeDelegate delegate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LightningStrike strikeLightning(final Location lctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LightningStrike strikeLightningEffect(final Location lctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Entity> getEntities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<LivingEntity> getLivingEntities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Player> getPlayers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getSpawnLocation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setSpawnLocation(final Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setSpawnLocation(final int i, final int i1, final int i2, final float v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setSpawnLocation(final int i, final int i1, final int i2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTime(final long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getFullTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFullTime(final long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasStorm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStorm(final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getWeatherDuration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWeatherDuration(final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isThundering() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setThundering(final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getThunderDuration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setThunderDuration(final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isClearWeather() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setClearWeatherDuration(int duration) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getClearWeatherDuration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Environment getEnvironment() {
        return env;
    }

    @Override
    public long getSeed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getPVP() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPVP(final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(final double d, final double d1, final double d2, final float f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(final Location lctn, final float f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChunkGenerator getGenerator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<BlockPopulator> getPopulators() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playEffect(final Location lctn, final Effect effect, final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playEffect(final Location lctn, final Effect effect, final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(final double d, final double d1, final double d2, final float f, final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(final Location lctn, final float f, final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(final Location location, final float v, final boolean b, final boolean b1) {
        return false;
    }

    @Override
    public boolean createExplosion(final Location location, final float v, final boolean b, final boolean b1, final Entity entity) {
        return false;
    }

    @Override
    public <T extends Entity> T spawn(final Location lctn, final Class<T> type) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Entity> T spawn(final Location location, final Class<T> aClass, final org.bukkit.util.Consumer<T> consumer) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FallingBlock spawnFallingBlock(final Location location, final MaterialData materialData) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FallingBlock spawnFallingBlock(final Location location, final BlockData blockData) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChunkSnapshot getEmptyChunkSnapshot(final int i, final int i1, final boolean bln, final boolean bln1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSpawnFlags(final boolean bln, final boolean bln1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getAllowAnimals() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getAllowMonsters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID getUID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Block getHighestBlockAt(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Block getHighestBlockAt(final Location lctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHighestBlockYAt(final int i, final int i1, final HeightMap heightMap) {
        return 0;
    }

    @Override
    public int getHighestBlockYAt(final Location location, final HeightMap heightMap) {
        return 0;
    }

    @Override
    public Block getHighestBlockAt(final int i, final int i1, final HeightMap heightMap) {
        return null;
    }

    @Override
    public Block getHighestBlockAt(final Location location, final HeightMap heightMap) {
        return null;
    }

    @Override
    public Biome getBiome(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Biome getBiome(final int i, final int i1, final int i2) {
        return null;
    }

    @Override
    public double getTemperature(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getTemperature(final int i, final int i1, final int i2) {
        return 0;
    }

    @Override
    public double getHumidity(final int i, final int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getHumidity(final int i, final int i1, final int i2) {
        return 0;
    }

    @Override
    public boolean unloadChunk(final Chunk chunk) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMaxHeight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getKeepSpawnInMemory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setKeepSpawnInMemory(final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAutoSave() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAutoSave(final boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Difficulty getDifficulty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDifficulty(final Difficulty difficulty) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getSeaLevel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File getWorldFolder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Entity> Collection<T> getEntitiesByClass(final Class<T>... types) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WorldType getWorldType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendPluginMessage(final Plugin plugin, final String string, final byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canGenerateStructures() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isHardcore() {
        return false;
    }

    @Override
    public void setHardcore(final boolean b) {

    }

    @Override
    public long getTicksPerAnimalSpawns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTicksPerAnimalSpawns(final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getTicksPerMonsterSpawns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTicksPerMonsterSpawns(final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getTicksPerWaterSpawns() {
        return 0;
    }

    @Override
    public void setTicksPerWaterSpawns(final int i) {

    }

    @Override
    public long getTicksPerWaterAmbientSpawns() {
        return 0;
    }

    @Override
    public void setTicksPerWaterAmbientSpawns(final int i) {

    }

    @Override
    public long getTicksPerAmbientSpawns() {
        return 0;
    }

    @Override
    public void setTicksPerAmbientSpawns(final int i) {

    }

    @Override
    public <T extends Entity> Collection<T> getEntitiesByClass(final Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Entity> getEntitiesByClasses(final Class<?>... types) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void playEffect(final Location lctn, final Effect effect, final T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void playEffect(final Location lctn, final Effect effect, final T t, final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMetadata(final String string, final MetadataValue mv) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<MetadataValue> getMetadata(final String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasMetadata(final String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeMetadata(final String string, final Plugin plugin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setBiome(final int arg0, final int arg1, final Biome arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setBiome(final int i, final int i1, final int i2, final Biome biome) {

    }

    @Override
    public int getMonsterSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMonsterSpawnLimit(final int arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAnimalSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAnimalSpawnLimit(final int arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWaterAnimalSpawnLimit(final int arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getWaterAmbientSpawnLimit() {
        return 0;
    }

    @Override
    public void setWaterAmbientSpawnLimit(final int i) {

    }

    @Override
    public Entity spawnEntity(final Location lctn, final EntityType et) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChunkInUse(final int x, final int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FallingBlock spawnFallingBlock(final Location location, final Material material, final byte data) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(final Location arg0, final Sound arg1, final float arg2, final float arg3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(final Location location, final String s, final float v, final float v1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(final Location location, final Sound sound, final SoundCategory soundCategory, final float v, final float v1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(final Location location, final String s, final SoundCategory soundCategory, final float v, final float v1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAmbientSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAmbientSpawnLimit(final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getGameRules() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getGameRuleValue(final String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setGameRuleValue(final String string, final String string1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isGameRule(final String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(final double d, final double d1, final double d2, final float f, final boolean bln, final boolean bln1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(final double v, final double v1, final double v2, final float v3, final boolean b, final boolean b1, final Entity entity) {
        return false;
    }

    @Override
    public WorldBorder getWorldBorder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(final Particle particle, final Location location, final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final Location location, final int i, final T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i, final T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(final Particle particle, final Location location, final int i, final double v, final double v1, final double v2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i, final double v3, final double v4, final double v5) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final Location location, final int i, final double v, final double v1, final double v2, final T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i, final double v3, final double v4, final double v5, final T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(final Particle particle, final Location location, final int i, final double v, final double v1, final double v2, final double v3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i, final double v3, final double v4, final double v5, final double v6) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final Location location, final int i, final double v, final double v1, final double v2, final double v3, final T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i, final double v3, final double v4, final double v5, final double v6, final T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final Location location, final int i, final double v, final double v1, final double v2, final double v3, final T t, final boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i, final double v3, final double v4, final double v5, final double v6, final T t, final boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Location locateNearestStructure(final Location origin, final StructureType structureType, final int radius, final boolean findUnexplored) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getViewDistance() {
        return 0;
    }

    @Override
    public Spigot spigot() {
        return null;
    }

    @Override
    public Raid locateNearestRaid(final Location location, final int i) {
        return null;
    }

    @Override
    public List<Raid> getRaids() {
        return null;
    }

    @Override
    public DragonBattle getEnderDragonBattle() {
        return null;
    }

    @Override
    public List<Entity> getNearbyEntities(final Location loc, final double x, final double y, final double z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Entity> getNearbyEntities(final Location location, final double v, final double v1, final double v2, final Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public Collection<Entity> getNearbyEntities(final BoundingBox boundingBox) {
        return null;
    }

    @Override
    public Collection<Entity> getNearbyEntities(final BoundingBox boundingBox, final Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public RayTraceResult rayTraceEntities(final Location location, final Vector vector, final double v) {
        return null;
    }

    @Override
    public RayTraceResult rayTraceEntities(final Location location, final Vector vector, final double v, final double v1) {
        return null;
    }

    @Override
    public RayTraceResult rayTraceEntities(final Location location, final Vector vector, final double v, final Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public RayTraceResult rayTraceEntities(final Location location, final Vector vector, final double v, final double v1, final Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public RayTraceResult rayTraceBlocks(final Location location, final Vector vector, final double v) {
        return null;
    }

    @Override
    public RayTraceResult rayTraceBlocks(final Location location, final Vector vector, final double v, final FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    @Override
    public RayTraceResult rayTraceBlocks(final Location location, final Vector vector, final double v, final FluidCollisionMode fluidCollisionMode, final boolean b) {
        return null;
    }

    @Override
    public RayTraceResult rayTrace(final Location location, final Vector vector, final double v, final FluidCollisionMode fluidCollisionMode, final boolean b, final double v1, final Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public <T> T getGameRuleDefault(final GameRule<T> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T getGameRuleValue(final GameRule<T> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> boolean setGameRule(final GameRule<T> arg0, final T arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

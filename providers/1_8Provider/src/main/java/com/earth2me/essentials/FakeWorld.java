package com.earth2me.essentials;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
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
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FakeWorld implements World {
    private final String name;
    private final Environment env;
    private final UUID uid;

    public FakeWorld(final String string, final Environment environment) {
        this.name = string;
        this.env = environment;
        this.uid = UUID.randomUUID();
    }

    @Override
    public Block getBlockAt(int x, int y, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Block getBlockAt(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getBlockTypeIdAt(int x, int y, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getBlockTypeIdAt(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHighestBlockYAt(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHighestBlockYAt(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Block getHighestBlockAt(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Block getHighestBlockAt(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Chunk getChunkAt(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Chunk getChunkAt(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Chunk getChunkAt(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChunkLoaded(Chunk chunk) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Chunk[] getLoadedChunks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadChunk(Chunk chunk) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChunkInUse(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadChunk(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean loadChunk(int x, int z, boolean generate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunk(Chunk chunk) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunk(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunk(int x, int z, boolean save) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunkRequest(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunkRequest(int x, int z, boolean safe) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean regenerateChunk(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean refreshChunk(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Item dropItem(Location location, ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Item dropItemNaturally(Location location, ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Arrow spawnArrow(Location location, Vector direction, float speed, float spread) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Arrow> T spawnArrow(Location location, Vector direction, float speed, float spread, Class<T> clazz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean generateTree(Location location, TreeType type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Entity spawnEntity(Location loc, EntityType type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LightningStrike strikeLightning(Location loc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LightningStrike strikeLightningEffect(Location loc) {
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

    @SafeVarargs
    @Override
    public final <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> cls) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Entity> getEntitiesByClasses(Class<?>... classes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Player> getPlayers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUID() {
        return uid;
    }

    @Override
    public Location getSpawnLocation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setSpawnLocation(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setSpawnLocation(int x, int y, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTime(long time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getFullTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFullTime(long time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasStorm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStorm(boolean hasStorm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getWeatherDuration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWeatherDuration(int duration) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isThundering() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setThundering(boolean thundering) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getThunderDuration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setThunderDuration(int duration) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(Location loc, float power) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(Location loc, float power, boolean setFire) {
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
    public void setPVP(boolean pvp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChunkGenerator getGenerator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<BlockPopulator> getPopulators() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<T> function) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, MaterialData data) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, Material material, byte data) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, int blockId, byte blockData) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playEffect(Location location, Effect effect, int data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playEffect(Location location, Effect effect, int data, int radius) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T data, int radius) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
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
    public Biome getBiome(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setBiome(int x, int z, Biome bio) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getTemperature(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getHumidity(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMaxHeight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getSeaLevel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getKeepSpawnInMemory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setKeepSpawnInMemory(boolean keepLoaded) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAutoSave() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAutoSave(boolean value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Difficulty getDifficulty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File getWorldFolder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WorldType getWorldType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canGenerateStructures() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getTicksPerAnimalSpawns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getTicksPerMonsterSpawns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMonsterSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMonsterSpawnLimit(int limit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAnimalSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAnimalSpawnLimit(int limit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWaterAnimalSpawnLimit(int limit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAmbientSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAmbientSpawnLimit(int limit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(Location location, String sound, float volume, float pitch) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(Location location, String sound, SoundCategory category, float volume, float pitch) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getGameRules() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getGameRuleValue(String rule) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setGameRuleValue(String rule, String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isGameRule(String rule) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WorldBorder getWorldBorder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, T data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

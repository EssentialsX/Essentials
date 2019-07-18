package com.earth2me.essentials.craftbukkit;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
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

    public FakeWorld(String string, Environment environment) {
        this.name = string;
        this.env = environment;
    }

    @Override
    public Block getBlockAt(int i, int i1, int i2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Block getBlockAt(Location lctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHighestBlockYAt(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHighestBlockYAt(Location lctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Chunk getChunkAt(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Chunk getChunkAt(Location lctn) {
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
    public boolean isChunkLoaded(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChunkGenerated(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadChunk(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean loadChunk(int i, int i1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunk(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunk(int i, int i1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public boolean unloadChunk(int i, int i1, boolean bln, boolean bln1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunkRequest(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public boolean unloadChunkRequest(int i, int i1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean regenerateChunk(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean refreshChunk(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChunkForceLoaded(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setChunkForceLoaded(int x, int z, boolean forced) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Chunk> getForceLoadedChunks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addPluginChunkTicket(int i, int i1, Plugin plugin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removePluginChunkTicket(int i, int i1, Plugin plugin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removePluginChunkTickets(Plugin plugin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Plugin> getPluginChunkTickets(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Plugin, Collection<Chunk>> getPluginChunkTickets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Item dropItem(Location lctn, ItemStack is) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Item dropItemNaturally(Location lctn, ItemStack is) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Arrow spawnArrow(Location lctn, Vector vector, float f, float f1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends AbstractArrow> T spawnArrow(Location location, Vector direction, float speed, float spread, Class<T> clazz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean generateTree(Location lctn, TreeType tt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LightningStrike strikeLightning(Location lctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LightningStrike strikeLightningEffect(Location lctn) {
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
    public boolean setSpawnLocation(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setSpawnLocation(int i, int i1, int i2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTime(long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getFullTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFullTime(long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasStorm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStorm(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getWeatherDuration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWeatherDuration(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isThundering() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setThundering(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getThunderDuration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setThunderDuration(int i) {
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
    public void setPVP(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(double d, double d1, double d2, float f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(Location lctn, float f) {
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
    public void playEffect(Location lctn, Effect effect, int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playEffect(Location lctn, Effect effect, int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(double d, double d1, double d2, float f, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(Location lctn, float f, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Entity> T spawn(Location lctn, Class<T> type) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Entity> T spawn(Location location, Class<T> aClass, org.bukkit.util.Consumer<T> consumer) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, MaterialData materialData) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, BlockData blockData) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChunkSnapshot getEmptyChunkSnapshot(int i, int i1, boolean bln, boolean bln1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSpawnFlags(boolean bln, boolean bln1) {
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
    public Block getHighestBlockAt(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Block getHighestBlockAt(Location lctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Biome getBiome(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getTemperature(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getHumidity(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean unloadChunk(Chunk chunk) {
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
    public void setKeepSpawnInMemory(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAutoSave() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAutoSave(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Difficulty getDifficulty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
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
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... types) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WorldType getWorldType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendPluginMessage(Plugin plugin, String string, byte[] bytes) {
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
    public long getTicksPerAnimalSpawns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTicksPerAnimalSpawns(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getTicksPerMonsterSpawns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTicksPerMonsterSpawns(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Entity> getEntitiesByClasses(Class<?>... types) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void playEffect(Location lctn, Effect effect, T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void playEffect(Location lctn, Effect effect, T t, int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMetadata(String string, MetadataValue mv) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<MetadataValue> getMetadata(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasMetadata(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeMetadata(String string, Plugin plugin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setBiome(int arg0, int arg1, Biome arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMonsterSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMonsterSpawnLimit(int arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAnimalSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAnimalSpawnLimit(int arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWaterAnimalSpawnLimit(int arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Entity spawnEntity(Location lctn, EntityType et) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChunkInUse(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, Material material, byte data) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(Location arg0, Sound arg1, float arg2, float arg3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(Location location, String s, float v, float v1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(Location location, Sound sound, SoundCategory soundCategory, float v, float v1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(Location location, String s, SoundCategory soundCategory, float v, float v1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAmbientSpawnLimit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAmbientSpawnLimit(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getGameRules() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getGameRuleValue(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setGameRuleValue(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isGameRule(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean createExplosion(double d, double d1, double d2, float f, boolean bln, boolean bln1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WorldBorder getWorldBorder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3, T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3, T t, boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, T t, boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Location locateNearestStructure(Location origin, StructureType structureType, int radius, boolean findUnexplored) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Entity> getNearbyEntities(Location loc, double x, double y, double z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z, Predicate<Entity> filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Entity> getNearbyEntities(BoundingBox boundingBox) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Entity> getNearbyEntities(BoundingBox boundingBox, Predicate<Entity> filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, double raySize) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, Predicate<Entity> filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, double raySize, Predicate<Entity> filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RayTraceResult rayTrace(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, double raySize, Predicate<Entity> filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
	public <T> T getGameRuleDefault(GameRule<T> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T> T getGameRuleValue(GameRule<T> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T> boolean setGameRule(GameRule<T> arg0, T arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
	}
}

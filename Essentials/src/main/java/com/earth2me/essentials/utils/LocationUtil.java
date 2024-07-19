package com.earth2me.essentials.utils;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IEssentials;
import net.ess3.api.IUser;
import net.ess3.api.TranslatableException;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class LocationUtil {
    public static final int RADIUS = 3;
    public static final Vector3D[] VOLUME;
    // Water types used for TRANSPARENT_MATERIALS and is-water-safe config option
    private static final Set<Material> WATER_TYPES = EnumUtil.getAllMatching(Material.class,
        "FLOWING_WATER", "WATER");
    // Types checked by isBlockDamaging
    private static final Set<Material> DAMAGING_TYPES = EnumUtil.getAllMatching(Material.class,
        "CACTUS", "CAMPFIRE", "FIRE", "MAGMA_BLOCK", "SOUL_CAMPFIRE", "SOUL_FIRE", "SWEET_BERRY_BUSH", "WITHER_ROSE");
    private static final Set<Material> LAVA_TYPES = EnumUtil.getAllMatching(Material.class,
        "FLOWING_LAVA", "LAVA", "STATIONARY_LAVA");
    private static final Material PORTAL = EnumUtil.getMaterial("NETHER_PORTAL", "PORTAL");
    private static final Material LIGHT = EnumUtil.getMaterial("LIGHT");

    private static final Material PATH = EnumUtil.getMaterial("GRASS_PATH");

    private static final Material FARMLAND = EnumUtil.getMaterial("FARMLAND");

    // The player can stand inside these materials
    private static final Set<Material> HOLLOW_MATERIALS = new HashSet<>();
    private static final Set<Material> TRANSPARENT_MATERIALS = new HashSet<>();

    static {
        // If the server is running in a test environment, the isTransparent() method will blow up since
        // it requires the registry to be initialized. This is a workaround to prevent that from happening.
        if (!Essentials.TESTING) {
            // Materials from Material.isTransparent()
            for (final Material mat : MaterialUtil.getKnownMaterials()) {
                if (mat.isTransparent()) {
                    HOLLOW_MATERIALS.add(mat);
                }
            }
        }

        TRANSPARENT_MATERIALS.addAll(HOLLOW_MATERIALS);
        TRANSPARENT_MATERIALS.addAll(WATER_TYPES);

        // Barrier is transparent, but solid
        HOLLOW_MATERIALS.remove(Material.BARRIER);

        // Path and farmland are transparent, but solid
        HOLLOW_MATERIALS.remove(PATH);
        HOLLOW_MATERIALS.remove(FARMLAND);

        // Light blocks can be passed through and are not considered transparent for some reason
        if (LIGHT != null) {
            HOLLOW_MATERIALS.add(LIGHT);
        }
    }

    static {
        final List<Vector3D> pos = new ArrayList<>();
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    pos.add(new Vector3D(x, y, z));
                }
            }
        }
        pos.sort(Comparator.comparingInt(a -> a.x * a.x + a.y * a.y + a.z * a.z));
        VOLUME = pos.toArray(new Vector3D[0]);
    }

    private LocationUtil() {
    }

    public static void setIsWaterSafe(final boolean isWaterSafe) {
        if (isWaterSafe) {
            HOLLOW_MATERIALS.addAll(WATER_TYPES);
        } else {
            HOLLOW_MATERIALS.removeAll(WATER_TYPES);
        }
    }

    public static ItemStack convertBlockToItem(final Block block) {
        return new ItemStack(block.getType(), 1);
    }

    public static Location getTarget(final LivingEntity entity) throws Exception {
        return getTarget(entity, 300);
    }

    public static Location getTarget(final LivingEntity entity, final int maxDistance) throws Exception {
        Block block = null;
        try {
            block = entity.getTargetBlock(TRANSPARENT_MATERIALS, maxDistance);
        } catch (final NoSuchMethodError ignored) {
        } // failing now :(
        if (block == null) {
            throw new Exception("Not targeting a block");
        }
        return block.getLocation();
    }

    public static boolean isBlockAboveAir(IEssentials ess, final World world, final int x, final int y, final int z) {
        return y > ess.getWorldInfoProvider().getMaxHeight(world) || HOLLOW_MATERIALS.contains(world.getBlockAt(x, y - 1, z).getType());
    }

    public static boolean isBlockOutsideWorldBorder(final World world, final int x, final int z) {
        final Location center = world.getWorldBorder().getCenter();
        final int radius = (int) world.getWorldBorder().getSize() / 2;
        final int x1 = center.getBlockX() - radius, x2 = center.getBlockX() + radius;
        final int z1 = center.getBlockZ() - radius, z2 = center.getBlockZ() + radius;
        return x < x1 || x > x2 || z < z1 || z > z2;
    }

    public static int getXInsideWorldBorder(final World world, final int x) {
        final Location center = world.getWorldBorder().getCenter();
        final int radius = (int) world.getWorldBorder().getSize() / 2;
        final int x1 = center.getBlockX() - radius, x2 = center.getBlockX() + radius;
        if (x < x1) {
            return x1;
        } else if (x > x2) {
            return x2;
        }
        return x;
    }

    public static int getZInsideWorldBorder(final World world, final int z) {
        final Location center = world.getWorldBorder().getCenter();
        final int radius = (int) world.getWorldBorder().getSize() / 2;
        final int z1 = center.getBlockZ() - radius, z2 = center.getBlockZ() + radius;
        if (z < z1) {
            return z1;
        } else if (z > z2) {
            return z2;
        }
        return z;
    }

    public static boolean isBlockUnsafeForUser(IEssentials ess, final IUser user, final World world, final int x, final int y, final int z) {
        if (user.getBase().isOnline() && world.equals(user.getBase().getWorld()) && (user.getBase().getGameMode() == GameMode.CREATIVE || user.getBase().getGameMode() == GameMode.SPECTATOR || user.isGodModeEnabled()) && user.getBase().getAllowFlight()) {
            return false;
        }

        if (isBlockDamaging(world, x, y, z)) {
            return true;
        }
        if (isBlockAboveAir(ess, world, x, y, z)) {
            return true;
        }
        return isBlockOutsideWorldBorder(world, x, z);
    }

    public static boolean isBlockUnsafe(IEssentials ess, final World world, final int x, final int y, final int z) {
        return isBlockDamaging(world, x, y, z) || isBlockAboveAir(ess, world, x, y, z);
    }

    public static boolean isBlockDamaging(final World world, final int x, final int y, final int z) {
        final Material block = world.getBlockAt(x, y, z).getType();
        final Material below = world.getBlockAt(x, y - 1, z).getType();
        final Material above = world.getBlockAt(x, y + 1, z).getType();

        if (DAMAGING_TYPES.contains(below) || LAVA_TYPES.contains(below) || MaterialUtil.isBed(below)) {
            return true;
        }

        if (block == PORTAL) {
            return true;
        }

        return !HOLLOW_MATERIALS.contains(block) || !HOLLOW_MATERIALS.contains(above);
    }

    // Not needed if using getSafeDestination(loc)
    public static Location getRoundedDestination(final Location loc) {
        final World world = loc.getWorld();
        final int x = loc.getBlockX();
        final int y = (int) Math.round(loc.getY());
        final int z = loc.getBlockZ();
        return new Location(world, x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
    }

    /**
     * @deprecated Use {@link #getSafeDestination(IEssentials, IUser, Location)}
     */
    @Deprecated
    public static Location getSafeDestination(final IUser user, final Location loc) throws Exception {
        return getSafeDestination(null, user, loc);
    }

    public static Location getSafeDestination(final IEssentials ess, final IUser user, final Location loc) throws Exception {
        if (user.getBase().isOnline() && (ess == null || !ess.getSettings().isAlwaysTeleportSafety()) && (user.getBase().getGameMode() == GameMode.CREATIVE || user.getBase().getGameMode() == GameMode.SPECTATOR || user.isGodModeEnabled())) {
            if (shouldFly(ess, loc) && user.getBase().getAllowFlight()) {
                user.getBase().setFlying(true);
            }
            // ess can be null if old deprecated method is calling it.
            if (ess == null || ess.getSettings().isTeleportToCenterLocation()) {
                return getRoundedDestination(loc);
            } else {
                return loc;
            }
        }
        return getSafeDestination(ess, loc);
    }

    public static Location getSafeDestination(IEssentials ess, final Location loc) throws Exception {
        if (loc == null || loc.getWorld() == null) {
            throw new TranslatableException("destinationNotSet");
        }
        final World world = loc.getWorld();
        final int worldMinY = ess.getWorldInfoProvider().getMinHeight(world);
        final int worldLogicalY = ess.getWorldInfoProvider().getLogicalHeight(world);
        final int worldMaxY = loc.getBlockY() < worldLogicalY ? worldLogicalY : ess.getWorldInfoProvider().getMaxHeight(world);
        int x = loc.getBlockX();
        int y = (int) Math.round(loc.getY());
        int z = loc.getBlockZ();
        if (isBlockOutsideWorldBorder(world, x, z)) {
            x = getXInsideWorldBorder(world, x);
            z = getZInsideWorldBorder(world, z);
        }
        final int origX = x;
        final int origY = y;
        final int origZ = z;
        while (isBlockAboveAir(ess, world, x, y, z)) {
            y -= 1;
            if (y < 0) {
                y = origY;
                break;
            }
        }
        if (isBlockUnsafe(ess, world, x, y, z)) {
            x = Math.round(loc.getX()) == origX ? x - 1 : x + 1;
            z = Math.round(loc.getZ()) == origZ ? z - 1 : z + 1;
        }
        int i = 0;
        while (isBlockUnsafe(ess, world, x, y, z)) {
            i++;
            if (i >= VOLUME.length) {
                x = origX;
                y = NumberUtil.constrainToRange(origY + RADIUS, worldMinY, worldMaxY);
                z = origZ;
                break;
            }
            x = origX + VOLUME[i].x;
            y = NumberUtil.constrainToRange(origY + VOLUME[i].y, worldMinY, worldMaxY);
            z = origZ + VOLUME[i].z;
        }
        while (isBlockUnsafe(ess, world, x, y, z)) {
            y += 1;
            if (y >= worldMaxY) {
                x += 1;
                break;
            }
        }
        while (isBlockUnsafe(ess, world, x, y, z)) {
            y -= 1;
            if (y <= worldMinY + 1) {
                x += 1;
                // Allow spawning at the top of the world, but not above the nether roof
                y = Math.min(world.getHighestBlockYAt(x, z) + 1, worldMaxY);
                if (x - 48 > loc.getBlockX()) {
                    throw new TranslatableException("holeInFloor");
                }
            }
        }
        return new Location(world, x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
    }

    public static boolean shouldFly(IEssentials ess, final Location loc) {
        final World world = loc.getWorld();
        final int x = loc.getBlockX();
        int y = (int) Math.round(loc.getY());
        final int z = loc.getBlockZ();
        int count = 0;
        // Check whether more than 2 unsafe block are below player.
        while (LocationUtil.isBlockUnsafe(ess, world, x, y, z) && y >= ess.getWorldInfoProvider().getMinHeight(world)) {
            y--;
            count++;
            if (count > 2) {
                return true;
            }
        }

        // If not then check if player is in the void
        return y < ess.getWorldInfoProvider().getMinHeight(world);
    }

    public static class Vector3D {
        public final int x;
        public final int y;
        public final int z;

        Vector3D(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}

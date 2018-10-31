package com.earth2me.essentials.utils;

import net.ess3.api.IUser;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.earth2me.essentials.I18n.tl;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IEssentials;


public class LocationUtil {
    // The player can stand inside these materials
    public static final Set<Material> HOLLOW_MATERIALS = new HashSet<>();
    private static final Set<Material> TRANSPARENT_MATERIALS = new HashSet<>();

    static {
        // Materials from Material.isTransparent()
        HOLLOW_MATERIALS.add(Material.AIR);
        HOLLOW_MATERIALS.add(Material.OAK_SAPLING);
        HOLLOW_MATERIALS.add(Material.SPRUCE_SAPLING);
        HOLLOW_MATERIALS.add(Material.BIRCH_SAPLING);
        HOLLOW_MATERIALS.add(Material.JUNGLE_SAPLING);
        HOLLOW_MATERIALS.add(Material.ACACIA_SAPLING);
        HOLLOW_MATERIALS.add(Material.DARK_OAK_SAPLING);
        HOLLOW_MATERIALS.add(Material.POWERED_RAIL);
        HOLLOW_MATERIALS.add(Material.DETECTOR_RAIL);
        HOLLOW_MATERIALS.add(Material.TALL_GRASS);
        HOLLOW_MATERIALS.add(Material.DEAD_BUSH);
        HOLLOW_MATERIALS.add(Material.DANDELION);
        HOLLOW_MATERIALS.add(Material.POPPY);
        HOLLOW_MATERIALS.add(Material.BROWN_MUSHROOM);
        HOLLOW_MATERIALS.add(Material.RED_MUSHROOM);
        HOLLOW_MATERIALS.add(Material.TORCH);
        HOLLOW_MATERIALS.add(Material.FIRE);
        HOLLOW_MATERIALS.add(Material.REDSTONE_WIRE);
        HOLLOW_MATERIALS.add(Material.WHEAT);
        HOLLOW_MATERIALS.add(Material.LADDER);
        HOLLOW_MATERIALS.add(Material.RAIL);
        HOLLOW_MATERIALS.add(Material.LEVER);
        HOLLOW_MATERIALS.add(Material.REDSTONE_TORCH);
        HOLLOW_MATERIALS.add(Material.STONE_BUTTON);
        HOLLOW_MATERIALS.add(Material.SNOW);
        HOLLOW_MATERIALS.add(Material.SUGAR_CANE);
        HOLLOW_MATERIALS.add(Material.NETHER_PORTAL);
        HOLLOW_MATERIALS.add(Material.REPEATER);
        HOLLOW_MATERIALS.add(Material.PUMPKIN_STEM);
        HOLLOW_MATERIALS.add(Material.MELON_STEM);
        HOLLOW_MATERIALS.add(Material.VINE);
        HOLLOW_MATERIALS.add(Material.LILY_PAD);
        HOLLOW_MATERIALS.add(Material.NETHER_WART);
        HOLLOW_MATERIALS.add(Material.END_PORTAL);
        HOLLOW_MATERIALS.add(Material.COCOA);
        HOLLOW_MATERIALS.add(Material.TRIPWIRE_HOOK);
        HOLLOW_MATERIALS.add(Material.TRIPWIRE);
        HOLLOW_MATERIALS.add(Material.FLOWER_POT);
        HOLLOW_MATERIALS.add(Material.CARROT);
        HOLLOW_MATERIALS.add(Material.POTATO);
        HOLLOW_MATERIALS.add(Material.OAK_BUTTON);
        HOLLOW_MATERIALS.add(Material.SPRUCE_BUTTON);
        HOLLOW_MATERIALS.add(Material.BIRCH_BUTTON);
        HOLLOW_MATERIALS.add(Material.JUNGLE_BUTTON);
        HOLLOW_MATERIALS.add(Material.ACACIA_BUTTON);
        HOLLOW_MATERIALS.add(Material.DARK_OAK_BUTTON);
        HOLLOW_MATERIALS.add(Material.SKELETON_SKULL);
        HOLLOW_MATERIALS.add(Material.SKELETON_WALL_SKULL);
        HOLLOW_MATERIALS.add(Material.WITHER_SKELETON_SKULL);
        HOLLOW_MATERIALS.add(Material.WITHER_SKELETON_WALL_SKULL);
        HOLLOW_MATERIALS.add(Material.ZOMBIE_HEAD);
        HOLLOW_MATERIALS.add(Material.ZOMBIE_WALL_HEAD);
        HOLLOW_MATERIALS.add(Material.PLAYER_HEAD);
        HOLLOW_MATERIALS.add(Material.PLAYER_WALL_HEAD);
        HOLLOW_MATERIALS.add(Material.CREEPER_HEAD);
        HOLLOW_MATERIALS.add(Material.CREEPER_WALL_HEAD);
        HOLLOW_MATERIALS.add(Material.DRAGON_HEAD);
        HOLLOW_MATERIALS.add(Material.DRAGON_WALL_HEAD);
        HOLLOW_MATERIALS.add(Material.COMPARATOR);
        HOLLOW_MATERIALS.add(Material.ACTIVATOR_RAIL);
        HOLLOW_MATERIALS.add(Material.WHITE_CARPET);
        HOLLOW_MATERIALS.add(Material.ORANGE_CARPET);
        HOLLOW_MATERIALS.add(Material.MAGENTA_CARPET);
        HOLLOW_MATERIALS.add(Material.LIGHT_BLUE_CARPET);
        HOLLOW_MATERIALS.add(Material.YELLOW_CARPET);
        HOLLOW_MATERIALS.add(Material.LIME_CARPET);
        HOLLOW_MATERIALS.add(Material.PINK_CARPET);
        HOLLOW_MATERIALS.add(Material.GRAY_CARPET);
        HOLLOW_MATERIALS.add(Material.LIGHT_GRAY_CARPET);
        HOLLOW_MATERIALS.add(Material.CYAN_CARPET);
        HOLLOW_MATERIALS.add(Material.PURPLE_CARPET);
        HOLLOW_MATERIALS.add(Material.BLUE_CARPET);
        HOLLOW_MATERIALS.add(Material.BROWN_CARPET);
        HOLLOW_MATERIALS.add(Material.GREEN_CARPET);
        HOLLOW_MATERIALS.add(Material.RED_CARPET);
        HOLLOW_MATERIALS.add(Material.BLACK_CARPET);
        HOLLOW_MATERIALS.add(Material.SUNFLOWER);
        HOLLOW_MATERIALS.add(Material.LILAC);
        HOLLOW_MATERIALS.add(Material.TALL_GRASS);
        HOLLOW_MATERIALS.add(Material.LARGE_FERN);
        HOLLOW_MATERIALS.add(Material.ROSE_BUSH);
        HOLLOW_MATERIALS.add(Material.PEONY);

                // Additional Materials added in by Essentials
        HOLLOW_MATERIALS.add(Material.WHEAT_SEEDS);
        HOLLOW_MATERIALS.add(Material.SIGN);
        HOLLOW_MATERIALS.add(Material.OAK_DOOR);
        HOLLOW_MATERIALS.add(Material.SPRUCE_DOOR);
        HOLLOW_MATERIALS.add(Material.BIRCH_DOOR);
        HOLLOW_MATERIALS.add(Material.JUNGLE_DOOR);
        HOLLOW_MATERIALS.add(Material.ACACIA_DOOR);
        HOLLOW_MATERIALS.add(Material.DARK_OAK_DOOR);
        HOLLOW_MATERIALS.add(Material.WALL_SIGN);
        HOLLOW_MATERIALS.add(Material.STONE_PRESSURE_PLATE);
        HOLLOW_MATERIALS.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        HOLLOW_MATERIALS.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        HOLLOW_MATERIALS.add(Material.OAK_PRESSURE_PLATE);
        HOLLOW_MATERIALS.add(Material.SPRUCE_PRESSURE_PLATE);
        HOLLOW_MATERIALS.add(Material.BIRCH_PRESSURE_PLATE);
        HOLLOW_MATERIALS.add(Material.JUNGLE_PRESSURE_PLATE);
        HOLLOW_MATERIALS.add(Material.ACACIA_PRESSURE_PLATE);
        HOLLOW_MATERIALS.add(Material.DARK_OAK_PRESSURE_PLATE);
        HOLLOW_MATERIALS.add(Material.IRON_DOOR);
        HOLLOW_MATERIALS.add(Material.OAK_FENCE_GATE);
        HOLLOW_MATERIALS.add(Material.SPRUCE_FENCE_GATE);
        HOLLOW_MATERIALS.add(Material.BIRCH_FENCE_GATE);
        HOLLOW_MATERIALS.add(Material.JUNGLE_FENCE_GATE);
        HOLLOW_MATERIALS.add(Material.ACACIA_FENCE_GATE);
        HOLLOW_MATERIALS.add(Material.DARK_OAK_FENCE_GATE);

        TRANSPARENT_MATERIALS.addAll(HOLLOW_MATERIALS);
        TRANSPARENT_MATERIALS.add(Material.WATER);
    }

    public static final int RADIUS = 3;
    public static final Vector3D[] VOLUME;

    public static ItemStack convertBlockToItem(final Block block) {
        final ItemStack is = new ItemStack(block.getType());
        switch (is.getType()) {
            case SIGN:
            case WALL_SIGN:
                is.setType(Material.SIGN);
                is.setDurability((short) 0);
                break;
            case WHEAT:
                is.setType(Material.WHEAT_SEEDS);
                is.setDurability((short) 0);
                break;
            case REDSTONE_WIRE:
                is.setType(Material.REDSTONE);
                is.setDurability((short) 0);
                break;
            case TORCH:
            case RAIL:
            case LADDER:
            case OAK_STAIRS:
            case SPRUCE_STAIRS:
            case BIRCH_STAIRS:
            case JUNGLE_STAIRS:
            case ACACIA_STAIRS:
            case DARK_OAK_STAIRS:
            case COBBLESTONE_STAIRS:
            case LEVER:
            case STONE_BUTTON:
            case FURNACE:
            case DISPENSER:
            case PUMPKIN:
            case JACK_O_LANTERN:
            case OAK_PRESSURE_PLATE:
            case SPRUCE_PRESSURE_PLATE:
            case BIRCH_PRESSURE_PLATE:
            case JUNGLE_PRESSURE_PLATE:
            case ACACIA_PRESSURE_PLATE:
            case DARK_OAK_PRESSURE_PLATE:
            case STONE_PRESSURE_PLATE:
            case LIGHT_WEIGHTED_PRESSURE_PLATE:
            case HEAVY_WEIGHTED_PRESSURE_PLATE:
            case STICKY_PISTON:
            case PISTON:
            case IRON_BARS:
            case GLASS_PANE:
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case ACACIA_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case IRON_TRAPDOOR:
            case OAK_FENCE:
            case SPRUCE_FENCE:
            case BIRCH_FENCE:
            case JUNGLE_FENCE:
            case ACACIA_FENCE:
            case DARK_OAK_FENCE:
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case NETHER_BRICK_FENCE:
                is.setDurability((short) 0);
                break;
            case FIRE:
                return null;
            case PUMPKIN_STEM:
                is.setType(Material.PUMPKIN_SEEDS);
                break;
            case MELON_STEM:
                is.setType(Material.MELON_SEEDS);
                break;
        }
        return is;
    }


    public static class Vector3D {
        public int x;
        public int y;
        public int z;

        public Vector3D(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    static {
        List<Vector3D> pos = new ArrayList<Vector3D>();
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    pos.add(new Vector3D(x, y, z));
                }
            }
        }
        Collections.sort(pos, new Comparator<Vector3D>() {
            @Override
            public int compare(Vector3D a, Vector3D b) {
                return (a.x * a.x + a.y * a.y + a.z * a.z) - (b.x * b.x + b.y * b.y + b.z * b.z);
            }
        });
        VOLUME = pos.toArray(new Vector3D[0]);
    }

    @SuppressWarnings("deprecation")
    public static Location getTarget(final LivingEntity entity) throws Exception {
        Block block;
        try {
            block = entity.getTargetBlock(TRANSPARENT_MATERIALS, 300);
        } catch (NoSuchMethodError e) {
            HashSet<Material> legacyTransparent = new HashSet<>(); // Bukkit API prevents declaring as Set<Byte>
            for (Material m : TRANSPARENT_MATERIALS) {
                legacyTransparent.add(m);
            }
            block = entity.getTargetBlock(legacyTransparent, 300);
        }
        if (block == null) {
            throw new Exception("Not targeting a block");
        }
        return block.getLocation();
    }

    static boolean isBlockAboveAir(final World world, final int x, final int y, final int z) {
        if (y > world.getMaxHeight()) {
            return true;
        }
        return HOLLOW_MATERIALS.contains(world.getBlockAt(x, y - 1, z).getType());
    }

    public static boolean isBlockUnsafeForUser(final IUser user, final World world, final int x, final int y, final int z) {
        if (user.getBase().isOnline() && world.equals(user.getBase().getWorld()) && (user.getBase().getGameMode() == GameMode.CREATIVE || user.getBase().getGameMode() == GameMode.SPECTATOR || user.isGodModeEnabled()) && user.getBase().getAllowFlight()) {
            return false;
        }

        if (isBlockDamaging(world, x, y, z)) {
            return true;
        }
        return isBlockAboveAir(world, x, y, z);
    }

    public static boolean isBlockUnsafe(final World world, final int x, final int y, final int z) {
        if (isBlockDamaging(world, x, y, z)) {
            return true;
        }
        return isBlockAboveAir(world, x, y, z);
    }

    public static boolean isBlockDamaging(final World world, final int x, final int y, final int z) {
        final Block below = world.getBlockAt(x, y - 1, z);
        if (below.getType() == Material.LAVA || below.getType() == Material.LAVA) {
            return true;
        }
        if (below.getType() == Material.FIRE) {
            return true;
        }
        if (below.getType().name().endsWith("_BED")) {
            return true;
        }
        if (world.getBlockAt(x, y, z).getType() == Material.NETHER_PORTAL) {
            return true;
        }
        return (!HOLLOW_MATERIALS.contains(world.getBlockAt(x, y, z).getType())) || (!HOLLOW_MATERIALS.contains(world.getBlockAt(x, y + 1, z).getType()));
    }

    // Not needed if using getSafeDestination(loc)
    public static Location getRoundedDestination(final Location loc) {
        final World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = (int) Math.round(loc.getY());
        int z = loc.getBlockZ();
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
        if (user.getBase().isOnline() && loc.getWorld().equals(user.getBase().getWorld()) && (user.getBase().getGameMode() == GameMode.CREATIVE || user.isGodModeEnabled()) && user.getBase().getAllowFlight()) {
            if (shouldFly(loc)) {
                user.getBase().setFlying(true);
            }
            // ess can be null if old deprecated method is calling it.
            System.out.println((ess == null) + " " + ess.getSettings().isTeleportToCenterLocation());
            if (ess == null || ess.getSettings().isTeleportToCenterLocation()) {
                return getRoundedDestination(loc);
            } else {
                return loc;
            }
        }
        return getSafeDestination(loc);
    }

    public static Location getSafeDestination(final Location loc) throws Exception {
        if (loc == null || loc.getWorld() == null) {
            throw new Exception(tl("destinationNotSet"));
        }
        final World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = (int) Math.round(loc.getY());
        int z = loc.getBlockZ();
        final int origX = x;
        final int origY = y;
        final int origZ = z;
        while (isBlockAboveAir(world, x, y, z)) {
            y -= 1;
            if (y < 0) {
                y = origY;
                break;
            }
        }
        if (isBlockUnsafe(world, x, y, z)) {
            x = Math.round(loc.getX()) == origX ? x - 1 : x + 1;
            z = Math.round(loc.getZ()) == origZ ? z - 1 : z + 1;
        }
        int i = 0;
        while (isBlockUnsafe(world, x, y, z)) {
            i++;
            if (i >= VOLUME.length) {
                x = origX;
                y = origY + RADIUS;
                z = origZ;
                break;
            }
            x = origX + VOLUME[i].x;
            y = origY + VOLUME[i].y;
            z = origZ + VOLUME[i].z;
        }
        while (isBlockUnsafe(world, x, y, z)) {
            y += 1;
            if (y >= world.getMaxHeight()) {
                x += 1;
                break;
            }
        }
        while (isBlockUnsafe(world, x, y, z)) {
            y -= 1;
            if (y <= 1) {
                x += 1;
                y = world.getHighestBlockYAt(x, z);
                if (x - 48 > loc.getBlockX()) {
                    throw new Exception(tl("holeInFloor"));
                }
            }
        }
        return new Location(world, x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
    }

    public static boolean shouldFly(Location loc) {
        final World world = loc.getWorld();
        final int x = loc.getBlockX();
        int y = (int) Math.round(loc.getY());
        final int z = loc.getBlockZ();
        int count = 0;
        while (LocationUtil.isBlockUnsafe(world, x, y, z) && y > -1) {
            y--;
            count++;
            if (count > 2) {
                return true;
            }
        }

        return y < 0;
    }
}

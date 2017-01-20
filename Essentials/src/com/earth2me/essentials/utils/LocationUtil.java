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
        HOLLOW_MATERIALS.add(Material.SAPLING);
        HOLLOW_MATERIALS.add(Material.POWERED_RAIL);
        HOLLOW_MATERIALS.add(Material.DETECTOR_RAIL);
        HOLLOW_MATERIALS.add(Material.LONG_GRASS);
        HOLLOW_MATERIALS.add(Material.DEAD_BUSH);
        HOLLOW_MATERIALS.add(Material.YELLOW_FLOWER);
        HOLLOW_MATERIALS.add(Material.RED_ROSE);
        HOLLOW_MATERIALS.add(Material.BROWN_MUSHROOM);
        HOLLOW_MATERIALS.add(Material.RED_MUSHROOM);
        HOLLOW_MATERIALS.add(Material.TORCH);
        HOLLOW_MATERIALS.add(Material.FIRE);
        HOLLOW_MATERIALS.add(Material.REDSTONE_WIRE);
        HOLLOW_MATERIALS.add(Material.CROPS);
        HOLLOW_MATERIALS.add(Material.LADDER);
        HOLLOW_MATERIALS.add(Material.RAILS);
        HOLLOW_MATERIALS.add(Material.LEVER);
        HOLLOW_MATERIALS.add(Material.REDSTONE_TORCH_OFF);
        HOLLOW_MATERIALS.add(Material.REDSTONE_TORCH_ON);
        HOLLOW_MATERIALS.add(Material.STONE_BUTTON);
        HOLLOW_MATERIALS.add(Material.SNOW);
        HOLLOW_MATERIALS.add(Material.SUGAR_CANE_BLOCK);
        HOLLOW_MATERIALS.add(Material.PORTAL);
        HOLLOW_MATERIALS.add(Material.DIODE_BLOCK_OFF);
        HOLLOW_MATERIALS.add(Material.DIODE_BLOCK_ON);
        HOLLOW_MATERIALS.add(Material.PUMPKIN_STEM);
        HOLLOW_MATERIALS.add(Material.MELON_STEM);
        HOLLOW_MATERIALS.add(Material.VINE);
        HOLLOW_MATERIALS.add(Material.WATER_LILY);
        HOLLOW_MATERIALS.add(Material.NETHER_WARTS);
        HOLLOW_MATERIALS.add(Material.ENDER_PORTAL);
        HOLLOW_MATERIALS.add(Material.COCOA);
        HOLLOW_MATERIALS.add(Material.TRIPWIRE_HOOK);
        HOLLOW_MATERIALS.add(Material.TRIPWIRE);
        HOLLOW_MATERIALS.add(Material.FLOWER_POT);
        HOLLOW_MATERIALS.add(Material.CARROT);
        HOLLOW_MATERIALS.add(Material.POTATO);
        HOLLOW_MATERIALS.add(Material.WOOD_BUTTON);
        HOLLOW_MATERIALS.add(Material.SKULL);
        HOLLOW_MATERIALS.add(Material.REDSTONE_COMPARATOR_OFF);
        HOLLOW_MATERIALS.add(Material.REDSTONE_COMPARATOR_ON);
        HOLLOW_MATERIALS.add(Material.ACTIVATOR_RAIL);
        HOLLOW_MATERIALS.add(Material.CARPET);
        HOLLOW_MATERIALS.add(Material.DOUBLE_PLANT);

        // Additional Materials added in by Essentials
        HOLLOW_MATERIALS.add(Material.SEEDS);
        HOLLOW_MATERIALS.add(Material.SIGN_POST);
        HOLLOW_MATERIALS.add(Material.WOODEN_DOOR);
        HOLLOW_MATERIALS.add(Material.WALL_SIGN);
        HOLLOW_MATERIALS.add(Material.STONE_PLATE);
        HOLLOW_MATERIALS.add(Material.IRON_DOOR_BLOCK);
        HOLLOW_MATERIALS.add(Material.WOOD_PLATE);
        HOLLOW_MATERIALS.add(Material.FENCE_GATE);

        TRANSPARENT_MATERIALS.addAll(HOLLOW_MATERIALS);
        TRANSPARENT_MATERIALS.add(Material.WATER);
        TRANSPARENT_MATERIALS.add(Material.STATIONARY_WATER);
    }

    public static final int RADIUS = 3;
    public static final Vector3D[] VOLUME;

    public static ItemStack convertBlockToItem(final Block block) {
        final ItemStack is = new ItemStack(block.getType(), 1, (short) 0, block.getData());
        switch (is.getType()) {
            case WOODEN_DOOR:
                is.setType(Material.WOOD_DOOR);
                is.setDurability((short) 0);
                break;
            case IRON_DOOR_BLOCK:
                is.setType(Material.IRON_DOOR);
                is.setDurability((short) 0);
                break;
            case SIGN_POST:
            case WALL_SIGN:
                is.setType(Material.SIGN);
                is.setDurability((short) 0);
                break;
            case CROPS:
                is.setType(Material.SEEDS);
                is.setDurability((short) 0);
                break;
            case CAKE_BLOCK:
                is.setType(Material.CAKE);
                is.setDurability((short) 0);
                break;
            case BED_BLOCK:
                is.setType(Material.BED);
                is.setDurability((short) 0);
                break;
            case REDSTONE_WIRE:
                is.setType(Material.REDSTONE);
                is.setDurability((short) 0);
                break;
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
                is.setType(Material.REDSTONE_TORCH_ON);
                is.setDurability((short) 0);
                break;
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
                is.setType(Material.DIODE);
                is.setDurability((short) 0);
                break;
            case DOUBLE_STEP:
                is.setType(Material.STEP);
                break;
            case TORCH:
            case RAILS:
            case LADDER:
            case WOOD_STAIRS:
            case COBBLESTONE_STAIRS:
            case LEVER:
            case STONE_BUTTON:
            case FURNACE:
            case DISPENSER:
            case PUMPKIN:
            case JACK_O_LANTERN:
            case WOOD_PLATE:
            case STONE_PLATE:
            case PISTON_STICKY_BASE:
            case PISTON_BASE:
            case IRON_FENCE:
            case THIN_GLASS:
            case TRAP_DOOR:
            case FENCE:
            case FENCE_GATE:
            case NETHER_FENCE:
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
            HashSet<Byte> legacyTransparent = new HashSet<>(); // Bukkit API prevents declaring as Set<Byte>
            for (Material m : TRANSPARENT_MATERIALS) {
                legacyTransparent.add((byte) m.getId());
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
        if (user.getBase().isOnline() && world.equals(user.getBase().getWorld()) && (user.getBase().getGameMode() == GameMode.CREATIVE || user.isGodModeEnabled()) && user.getBase().getAllowFlight()) {
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
        if (below.getType() == Material.LAVA || below.getType() == Material.STATIONARY_LAVA) {
            return true;
        }
        if (below.getType() == Material.FIRE) {
            return true;
        }
        if (below.getType() == Material.BED_BLOCK) {
            return true;
        }
        if (world.getBlockAt(x, y, z).getType() == Material.PORTAL) {
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

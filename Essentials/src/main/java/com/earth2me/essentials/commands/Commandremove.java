package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Mob;
import com.earth2me.essentials.User;
import com.google.common.collect.Lists;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Flying;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.WaterMob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;

// This could be rewritten in a simpler form if we made a mapping of all Entity names to their types (which would also provide possible mod support)

public class Commandremove extends EssentialsCommand {
    public Commandremove() {
        super("remove");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        World world = user.getWorld();
        int radius = 0;
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        if (args.length >= 2) {
            try {
                radius = Integer.parseInt(args[1]);
            } catch (final NumberFormatException e) {
                world = ess.getWorld(args[1]);
            }
        }
        if (args.length >= 3) {
            // This is to prevent breaking the old syntax
            radius = 0;
            world = ess.getWorld(args[2]);
        }
        parseCommand(server, user.getSource(), args, world, radius);

    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }
        final World world = ess.getWorld(args[1]);
        parseCommand(server, sender, args, world, 0);
    }

    private void parseCommand(final Server server, final CommandSource sender, final String[] args, final World world, final int radius) throws Exception {
        final List<String> types = new ArrayList<>();
        final List<String> customTypes = new ArrayList<>();

        if (world == null) {
            throw new Exception(tl("invalidWorld"));
        }

        if (args[0].contentEquals("*") || args[0].contentEquals("all")) {
            types.add(0, "ALL");
        } else {
            for (final String entityType : args[0].split(",")) {
                ToRemove toRemove;
                try {
                    toRemove = ToRemove.valueOf(entityType.toUpperCase(Locale.ENGLISH));
                } catch (final Exception e) {
                    try {
                        toRemove = ToRemove.valueOf(entityType.concat("S").toUpperCase(Locale.ENGLISH));
                    } catch (final Exception ee) {
                        toRemove = ToRemove.CUSTOM;
                        customTypes.add(entityType);
                    }
                }
                types.add(toRemove.toString());
            }
        }
        removeHandler(sender, types, customTypes, world, radius);
    }

    private void removeHandler(final CommandSource sender, final List<String> types, final List<String> customTypes, final World world, int radius) {
        int removed = 0;
        if (radius > 0) {
            radius *= radius;
        }

        final ArrayList<ToRemove> removeTypes = new ArrayList<>();
        final ArrayList<Mob> customRemoveTypes = new ArrayList<>();

        for (final String s : types) {
            removeTypes.add(ToRemove.valueOf(s));
        }

        boolean warnUser = false;

        for (final String s : customTypes) {
            final Mob mobType = Mob.fromName(s);
            if (mobType == null) {
                warnUser = true;
            } else {
                customRemoveTypes.add(mobType);
            }
        }

        if (warnUser) {
            sender.sendMessage(tl("invalidMob"));
        }

        for (final Chunk chunk : world.getLoadedChunks()) {
            for (final Entity e : chunk.getEntities()) {
                if (radius > 0) {
                    if (sender.getPlayer().getLocation().distanceSquared(e.getLocation()) > radius) {
                        continue;
                    }
                }
                if (e instanceof HumanEntity) {
                    continue;
                }

                for (final ToRemove toRemove : removeTypes) {

                    // We should skip any animals tamed by players unless we are specifially targetting them.
                    if (e instanceof Tameable && ((Tameable) e).isTamed() && (((Tameable) e).getOwner() instanceof Player || ((Tameable) e).getOwner() instanceof OfflinePlayer) && !removeTypes.contains(ToRemove.TAMED)) {
                        continue;
                    }

                    // We should skip any NAMED animals unless we are specifially targetting them.
                    if (e instanceof LivingEntity && e.getCustomName() != null && !removeTypes.contains(ToRemove.NAMED)) {
                        continue;
                    }

                    switch (toRemove) {
                        case TAMED:
                            if (e instanceof Tameable && ((Tameable) e).isTamed()) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case NAMED:
                            if (e instanceof LivingEntity && e.getCustomName() != null) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case DROPS:
                            if (e instanceof Item) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case ARROWS:
                            if (e instanceof Projectile) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case BOATS:
                            if (e instanceof Boat) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case MINECARTS:
                            if (e instanceof Minecart) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case XP:
                            if (e instanceof ExperienceOrb) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case PAINTINGS:
                            if (e instanceof Painting) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case ITEMFRAMES:
                            if (e instanceof ItemFrame) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case ENDERCRYSTALS:
                            if (e instanceof EnderCrystal) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case AMBIENT:
                            if (e instanceof Flying) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case HOSTILE:
                        case MONSTERS:
                            if (e instanceof Monster || e instanceof ComplexLivingEntity || e instanceof Flying || e instanceof Slime) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case PASSIVE:
                        case ANIMALS:
                            if (e instanceof Animals || e instanceof NPC || e instanceof Snowman || e instanceof WaterMob || e instanceof Ambient) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case MOBS:
                            if (e instanceof Animals || e instanceof NPC || e instanceof Snowman || e instanceof WaterMob || e instanceof Monster || e instanceof ComplexLivingEntity || e instanceof Flying || e instanceof Slime || e instanceof Ambient) {
                                e.remove();
                                removed++;
                            }
                            break;
                        case ENTITIES:
                        case ALL:
                            e.remove();
                            removed++;
                            break;
                        case CUSTOM:
                            for (final Mob type : customRemoveTypes) {
                                if (e.getType() == type.getType()) {
                                    e.remove();
                                    removed++;
                                }
                            }
                            break;
                    }
                }
            }
        }
        sender.sendMessage(tl("removed", removed));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final List<String> options = Lists.newArrayList();
            for (final ToRemove toRemove : ToRemove.values()) {
                options.add(toRemove.name().toLowerCase(Locale.ENGLISH));
            }
            return options;
        } else if (args.length == 2) {
            final List<String> worlds = Lists.newArrayList();
            for (final World world : server.getWorlds()) {
                worlds.add(world.getName());
            }
            return worlds;
        } else {
            return Collections.emptyList();
        }
    }

    private enum ToRemove {
        DROPS,
        ARROWS,
        BOATS,
        MINECARTS,
        XP,
        PAINTINGS,
        ITEMFRAMES,
        ENDERCRYSTALS,
        HOSTILE,
        MONSTERS,
        PASSIVE,
        ANIMALS,
        AMBIENT,
        MOBS,
        ENTITIES,
        ALL,
        CUSTOM,
        TAMED,
        NAMED
    }
}

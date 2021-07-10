package net.ess3.nms.refl.providers;

import net.ess3.provider.AbstractAchievementEvent;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

@SuppressWarnings("deprecation")
public class AchievementListenerProvider implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAchievement(final PlayerAchievementAwardedEvent event) {
        Bukkit.getPluginManager().callEvent(new AbstractAchievementEvent(event.getPlayer(), getAchievementName(event.getAchievement())));
    }

    private String getAchievementName(final Achievement achievement) {
        switch (achievement) {
            case OPEN_INVENTORY: {
                return "Taking Inventory";
            }
            case MINE_WOOD: {
                return "Getting Wood";
            }
            case BUILD_WORKBENCH: {
                return "Benchmarking";
            }
            case BUILD_PICKAXE: {
                return "Time to Mine!";
            }
            case BUILD_FURNACE: {
                return "Hot Topic";
            }
            case ACQUIRE_IRON: {
                return "Acquire Hardware!";
            }
            case BUILD_HOE: {
                return "Time to Farm!";
            }
            case MAKE_BREAD: {
                return "Bake Bread";
            }
            case BAKE_CAKE: {
                return "The Lie";
            }
            case BUILD_BETTER_PICKAXE: {
                return "Getting an Upgrade";
            }
            case COOK_FISH: {
                return "Delicious Fish";
            }
            case ON_A_RAIL: {
                return "On A Rail";
            }
            case BUILD_SWORD: {
                return "Time to Strike";
            }
            case KILL_ENEMY: {
                return "Monster Hunter";
            }
            case KILL_COW: {
                return "Cow Tipper";
            }
            case FLY_PIG: {
                return "When Pigs Fly!";
            }
            case SNIPE_SKELETON: {
                return "Sniper Duel";
            }
            case GET_DIAMONDS: {
                return "DIAMONDS!";
            }
            case NETHER_PORTAL: {
                return "We Need to Go Deeper";
            }
            case GHAST_RETURN: {
                return "Return to Sender";
            }
            case GET_BLAZE_ROD: {
                return "Into Fire";
            }
            case BREW_POTION: {
                return "Local Brewery";
            }
            case END_PORTAL: {
                return "The End?";
            }
            case THE_END: {
                return "The End.";
            }
            case ENCHANTMENTS: {
                return "Enchanter";
            }
            case OVERKILL: {
                return "Overkill";
            }
            case BOOKCASE: {
                return "Librarian";
            }
            case EXPLORE_ALL_BIOMES: {
                return "Adventuring Time";
            }
            case SPAWN_WITHER: {
                return "The Beginning?";
            }
            case KILL_WITHER: {
                return "The Beginning.";
            }
            case FULL_BEACON: {
                return "Beaconator";
            }
            case BREED_COW: {
                return "Repopulation";
            }
            case DIAMONDS_TO_YOU: {
                return "Diamonds to you!";
            }
            case OVERPOWERED: {
                return "Overpowered";
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
}

package net.ess3.api;

import com.earth2me.essentials.IConf;
import org.bukkit.Material;

import java.util.List;
import java.util.Set;

/**
 * Provides access to the storage of item groups. Maintainers should add methods to <i>this interface</i>.
 */
public interface IItemGroups extends IConf {

    /**
     * Gets the set of saved item groups from config file
     * @return the set of item groups
     */
    Set<String> getItemGroups();

    /**
     * Gets the list of item materials inside an item group
     * @param group the item group
     * @return a list of items in the item group
     */
    List<Material> getItemGroup(String group);
}

package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.Bukkit;


public class AfkStatusChangeEvent extends StatusChangeEvent {
    public AfkStatusChangeEvent(IUser affected, boolean value) {
        super(affected, affected, value);
    }
}

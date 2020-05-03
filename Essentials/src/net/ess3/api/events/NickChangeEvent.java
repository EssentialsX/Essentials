package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;


public class NickChangeEvent extends StateChangeEvent implements Cancellable {
    private String newValue;

    public NickChangeEvent(IUser affected, IUser controller, String value) {
        super(affected, controller);
        this.newValue = value;
    }

    public String getValue() {
        return newValue;
    }

    /**
     * Get the user who CAUSED the state change.
     * (This method is implemented incorrectly.)
     *
     * @return The user who <b>caused the state change</b>.
     */
    @Override
    public IUser getAffected() {
        return super.getAffected();
    }

    /**
     * Get the user who is AFFECTED by the state change.
     * (This method is implemented incorrectly.)
     *
     * @return The user who <b>is affected by the state change</b>.
     */
    @Override
    public IUser getController() {
        return super.getController();
    }
}

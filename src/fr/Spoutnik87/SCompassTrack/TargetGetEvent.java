package fr.Spoutnik87.SCompassTrack;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TargetGetEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player tracker;
	private Player target;
	
	/**
	 * @param arg0 Tracker.
	 * @param arg1 Target.
	 */
	public TargetGetEvent(Player arg0, Player arg1) {
		this.tracker = arg0;
		this.target = arg1;
	}
	
	/**
	 * @return Tracker.
	 */
	public Player getTracker() {
		return tracker;
	}
	
	/**
	 * @return Target.
	 */
	public Player getTarget() {
		return target;
	}
	
	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

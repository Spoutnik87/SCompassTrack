package fr.Spoutnik87.SCompassTrack;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TargetSearchEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player tracker;
	
	/**
	 * @param arg0 Tracker.
	 */
	public TargetSearchEvent(Player arg0) {
		this.tracker = arg0;
	}
	
	/**
	 * @return Tracker.
	 */
	public Player getTracker() {
		return tracker;
	}
	
	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

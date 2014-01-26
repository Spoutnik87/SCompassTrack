package fr.Spoutnik87.SCompassTrack;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TargetLoseEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player tracker;
	private Player lostTarget;
	
	/**
	 * @param arg0 Tracker.
	 * @param arg1 Target lost.
	 */
	public TargetLoseEvent(Player arg0, Player arg1) {
		this.tracker = arg0;
		this.lostTarget = arg1;
	}
	
	/**
	 * @return Tracker.
	 */
	public Player getTracker() {
		return tracker;
	}
	
	/**
	 * @return Lost target.
	 */
	public Player getLostTarget() {
		return lostTarget;
	}
	
	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

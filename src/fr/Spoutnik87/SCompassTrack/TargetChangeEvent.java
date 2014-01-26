package fr.Spoutnik87.SCompassTrack;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TargetChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player tracker;
	private Player lostTarget;
	private Player target;
	
	/**
	 * @param arg0 Tracker.
	 * @param arg1 Target lost.
	 * @param arg2 Target.
	 */
	public TargetChangeEvent(Player arg0, Player arg1, Player arg2) {
		this.tracker = arg0;
		this.lostTarget = arg1;
		this.target = arg2;
	}
	
	/**
	 * @return Tracker.
	 */
	public Player getTracker() {
		return tracker;
	}
	
	/**
	 * @return Last target.
	 */
	public Player getLostTarget() {
		return lostTarget;
	}
	
	/**
	 * @return New target.
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

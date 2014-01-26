package fr.Spoutnik87.SCompassTrack;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 
 * @author Spoutnik87
 */
public class SCompassTrack implements Listener {

	// General values
	private Main main;
	private HashMap<Player, Player> target;
	private double d;

	// Config file values
	private String prefix;
	private String noplayer;
	private String nopermission;
	private String spawntarget;
	private String playertarget;
	private String incorrectworld;
	private String lang;
	private double distance;
	/** Right click to track other player */
	private boolean rctp;
	/** Plugin activated on other worlds */
	private boolean paow;
	private boolean useworldwhitelist;

	private List<String> worldwhitelist;

	public SCompassTrack(Main main) {
		this.main = main;
		this.readConfig();
		this.target = new HashMap<Player, Player>();
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		for (Map.Entry<Player, Player> mapEntry : target.entrySet()) {
			if (mapEntry.getKey() == event.getPlayer()) {
				Bukkit.getServer().getPluginManager().callEvent(new TargetLoseEvent((Player) mapEntry.getKey(), (Player) mapEntry.getValue()));
				this.target.remove(mapEntry.getKey());
			}
			if (mapEntry.getValue() == event.getPlayer()) {
				((Player) mapEntry.getKey()).sendMessage(prefix + convertConfig(noplayer, null, null, 0));
				Bukkit.getServer().getPluginManager().callEvent(new TargetLoseEvent((Player) mapEntry.getKey(), (Player) mapEntry.getValue()));
				this.target.remove(mapEntry.getKey());
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (!paow && worldwhitelist != null) if (!worldwhitelist.contains(p.getWorld().getName())) return;
		else if (!paow && worldwhitelist == null) return;
		Player ptarget = null;
		if (event.getMaterial() != Material.COMPASS) return;
		Action a1;
		Action a2;
		Action b1;
		Action b2;
		if (rctp) {
			a1 = Action.RIGHT_CLICK_AIR;
			a2 = Action.RIGHT_CLICK_BLOCK;
			b1 = Action.LEFT_CLICK_AIR;
			b2 = Action.LEFT_CLICK_BLOCK;
		} else {
			a1 = Action.LEFT_CLICK_AIR;
			a2 = Action.LEFT_CLICK_BLOCK;
			b1 = Action.RIGHT_CLICK_AIR;
			b2 = Action.RIGHT_CLICK_BLOCK;
		}
		if (useworldwhitelist && worldwhitelist != null) {
			if (!worldwhitelist.contains(p.getWorld().getName())) {
				p.sendMessage(prefix + convertConfig(incorrectworld, p, ptarget, 0));
				return;
			}
		} else if (useworldwhitelist && worldwhitelist == null) {
			p.sendMessage(prefix + convertConfig(incorrectworld, p, ptarget, 0));
			return;
		}
		if ((event.getAction().equals(a1)) || (event.getAction().equals(a2))) {
			Bukkit.getServer().getPluginManager().callEvent(new TargetSearchEvent(p));
			if (!p.hasPermission("scompasstrack.track")) {
				p.sendMessage(prefix + convertConfig(nopermission, p, ptarget, 0));
				return;
			}

			for (Player p2 : Bukkit.getServer().getOnlinePlayers()) {
				if ((p.getWorld() == p2.getWorld()) && (p != p2) && (ptarget == null)) {
					d = p.getLocation().distance(p2.getLocation());
					if (d <= distance) ptarget = p2;
				}
			}

			if (ptarget == null) {
				p.setCompassTarget(p.getWorld().getSpawnLocation());
				p.sendMessage(prefix + convertConfig(noplayer, p, ptarget, 0));
				return;
			}
			if (ptarget.hasPermission("scompasstrack.untrackable")) {
				p.setCompassTarget(p.getWorld().getSpawnLocation());
				p.sendMessage(prefix + convertConfig(noplayer, p, ptarget, 0));
				return;
			}

			d = Math.round(d);
			int roundD = (int) d;
			p.sendMessage(prefix + convertConfig(playertarget, p, ptarget, roundD));
			if (target.get(p) != null) Bukkit.getServer().getPluginManager().callEvent(new TargetChangeEvent(p, target.get(p), ptarget));
			else Bukkit.getServer().getPluginManager().callEvent(new TargetGetEvent(p, ptarget));
			this.target.put(p, ptarget);
		}

		if ((event.getAction().equals(b1)) || (event.getAction().equals(b2))) {
			p.setCompassTarget(p.getWorld().getSpawnLocation());
			p.sendMessage(prefix + convertConfig(spawntarget, p, ptarget, 0));
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (this.target.get(event.getPlayer()) != null) event.getPlayer().setCompassTarget(((Player) this.target.get(event.getPlayer())).getLocation());
		else return;

		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			Player ct = (Player) this.target.get(p);
			if (ct == event.getPlayer())
				if (p.getWorld() == event.getPlayer().getWorld()) {
					if (p.getLocation().distance(event.getPlayer().getLocation()) <= this.distance) {
						Bukkit.getServer().getPluginManager().callEvent(new TargetLoseEvent(p, target.get(p)));
						this.target.remove(p);
						p.setCompassTarget(p.getWorld().getSpawnLocation());
					}
					p.setCompassTarget(event.getPlayer().getLocation());
				} 
				else {
					p.sendMessage(prefix + convertConfig(noplayer, p, null, 0));
					Bukkit.getServer().getPluginManager().callEvent(new TargetLoseEvent(p, target.get(p)));
					this.target.remove(p);
				}
		}
	}

	/** Read the config file */
	public void readConfig() {
		prefix = convertConfig(
				main.getConfig().getString("SCompassTrack.general.prefix"),
				null, null, 0);
		lang = main.getConfig().getString("SCompassTrack.general.lang");
		rctp = main.getConfig().getBoolean("SCompassTrack.general.userightclicktotrackplayer");
		worldwhitelist = main.getConfig().getStringList("SCompassTrack.general.worldwhitelist");
		noplayer = main.getConfig().getString("SCompassTrack.lang." + lang + ".noplayer");
		nopermission = main.getConfig().getString("SCompassTrack.lang." + lang + ".nopermission");
		spawntarget = main.getConfig().getString("SCompassTrack.lang." + lang + ".spawntarget");
		playertarget = main.getConfig().getString("SCompassTrack.lang." + lang + ".playertarget");
		incorrectworld = main.getConfig().getString("SCompassTrack.lang." + lang + ".incorrectworld");
		distance = Double.parseDouble(main.getConfig().getString("SCompassTrack.general.radius"));
		paow = main.getConfig().getBoolean("SCompassTrack.general.pluginactivatedonotherworld");
		useworldwhitelist = main.getConfig().getBoolean("SCompassTrack.general.useworldwhitelist");
	}

	public static String convertConfig(String s, Player p, Player ptarget, int d) {
		while (s.contains("@ptarget")) {
			String str = s.substring(s.indexOf("@ptarget")+8);
			s = s.substring(0, s.indexOf("@ptarget")) + ptarget.getName() + str;
		}
		while (s.contains("@p")) {
			String str = s.substring(s.indexOf("@p")+2);
			s = s.substring(0, s.indexOf("@p")) + p.getName() + str;
		}
		while (s.contains("@d")) {
			String str = s.substring(s.indexOf("@d")+2);
			s = s.substring(0, s.indexOf("@d")) + d + str;
		}
		while (s.contains("&4")) {
			String str = s.substring(s.indexOf("&4")+2);
			s = s.substring(0, s.indexOf("&4")) + ChatColor.DARK_RED + str;
		}
		while (s.contains("&c")) {
			String str = s.substring(s.indexOf("&c")+2);
			s = s.substring(0, s.indexOf("&c")) + ChatColor.RED + str;
		}
		while (s.contains("&6")) {
			String str = s.substring(s.indexOf("&6")+2);
			s = s.substring(0, s.indexOf("&6")) + ChatColor.GOLD + str;
		}
		while (s.contains("&e")) {
			String str = s.substring(s.indexOf("&e")+2);
			s = s.substring(0, s.indexOf("&e")) + ChatColor.YELLOW + str;
		}
		while (s.contains("&2")) {
			String str = s.substring(s.indexOf("&2")+2);
			s = s.substring(0, s.indexOf("&2")) + ChatColor.DARK_GREEN + str;
		}
		while (s.contains("&a")) {
			String str = s.substring(s.indexOf("&a")+2);
			s = s.substring(0, s.indexOf("&a")) + ChatColor.GREEN + str;
		}
		while (s.contains("&b")) {
			String str = s.substring(s.indexOf("&b")+2);
			s = s.substring(0, s.indexOf("&b")) + ChatColor.AQUA + str;
		}
		while (s.contains("&3")) {
			String str = s.substring(s.indexOf("&3")+2);
			s = s.substring(0, s.indexOf("&3")) + ChatColor.DARK_AQUA + str;
		}
		while (s.contains("&1")) {
			String str = s.substring(s.indexOf("&1")+2);
			s = s.substring(0, s.indexOf("&1")) + ChatColor.DARK_BLUE + str;
		}
		while (s.contains("&9")) {
			String str = s.substring(s.indexOf("&9")+2);
			s = s.substring(0, s.indexOf("&9")) + ChatColor.AQUA + str;
		}
		while (s.contains("&d")) {
			String str = s.substring(s.indexOf("&d")+2);
			s = s.substring(0, s.indexOf("&d")) + ChatColor.LIGHT_PURPLE + str;
		}
		while (s.contains("&5")) {
			String str = s.substring(s.indexOf("&5")+2);
			s = s.substring(0, s.indexOf("&5")) + ChatColor.DARK_PURPLE + str;
		}
		while (s.contains("&f")) {
			String str = s.substring(s.indexOf("&f")+2);
			s = s.substring(0, s.indexOf("&f")) + ChatColor.WHITE + str;
		}
		while (s.contains("&7")) {
			String str = s.substring(s.indexOf("&7")+2);
			s = s.substring(0, s.indexOf("&7")) + ChatColor.GRAY + str;
		}
		while (s.contains("&8")) {
			String str = s.substring(s.indexOf("&8")+2);
			s = s.substring(0, s.indexOf("&8")) + ChatColor.DARK_GRAY + str;
		}
		while (s.contains("&0")) {
			String str = s.substring(s.indexOf("&0")+2);
			s = s.substring(0, s.indexOf("&0")) + ChatColor.BLACK + str;
		}
		return s;
	}
}
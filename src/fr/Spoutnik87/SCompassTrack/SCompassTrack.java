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
@SuppressWarnings("rawtypes")
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

	private List worldwhitelist;

	public SCompassTrack(Main main) {
		this.main = main;
		this.readConfig();
		this.target = new HashMap<Player, Player>();
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		for (Map.Entry mapEntry : this.target.entrySet()) {
			if (mapEntry.getKey() == event.getPlayer()) {
				this.target.remove(mapEntry.getKey());
			}
			if (mapEntry.getValue() == event.getPlayer()) {
				((Player) mapEntry.getKey()).sendMessage(prefix
						+ convertConfig(noplayer, null, null, 0));
				this.target.remove(mapEntry.getKey());
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (!paow && worldwhitelist != null) {
			if (!worldwhitelist.contains(p.getWorld().getName())) return;
		}
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
			if (!p.hasPermission("scompasstrack.track")) {
				p.sendMessage(prefix
						+ convertConfig(nopermission, p, ptarget, 0).toString());
				return;
			}

			for (Player p2 : Bukkit.getServer().getOnlinePlayers()) {
				if ((p.getWorld() == p2.getWorld()) && (p != p2)
						&& (ptarget == null)) {
					d = p.getLocation().distance(p2.getLocation());
					if (d <= distance) {
						ptarget = p2;
					}
				}
			}

			if (ptarget == null) {
				p.setCompassTarget(p.getWorld().getSpawnLocation());
				p.sendMessage(prefix
						+ convertConfig(noplayer, p, ptarget, 0).toString());
				return;
			}
			if (ptarget.hasPermission("scompasstrack.untrackable")) {
				p.setCompassTarget(p.getWorld().getSpawnLocation());
				p.sendMessage(prefix
						+ convertConfig(noplayer, p, ptarget, 0).toString());
				return;
			}

			d = Math.round(d);
			int roundD = (int) d;
			p.sendMessage(prefix
					+ convertConfig(playertarget, p, ptarget, roundD));
			this.target.put(p, ptarget);
		}

		if ((event.getAction().equals(b1)) || (event.getAction().equals(b2))) {
			p.setCompassTarget(p.getWorld().getSpawnLocation());
			p.sendMessage(prefix + convertConfig(spawntarget, p, ptarget, 0));
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (this.target.get(event.getPlayer()) != null) {
			event.getPlayer()
					.setCompassTarget(
							((Player) this.target.get(event.getPlayer()))
									.getLocation());
		}

		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			Player ct = (Player) this.target.get(p);
			if (ct == event.getPlayer())
				if (p.getWorld() == event.getPlayer().getWorld()) {
					if (p.getLocation().distance(
							event.getPlayer().getLocation()) <= this.distance) {
						this.target.remove(p);
						p.setCompassTarget(p.getWorld().getSpawnLocation());
					}
					p.setCompassTarget(event.getPlayer().getLocation());
				} else {
					p.sendMessage(prefix + convertConfig(noplayer, p, null, 0));
					this.target.remove(p);
				}
		}
	}

	/** Read the config file */
	public void readConfig() {
		prefix = convertConfig(main.getConfig().getString("SCompassTrack.general.prefix"),null, null, 0).toString();
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

	public static StringBuffer convertConfig(String configValue, Player p,
			Player ptarget, int d) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(configValue);

		while (stringBufferContains(buffer, "@ptarget")) {
			String str = buffer.substring(buffer.indexOf("@ptarget")
					+ "@ptarget".length());
			buffer.replace((buffer.indexOf("@ptarget")), buffer.length(), "");
			buffer.append(ptarget.getName());
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "@p")) {
			String str = buffer.substring(buffer.indexOf("@p") + "@p".length());
			buffer.replace((buffer.indexOf("@p")), buffer.length(), "");
			buffer.append(p.getName());
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "@d")) {
			String str = buffer.substring(buffer.indexOf("@d") + "@d".length());
			buffer.replace((buffer.indexOf("@d")), buffer.length(), "");
			buffer.append(d);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&4")) {
			String str = buffer.substring(buffer.indexOf("&4") + "&4".length());
			buffer.replace((buffer.indexOf("&4")), buffer.length(), "");
			buffer.append(ChatColor.DARK_RED);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&c")) {
			String str = buffer.substring(buffer.indexOf("&c") + "&c".length());
			buffer.replace((buffer.indexOf("&c")), buffer.length(), "");
			buffer.append(ChatColor.RED);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&6")) {
			String str = buffer.substring(buffer.indexOf("&6") + "&6".length());
			buffer.replace((buffer.indexOf("&6")), buffer.length(), "");
			buffer.append(ChatColor.GOLD);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&e")) {
			String str = buffer.substring(buffer.indexOf("&e") + "&e".length());
			buffer.replace((buffer.indexOf("&e")), buffer.length(), "");
			buffer.append(ChatColor.YELLOW);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&2")) {
			String str = buffer.substring(buffer.indexOf("&2") + "&2".length());
			buffer.replace((buffer.indexOf("&2")), buffer.length(), "");
			buffer.append(ChatColor.DARK_GREEN);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&a")) {
			String str = buffer.substring(buffer.indexOf("&a") + "&a".length());
			buffer.replace((buffer.indexOf("&a")), buffer.length(), "");
			buffer.append(ChatColor.GREEN);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&b")) {
			String str = buffer.substring(buffer.indexOf("&b") + "&b".length());
			buffer.replace((buffer.indexOf("&b")), buffer.length(), "");
			buffer.append(ChatColor.AQUA);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&3")) {
			String str = buffer.substring(buffer.indexOf("&3") + "&3".length());
			buffer.replace((buffer.indexOf("&3")), buffer.length(), "");
			buffer.append(ChatColor.DARK_AQUA);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&1")) {
			String str = buffer.substring(buffer.indexOf("&1") + "&1".length());
			buffer.replace((buffer.indexOf("&1")), buffer.length(), "");
			buffer.append(ChatColor.DARK_BLUE);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&9")) {
			String str = buffer.substring(buffer.indexOf("&9") + "&9".length());
			buffer.replace((buffer.indexOf("&9")), buffer.length(), "");
			buffer.append(ChatColor.AQUA);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&d")) {
			String str = buffer.substring(buffer.indexOf("&d") + "&d".length());
			buffer.replace((buffer.indexOf("&d")), buffer.length(), "");
			buffer.append(ChatColor.LIGHT_PURPLE);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&5")) {
			String str = buffer.substring(buffer.indexOf("&5") + "&5".length());
			buffer.replace((buffer.indexOf("&5")), buffer.length(), "");
			buffer.append(ChatColor.DARK_PURPLE);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&f")) {
			String str = buffer.substring(buffer.indexOf("&f") + "&f".length());
			buffer.replace((buffer.indexOf("&f")), buffer.length(), "");
			buffer.append(ChatColor.WHITE);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&7")) {
			String str = buffer.substring(buffer.indexOf("&7") + "&7".length());
			buffer.replace((buffer.indexOf("&7")), buffer.length(), "");
			buffer.append(ChatColor.GRAY);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&8")) {
			String str = buffer.substring(buffer.indexOf("&8") + "&8".length());
			buffer.replace((buffer.indexOf("&8")), buffer.length(), "");
			buffer.append(ChatColor.DARK_GRAY);
			buffer.append(str);
		}
		while (stringBufferContains(buffer, "&0")) {
			String str = buffer.substring(buffer.indexOf("&0") + "&0".length());
			buffer.replace((buffer.indexOf("&0")), buffer.length(), "");
			buffer.append(ChatColor.BLACK);
			buffer.append(str);
		}
		return buffer;
	}

	/** Method contains for StringBuffer */
	public static boolean stringBufferContains(StringBuffer buffer, String s) {
		if (buffer.indexOf(s) != -1) {
			return true;
		} else
			return false;
	}
}
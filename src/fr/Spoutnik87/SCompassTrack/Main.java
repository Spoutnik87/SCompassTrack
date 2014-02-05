package fr.Spoutnik87.SCompassTrack;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Spoutnik87
 */

public class Main extends JavaPlugin {
	public static Logger logger = Logger.getLogger("Minecraft");

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new SCompassTrack(this), this);
		getConfig().options().copyDefaults(true);
		saveConfig();
		logger.info("[SCompassTrack] Config loaded");
		logger.info("[SCompassTrack] Plugin loaded");
		if (SCompassTrack.checkUpdate()) 
			new Update(60477, getDescription().getName() + " v" + getDescription().getVersion());
	}

	@Override
	public void onDisable() {
		logger.info("[SCompassTrack] Config unloaded");
		logger.info("[SCompassTrack] Plugin unloaded");
	}
}
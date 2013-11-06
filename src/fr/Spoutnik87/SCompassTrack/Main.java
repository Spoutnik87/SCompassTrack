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
    
    UpdateChecker uc = new UpdateChecker(this, "http://dev.bukkit.org/bukkit-plugins/scompasstrack/files.rss");
    if (uc.checkUpdate()) {
    	logger.info("[SCompassTrack] A new update is available: " + uc.getVersion());
    	logger.info("Get it from: " + uc.getLink());
    }
    
  }
  public void onDisable() {
      logger.info("[SCompassTrack] Config unloaded");
      logger.info("[SCompassTrack] Plugin unloaded");
  }
}
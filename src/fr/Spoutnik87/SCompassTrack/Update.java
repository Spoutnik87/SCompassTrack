package fr.Spoutnik87.SCompassTrack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Update {
    private final int projectID;
    private final String apiKey;
    private static final String API_LINK_VALUE = "downloadUrl";
    private static final String API_QUERY = "/servermods/files?projectIds=";
    private static final String API_HOST = "https://api.curseforge.com";
    private String CURRENT_VERSION = "";
    
    /**
     * Check for updates anonymously (keyless)
     *
     * @param projectID The BukkitDev Project ID, found in the "Facts" panel on the right-side of your project page.
     */
    public Update(int projectID, String currentVersion) {
        this(projectID, null, currentVersion);
    }

    /**
     * Check for updates using your Curse account (with key)
     *
     * @param projectID The BukkitDev Project ID, found in the "Facts" panel on the right-side of your project page.
     * @param apiKey Your ServerMods API key, found at https://dev.bukkit.org/home/servermods-apikey/
     */
    public Update(int projectID, String apiKey, String currentVersion) {
        this.projectID = projectID;
        this.apiKey = apiKey;
        this.CURRENT_VERSION = currentVersion;
        query();
    }

    /**
     * Query the API to find the latest approved file's details.
     */
    public void query() {
        URL url = null;

        try {
            url = new URL(API_HOST + API_QUERY + projectID);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        try {
            URLConnection conn = url.openConnection();
            if (apiKey != null) {
                conn.addRequestProperty("X-API-Key", apiKey);
            }
            conn.addRequestProperty("User-Agent", "ServerModsAPI-Example (by Gravity)");
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() > 0) {
                JSONObject latest = (JSONObject) array.get(array.size() - 1);
                String versionLink = (String) latest.get(API_LINK_VALUE);
                String versionName = (String) latest.get("name");
                if (!CURRENT_VERSION.equalsIgnoreCase(versionName)) 
                	Main.logger.info("[SCompassTrack] A new version is available at " + versionLink + ".");
                else Main.logger.info("[SCompassTrack] No new version available.");
            } else Main.logger.info("[SCompassTrack] There are no files for this project.");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}

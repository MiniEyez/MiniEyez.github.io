package io.github.minieyez.paramaniacenchants;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
	
	Plugin plugin = ParamaniacEnchants.getPlugin(ParamaniacEnchants.class);
	
	public File playerData;
	public FileConfiguration playerDataConfig;
	
	public FileConfiguration getPlayerDataConfig() {
		return playerDataConfig;
	}
	
	public void savePlayerDataConfig() {
		try {
			playerDataConfig.save(playerData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void reloadPlayerDataConfig() {
		playerDataConfig = YamlConfiguration.loadConfiguration(playerData);
	}

	public void setUp() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		playerData = new File(plugin.getDataFolder(), "playerdata.yml");
		
		if(!playerData.exists()) {
			try {
				playerData.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		playerDataConfig = YamlConfiguration.loadConfiguration(playerData);


		}

}

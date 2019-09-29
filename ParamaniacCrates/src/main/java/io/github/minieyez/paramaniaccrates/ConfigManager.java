package io.github.minieyez.paramaniaccrates;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
	
	
	String fileName;
	
	public File file;
	public FileConfiguration config;
	
	Plugin plugin = ParamaniacCrates.getPlugin(ParamaniacCrates.class);
	
	public ConfigManager(String fileName) {
		this.fileName = fileName;
	}
	
	private void setUp() {
		if(!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		file = new File(plugin.getDataFolder(), fileName);
		
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	public FileConfiguration get() {
		setUp();
		return config;
	}
	
	public void saveConfig() {
		try {
			config.save(file);
			config = YamlConfiguration.loadConfiguration(file);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(file);
	}

}

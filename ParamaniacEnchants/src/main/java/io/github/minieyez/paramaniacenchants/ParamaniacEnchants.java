package io.github.minieyez.paramaniacenchants;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.milkbowl.vault.economy.Economy;

public class ParamaniacEnchants extends JavaPlugin {

	private ConfigManager config;
	
	private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    public ArrayList<Player> debugMode;
	
	@Override
	public void onEnable() {
		getCommand("paramaniacenchants").setExecutor(new CommandManager());
		config = new ConfigManager();
		debugMode = new ArrayList<Player>();
		config.setUp();
		if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		Bukkit.getPluginManager().registerEvents(new EventManager(), this);
		
	}

	@Override
	public void onDisable() {

	}
	
	public void onOffDebugMode(Player p) {
		if(debugMode.contains(p)) {
			debugMode.remove(p);
			return;
		}
		debugMode.add(p);
	}
	
	public void sendDebugMessage(String message) {
		for(Player p : debugMode) {
			p.sendMessage(message);
		}
	}

	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}
	
	public ConfigManager getManager() {
		return this.config;
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public Economy getEcon() {
		return econ;
	}

}

package io.github.minieyez.paramaniaccrates;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ParamaniacCrates extends JavaPlugin{

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		getCommand("crate").setExecutor(new Commands());
	}
}
 